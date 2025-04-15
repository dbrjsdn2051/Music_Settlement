package org.example.csvuploader.job.settlement_csv;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.example.csvuploader.constant.FileConstants;
import org.example.csvuploader.domain.Flo;
import org.example.csvuploader.dto.settlement.flo.FloFilteredDataWriterDto;
import org.example.csvuploader.dto.settlement.flo.FloOriginalDataReaderDto;
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
public class FloSettlementJob {

    private final JobRepository jobRepository;
    private final EntityManagerFactory emf;
    private final PlatformTransactionManager tx;
    private final DataSource dataSource;
    private final Step commonExclusionListStep;

    private static final String[] COLUMN_NAMES = new String[]{
            "no",
            "settlementMonth",
            "salesMonth",
            "companyCode",
            "companyName",
            "contractCode",
            "contractName",
            "settlementCode",
            "settlementCodeName",
            "salesType",
            "productType",
            "mainCategory",
            "subCategory",
            "detailCategory",
            "albumCode",
            "albumName",
            "albumArtistName",
            "upc",
            "companyAlbumCode",
            "songCode",
            "songName",
            "uci",
            "isrc",
            "companySongCode",
            "artistName",
            "agencyName",
            "hitCount",
            "settlementAmount"
    };
    private static final int[] INCLUDE_FIELDS = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27};
    private static final String HEADER_NAMES = "No,정산 월,판매 월,회사코드,회사명,계약코드,계약명,정산코드,정산코드명," +
            "판매타입,상품타입,대분류,중분류,소분류,앨범코드,앨범명,앨범가수명," +
            "UPC,업체앨범코드,곡코드,곡명,UCI,ISRC,업체곡코드,가수명,기획사명,히트수,정산금액";

    @Bean("floSettlementCsvJob")
    public Job floSettlementCsvJob(
            Step floOriginalDataStep,
            Step floFilteredDataStep,
            Step floCalculatedExcludedAmountStep,
            Step floClearStep
    ) {
        return new JobBuilder("floSettlementCsvJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(floOriginalDataStep)
                .next(commonExclusionListStep)
                .next(floFilteredDataStep)
                .next(floCalculatedExcludedAmountStep)
                .next(floClearStep)
                .build();
    }

    @Bean
    @JobScope
    public Step floOriginalDataStep(
            ItemReader<FloOriginalDataReaderDto> floOriginalDataReader,
            ItemProcessor<FloOriginalDataReaderDto, Flo> floOriginalDataProcessor,
            ItemWriter<Flo> floJpaItemWriter
    ) {
        return new StepBuilder("floOriginalDataStep", jobRepository)
                .<FloOriginalDataReaderDto, Flo>chunk(1000, tx)
                .reader(floOriginalDataReader)
                .processor(floOriginalDataProcessor)
                .writer(floJpaItemWriter)
                .build();
    }

    @Bean
    @JobScope
    public Step floFilteredDataStep(
            ItemReader<Flo> floFilteredDataReader,
            ItemProcessor<Flo, FloFilteredDataWriterDto> floFilteredDataProcessor,
            ItemWriter<FloFilteredDataWriterDto> floFilteredDataWriter
    ) {
        return new StepBuilder("floFilteredDataStep", jobRepository)
                .<Flo, FloFilteredDataWriterDto>chunk(1000, tx)
                .reader(floFilteredDataReader)
                .processor(floFilteredDataProcessor)
                .writer(floFilteredDataWriter)
                .build();
    }

    @Bean
    @JobScope
    public Step floCalculatedExcludedAmountStep() {
        return new StepBuilder("floCalculatedExcludedAmountStep", jobRepository)
                .tasklet(floCalculateExcludeAmount(), tx)
                .build();
    }

    @Bean
    @JobScope
    public Step floClearStep() {
        return new StepBuilder("floClearStep", jobRepository)
                .tasklet(floClearTables(), tx)
                .build();
    }

    @Bean
    public FlatFileItemReader<FloOriginalDataReaderDto> floOriginalDataReader() {
        DelimitedLineTokenizer tokenizer = new CustomDelimitedLineTokenizer();
        tokenizer.setNames(COLUMN_NAMES);
        tokenizer.setIncludedFields(INCLUDE_FIELDS);
        tokenizer.setStrict(false);

        DefaultLineMapper<FloOriginalDataReaderDto> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
            setTargetType(FloOriginalDataReaderDto.class);
        }});

        return new FlatFileItemReaderBuilder<FloOriginalDataReaderDto>()
                .name("floOriginalDataReader")
                .encoding(StandardCharsets.UTF_8.name())
                .resource(new FileSystemResource(FileConstants.getBaseDirectory() + "/settlement.csv"))
                .linesToSkip(1)
                .lineMapper(lineMapper)
                .build();
    }

    @Bean
    public ItemProcessor<FloOriginalDataReaderDto, Flo> floOriginalDataProcessor() {
        return FloOriginalDataReaderDto::from;
    }

    @Bean
    public JpaItemWriter<Flo> floJpaItemWriter() {
        JpaItemWriter<Flo> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(emf);
        return writer;
    }

    @Bean
    public JdbcCursorItemReader<Flo> floFilteredDataReader() {
        return new JdbcCursorItemReaderBuilder<Flo>()
                .name("floFilteredDataReader")
                .dataSource(dataSource)
                .sql("SELECT * FROM flo f " +
                        "WHERE NOT EXISTS " +
                        "(" +
                        "SELECT 1 FROM common_exclude_settlement c " +
                        "WHERE f.company_song_code = c.track_code " +
                        "OR f.company_album_code = c.song_code" +
                        ")" +
                        " ORDER BY f.id")
                .beanRowMapper(Flo.class)
                .fetchSize(1000)
                .build();
    }

    @Bean
    public ItemProcessor<Flo, FloFilteredDataWriterDto> floFilteredDataProcessor() {
        return FloFilteredDataWriterDto::of;
    }

    @Bean
    public FlatFileItemWriter<FloFilteredDataWriterDto> floFilteredDataWriter() {
        return new FlatFileItemWriterBuilder<FloFilteredDataWriterDto>()
                .name("floFilteredDataWriter")
                .encoding(StandardCharsets.UTF_8.name())
                .resource(new FileSystemResource(FileConstants.getFloFilePath()))
                .headerCallback(writer -> writer.write(HEADER_NAMES))
                .delimited().delimiter(",")
                .names(COLUMN_NAMES)
                .build();
    }

    @Bean
    public Tasklet floCalculateExcludeAmount() {
        return (contribution, chunkContext) -> {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            Double result = jdbcTemplate.queryForObject(
                    """
                            SELECT SUM(settlement_amount + 0.0) AS total_fee
                            FROM (
                            SELECT f.settlement_amount
                            FROM flo f
                            JOIN common_exclude_settlement c 
                            ON f.company_song_code = c.track_code
                                                        
                            UNION ALL
                                                        
                            SELECT f.settlement_amount
                            FROM flo f
                            JOIN common_exclude_settlement c
                            ON f.company_album_code = c.song_code
                            ) AS combined_results
                            """
                    , Double.class);
            TaskletResultStore.setResult(result);
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Tasklet floClearTables() {
        return (contribution, chunkContext) -> {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            jdbcTemplate.update("DELETE FROM flo");
            jdbcTemplate.update("DELETE FROM common_exclude_settlement");
            return RepeatStatus.FINISHED;
        };
    }
}
