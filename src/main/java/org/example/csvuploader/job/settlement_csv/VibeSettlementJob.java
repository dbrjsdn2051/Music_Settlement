package org.example.csvuploader.job.settlement_csv;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.example.csvuploader.constant.FileConstants;
import org.example.csvuploader.domain.Vibe;
import org.example.csvuploader.dto.settlement.vibe.VibeFilteredDataWriterDto;
import org.example.csvuploader.dto.settlement.vibe.VibeOriginalDataReaderDto;
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
public class VibeSettlementJob {

    private final JobRepository jobRepository;
    private final EntityManagerFactory emf;
    private final PlatformTransactionManager tx;
    private final DataSource dataSource;
    private final Step commonExclusionListStep;

    private static final String[] COLUMN_NAMES = new String[]{
            "settlementMonth",
            "serviceMonth",
            "companyCode",
            "companyName",
            "agencyCode",
            "agencyName",
            "contractCode",
            "contractName",
            "channelName",
            "serviceCode",
            "productCode",
            "serviceName",
            "hasMusicVideo",
            "isFree",
            "songCode",
            "companySongCode",
            "uci",
            "songName",
            "albumCode",
            "companyAlbumCode",
            "albumName",
            "artistName",
            "hitCount",
            "vpsFinalRights"
    };

    private static final int[] READ_INCLUDE_FIELDS = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23};

    private static final String HEADER_NAMES = "정산 월,서비스월,업체코드,업체명,기획사코드,기획사명,계약코드," +
            "계약명,채널명,서비스코드,상품코드,서비스명,뮤비여부,무료여부," +
            "곡코드,업체곡코드,UCI,곡명,앨범코드,업체앨범코드,앨범명,아티스트명,히트수,인접권료";

    @Bean("vibeSettlementCsvJob")
    public Job vibeSettlementCsvJob(
            Step vibeOriginalDataStep,
            Step vibeFilteredDataStep,
            Step vibeCalculateExcludedAmountStep,
            Step vibeClearStep
    ) {
        return new JobBuilder("vibeSettlementJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(vibeOriginalDataStep)
                .next(commonExclusionListStep)
                .next(vibeFilteredDataStep)
                .next(vibeCalculateExcludedAmountStep)
                .next(vibeClearStep)
                .build();
    }

    @Bean
    @JobScope
    public Step vibeOriginalDataStep(
            ItemReader<VibeOriginalDataReaderDto> vibeOriginalDataReader,
            ItemProcessor<VibeOriginalDataReaderDto, Vibe> vibeOriginalDataProcessor,
            ItemWriter<Vibe> vibeJpaItemWriter
    ) {
        return new StepBuilder("vibeOriginalDataStep", jobRepository)
                .<VibeOriginalDataReaderDto, Vibe>chunk(1000, tx)
                .reader(vibeOriginalDataReader)
                .processor(vibeOriginalDataProcessor)
                .writer(vibeJpaItemWriter)
                .build();
    }

    @Bean
    @JobScope
    public Step vibeFilteredDataStep(
            ItemReader<Vibe> vibeFilteredDataReader,
            ItemProcessor<Vibe, VibeFilteredDataWriterDto> vibeFilteredDataProcessor,
            ItemWriter<VibeFilteredDataWriterDto> vibeFilteredDataWriter
    ) {
        return new StepBuilder("vibeFilteredDataStep", jobRepository)
                .<Vibe, VibeFilteredDataWriterDto>chunk(1000, tx)
                .reader(vibeFilteredDataReader)
                .processor(vibeFilteredDataProcessor)
                .writer(vibeFilteredDataWriter)
                .build();
    }

    @Bean
    @JobScope
    public Step vibeCalculateExcludedAmountStep() {
        return new StepBuilder("vibeCalculateExcludedAmountStep", jobRepository)
                .tasklet(vibeCalculateExcludedAmount(), tx)
                .build();
    }

    @Bean
    @JobScope
    public Step vibeClearStep() {
        return new StepBuilder("vibeClearStep", jobRepository)
                .tasklet(vibeClearTables(), tx)
                .build();
    }

    @Bean
    public FlatFileItemReader<VibeOriginalDataReaderDto> vibeOriginalDataReader() {
        DelimitedLineTokenizer tokenizer = new CustomDelimitedLineTokenizer();
        tokenizer.setNames(COLUMN_NAMES);
        tokenizer.setIncludedFields(READ_INCLUDE_FIELDS);
        tokenizer.setStrict(false);

        DefaultLineMapper<VibeOriginalDataReaderDto> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
            setTargetType(VibeOriginalDataReaderDto.class);
        }});

        return new FlatFileItemReaderBuilder<VibeOriginalDataReaderDto>()
                .name("vibeOriginalDataReader")
                .encoding(StandardCharsets.UTF_8.name())
                .resource(new FileSystemResource(FileConstants.getBaseDirectory() + "/settlement.csv"))
                .linesToSkip(1)
                .lineMapper(lineMapper)
                .build();
    }

    @Bean
    public ItemProcessor<VibeOriginalDataReaderDto, Vibe> vibeOriginalDataProcessor() {
        return VibeOriginalDataReaderDto::from;
    }

    @Bean
    public JpaItemWriter<Vibe> vibeJpaItemWriter() {
        JpaItemWriter<Vibe> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(emf);
        return writer;
    }

    @Bean
    public JdbcCursorItemReader<Vibe> vibeFilteredDataReader() {
        return new JdbcCursorItemReaderBuilder<Vibe>()
                .name("vibeFilteredDataReader")
                .dataSource(dataSource)
                .sql("SELECT * FROM vibe v WHERE NOT EXISTS " +
                        "(" +
                        "SELECT 1 FROM common_exclude_settlement c " +
                        "WHERE v.company_song_code = c.track_code " +
                        "OR v.company_album_code = c.song_code" +
                        ")" +
                        " ORDER BY v.id")
                .beanRowMapper(Vibe.class)
                .fetchSize(1000)
                .build();
    }

    @Bean
    public ItemProcessor<Vibe, VibeFilteredDataWriterDto> vibeFilteredDataProcessor() {
        return VibeFilteredDataWriterDto::of;
    }

    @Bean
    public FlatFileItemWriter<VibeFilteredDataWriterDto> vibeFilteredDataWriter() {
        return new FlatFileItemWriterBuilder<VibeFilteredDataWriterDto>()
                .name("vibeFilteredDataWriter")
                .encoding(StandardCharsets.UTF_8.name())
                .resource(new FileSystemResource(FileConstants.getVibeFilePath()))
                .headerCallback(writer -> writer.write(HEADER_NAMES))
                .delimited().delimiter(",")
                .names(COLUMN_NAMES)
                .build();
    }

    @Bean
    public Tasklet vibeCalculateExcludedAmount() {
        return (contribution, chunkContext) -> {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            Double result = jdbcTemplate.queryForObject(
                    """
                            SELECT SUM(vps_final_rights + 0.0) AS total_fee
                            FROM (
                            SELECT v.vps_final_rights
                            FROM vibe v
                            JOIN common_exclude_settlement c
                            ON v.company_song_code = c.track_code
                                                       
                            UNION ALL
                                                       
                            SELECT v.vps_final_rights
                            FROM vibe v
                            JOIN common_exclude_settlement c
                            ON v.company_album_code = c.song_code
                            ) AS combined_results""", Double.class);
            TaskletResultStore.setResult(result);
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Tasklet vibeClearTables() {
        return (contribution, chunkContext) -> {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            jdbcTemplate.update("DELETE FROM vibe");
            jdbcTemplate.update("DELETE FROM common_exclude_settlement");
            return RepeatStatus.FINISHED;
        };
    }
}
