package org.example.csvuploader.job.settlement_csv;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.example.csvuploader.constant.FileConstants;
import org.example.csvuploader.domain.Genie;
import org.example.csvuploader.dto.settlement.genie.GenieFilteredDataWriterDto;
import org.example.csvuploader.dto.settlement.genie.GenieOriginalDataReaderDto;
import org.example.csvuploader.util.TaskletResultStore;
import org.example.csvuploader.tokenizer.CustomDelimitedLineTokenizer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;

@Configuration
@RequiredArgsConstructor
public class GenieSettlementJob {

    private final DataSource dataSource;
    private final EntityManagerFactory emf;
    private final PlatformTransactionManager tx;
    private final JobRepository jobRepository;
    private final Step commonExclusionListStep;

    private static final String[] COLUMN_NAMES = new String[]{
            "settlementMonth",
            "saleMonth",
            "songName",
            "albumName",
            "artist",
            "lpName",
            "songLid",
            "albumLid",
            "songExternalCode",
            "albumExternalCode",
            "dn",
            "st",
            "salesAmount",
            "neighboringRightFee",
            "neighboringRightFee",
            "copyrightFee",
            "performanceRightFee"
    };

    private static final int[] READ_INCLUDE_FIELDS = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};

    private static final String HEADER_NAMES = "정산 월,판매 월,곡명,음반명,아티스트명," +
            "LP명,곡LID,음반LID,곡외부코드,음반외부코드,DN," +
            "ST,판매금액,정산금액,인접권료,저작권료,실연권료";

    @Bean("genieSettlementCsvJob")
    public Job genieSettlementCsvJob(
            Step genieOriginalDataStep,
            Step genieFilteredDataStep,
            Step genieCalculateExcludedAmountStep,
            Step genieClearStep
    ) {
        return new JobBuilder("genieSettlementCsvJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(genieOriginalDataStep)
                .next(commonExclusionListStep)
                .next(genieFilteredDataStep)
                .next(genieCalculateExcludedAmountStep)
                .next(genieClearStep)
                .build();
    }

    @Bean
    @JobScope
    public Step genieOriginalDataStep(
            ItemReader<GenieOriginalDataReaderDto> genieOriginalDataReader,
            ItemProcessor<GenieOriginalDataReaderDto, Genie> genieOriginalDataProcessor,
            ItemWriter<Genie> genieJpaItemWriter
    ) {
        return new StepBuilder("genieOriginalDataStep", jobRepository)
                .<GenieOriginalDataReaderDto, Genie>chunk(1000, tx)
                .reader(genieOriginalDataReader)
                .processor(genieOriginalDataProcessor)
                .writer(genieJpaItemWriter)
                .build();
    }

    @Bean
    @JobScope
    public Step genieFilteredDataStep(
            ItemReader<Genie> genieFilteredDataReader,
            ItemProcessor<Genie, GenieFilteredDataWriterDto> genieFilteredDataProcessor,
            ItemWriter<GenieFilteredDataWriterDto> genieFilteredDataWriter
    ) {
        return new StepBuilder("genieFilteredDataStep", jobRepository)
                .<Genie, GenieFilteredDataWriterDto>chunk(1000, tx)
                .reader(genieFilteredDataReader)
                .processor(genieFilteredDataProcessor)
                .writer(genieFilteredDataWriter)
                .build();
    }

    @Bean
    @JobScope
    public Step genieCalculateExcludedAmountStep() {
        return new StepBuilder("genieCalculateExcludedAmountStep", jobRepository)
                .tasklet(genieCalculateExcludedAmount(), tx)
                .build();
    }

    @Bean
    @JobScope
    public Step genieClearStep() {
        return new StepBuilder("genieClearStep", jobRepository)
                .tasklet(genieClearTables(), tx)
                .build();
    }

    @Bean
    public FlatFileItemReader<GenieOriginalDataReaderDto> genieOriginalDataReader() {
        DelimitedLineTokenizer tokenizer = new CustomDelimitedLineTokenizer();
        tokenizer.setNames(COLUMN_NAMES);
        tokenizer.setIncludedFields(READ_INCLUDE_FIELDS);
        tokenizer.setStrict(false);

        DefaultLineMapper<GenieOriginalDataReaderDto> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
            setTargetType(GenieOriginalDataReaderDto.class);
        }});

        return new FlatFileItemReaderBuilder<GenieOriginalDataReaderDto>()
                .name("genieOriginalDataReader")
                .encoding(StandardCharsets.UTF_8.name())
                .resource(new FileSystemResource(FileConstants.getBaseDirectory() + "/settlement.csv"))
                .linesToSkip(1)
                .lineMapper(lineMapper)
                .build();
    }

    @Bean
    public ItemProcessor<GenieOriginalDataReaderDto, Genie> genieOriginalDataProcessor() {
        return GenieOriginalDataReaderDto::from;
    }

    @Bean
    public JpaItemWriter<Genie> genieJpaItemWriter() {
        JpaItemWriter<Genie> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(emf);
        return writer;
    }

    @Bean
    public JdbcCursorItemReader<Genie> genieFilteredDataReader() {
        return new JdbcCursorItemReaderBuilder<Genie>()
                .name("genieFilteredDataWriter")
                .dataSource(dataSource)
                .sql("SELECT * FROM genie g " +
                        "WHERE NOT EXISTS " +
                        "(" +
                        "SELECT 1 FROM common_exclude_settlement c " +
                        "WHERE g.song_external_code = c.track_code " +
                        "OR g.album_external_code = c.song_code" +
                        ") " +
                        "ORDER BY g.id")
                .beanRowMapper(Genie.class)
                .fetchSize(1000)
                .build();
    }

    @Bean
    public ItemProcessor<Genie, GenieFilteredDataWriterDto> genieFilteredDataProcessor() {
        return GenieFilteredDataWriterDto::of;
    }

    @Bean
    public FlatFileItemWriter<GenieFilteredDataWriterDto> genieFilteredDataWriter() {
        return new FlatFileItemWriterBuilder<GenieFilteredDataWriterDto>()
                .name("genieFilteredDataWriter")
                .encoding(StandardCharsets.UTF_8.name())
                .resource(new FileSystemResource(FileConstants.getGenieFilePath()))
                .headerCallback(writer -> writer.write(HEADER_NAMES))
                .delimited().delimiter(",")
                .names(COLUMN_NAMES)
                .build();
    }

    @Bean
    public Tasklet genieCalculateExcludedAmount() {
        return (contribution, chunkContext) -> {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            Double result = jdbcTemplate.queryForObject(
                    """
                            SELECT SUM(neighboring_right_fee + 0.0) AS total_fee
                            FROM (
                                 SELECT g.neighboring_right_fee
                                 FROM genie g
                                 JOIN common_exclude_settlement c
                                 ON g.song_external_code = c.track_code 
                                 UNION ALL
                                 SELECT g.neighboring_right_fee
                                 FROM genie g
                                 JOIN common_exclude_settlement c
                                 ON g.album_external_code = c.song_code
                             ) AS combined_results
                            """,
                    Double.class);
            TaskletResultStore.setResult(result);
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Tasklet genieClearTables() {
        return (contribution, chunkContext) -> {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            jdbcTemplate.update("DELETE FROM genie");
            jdbcTemplate.update("DELETE FROM common_exclude_settlement");
            return RepeatStatus.FINISHED;
        };
    }
}
