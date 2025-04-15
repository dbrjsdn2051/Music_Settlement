package org.example.csvuploader.job.settlement_csv;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.example.csvuploader.constant.FileConstants;
import org.example.csvuploader.domain.Melon;
import org.example.csvuploader.domain.excluce.MelonExcludeSettlement;
import org.example.csvuploader.dto.settlement.melon.MelonExclusionListReaderDto;
import org.example.csvuploader.dto.settlement.melon.MelonOriginalDataReaderDto;
import org.example.csvuploader.dto.settlement.melon.MelonFilteredDataWriterDto;
import org.example.csvuploader.util.TaskletResultStore;
import org.example.csvuploader.listener.JobListener;
import org.example.csvuploader.tokenizer.MelonTokenizer;
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
public class MelonSettlementJob {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager tx;
    private final EntityManagerFactory emf;
    private final DataSource dataSource;
    private final JobListener jobListener;

    private static final String[] COLUMN_NAMES = new String[]{
            "settlementMonth",
            "saleMonth",
            "songName",
            "songCode",
            "artist",
            "lhSongCode",
            "albumName",
            "albumCode",
            "lhAlbumCode",
            "barcode",
            "mainArtist",
            "releasesDate",
            "agency",
            "label",
            "totalInfoFee",
            "totalSt",
            "totalDl",
            "totalRoyalty"
    };

    private static final int[] READ_INCLUDE_FIELDS = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17};
    private static final String HEADER_NAMES = "정산 월,판매 월,곡명,곡코드,아티스트,LH곡코드," +
            "음반명,음반코드,LH음반코드,바코드,대표아티스트,발매일," +
            "기획사,음반사,정보이용료,ST,DL,저작인접권료";

    @Bean("melonSettlementCsvJob")
    public Job melonSettlementCsvJob(
            Step melonOriginalDataStep,
            Step melonExclusionListStep,
            Step melonFilteredDataStep,
            Step melonCalculateExcludedAmountStep,
            Step melonClearStep
    ) {
        return new JobBuilder("melonSettlementJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(jobListener)
                .start(melonOriginalDataStep)
                .next(melonExclusionListStep)
                .next(melonFilteredDataStep)
                .next(melonCalculateExcludedAmountStep)
                .next(melonClearStep)
                .build();
    }

    @Bean
    @JobScope
    public Step melonOriginalDataStep(
            ItemReader<MelonOriginalDataReaderDto> melonOriginalDataReader,
            ItemProcessor<MelonOriginalDataReaderDto, Melon> melonOriginalDataProcessor,
            ItemWriter<Melon> melonOriginalDataWriter
    ) {
        return new StepBuilder("melonOriginalDataStep", jobRepository)
                .<MelonOriginalDataReaderDto, Melon>chunk(1000, tx)
                .reader(melonOriginalDataReader)
                .processor(melonOriginalDataProcessor)
                .writer(melonOriginalDataWriter)
                .build();
    }

    @Bean
    @JobScope
    public Step melonExclusionListStep(
            ItemReader<MelonExclusionListReaderDto> melonExclusionListReader,
            ItemProcessor<MelonExclusionListReaderDto, MelonExcludeSettlement> melonExclusionListProcessor,
            ItemWriter<MelonExcludeSettlement> melonExclusionListWriter
    ) {
        return new StepBuilder("melonExclusionListStep", jobRepository)
                .<MelonExclusionListReaderDto, MelonExcludeSettlement>chunk(1000, tx)
                .reader(melonExclusionListReader)
                .processor(melonExclusionListProcessor)
                .writer(melonExclusionListWriter)
                .build();
    }

    @Bean
    @JobScope
    public Step melonFilteredDataStep(
            ItemReader<Melon> jdbcCursorItemReader,
            ItemProcessor<Melon, MelonFilteredDataWriterDto> melonFilteredDataProcessor,
            ItemWriter<MelonFilteredDataWriterDto> melonFilteredDataWriter
    ) {
        return new StepBuilder("melonFilteredDataStep", jobRepository)
                .<Melon, MelonFilteredDataWriterDto>chunk(1000, tx)
                .reader(jdbcCursorItemReader)
                .processor(melonFilteredDataProcessor)
                .writer(melonFilteredDataWriter)
                .build();
    }

    @Bean
    @JobScope
    public Step melonCalculateExcludedAmountStep() {
        return new StepBuilder("melonCalculateExcludedAmountStep", jobRepository)
                .tasklet(melonCalculateExcludedAmount(), tx)
                .build();
    }

    @Bean
    @JobScope
    public Step melonClearStep() {
        return new StepBuilder("melonClearStep", jobRepository)
                .tasklet(melonClearTables(), tx)
                .build();
    }

    @Bean
    public FlatFileItemReader<MelonOriginalDataReaderDto> melonOriginalDataReader() {
        DelimitedLineTokenizer tokenizer = new MelonTokenizer();
        tokenizer.setNames(COLUMN_NAMES);
        tokenizer.setIncludedFields(READ_INCLUDE_FIELDS);
        tokenizer.setStrict(false);

        DefaultLineMapper<MelonOriginalDataReaderDto> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
            setTargetType(MelonOriginalDataReaderDto.class);
        }});

        return new FlatFileItemReaderBuilder<MelonOriginalDataReaderDto>()
                .name("melonOriginalDataReader")
                .encoding(StandardCharsets.UTF_8.name())
                .resource(new FileSystemResource(FileConstants.getBaseDirectory() + "/settlement.csv"))
                .linesToSkip(1)
                .lineMapper(lineMapper)
                .build();
    }

    @Bean
    public ItemProcessor<MelonOriginalDataReaderDto, Melon> melonOriginalDataProcessor() {
        return MelonOriginalDataReaderDto::from;
    }

    @Bean
    public JpaItemWriter<Melon> melonOriginalDataWriter() {
        JpaItemWriter<Melon> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(emf);
        return writer;
    }

    @Bean
    public FlatFileItemReader<MelonExclusionListReaderDto> melonExclusionListReader() {
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames(new String[]{"trackCode", "memberShip"});
        tokenizer.setIncludedFields(new int[]{0, 1});
        tokenizer.setStrict(false);

        DefaultLineMapper<MelonExclusionListReaderDto> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
            setTargetType(MelonExclusionListReaderDto.class);
        }});

        return new FlatFileItemReaderBuilder<MelonExclusionListReaderDto>()
                .name("melonExclusionListReader")
                .encoding(StandardCharsets.UTF_8.name())
                .linesToSkip(1)
                .resource(new FileSystemResource(FileConstants.getBaseDirectory() + "/externalFile.csv"))
                .lineMapper(lineMapper)
                .build();
    }

    @Bean
    public ItemProcessor<MelonExclusionListReaderDto, MelonExcludeSettlement> melonExclusionListProcessor() {
        return MelonExclusionListReaderDto::from;
    }

    @Bean
    public JpaItemWriter<MelonExcludeSettlement> melonExclusionListWriter() {
        JpaItemWriter<MelonExcludeSettlement> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(emf);
        return writer;
    }

    @Bean
    public JdbcCursorItemReader<Melon> jdbcCursorItemReader() {
        return new JdbcCursorItemReaderBuilder<Melon>()
                .name("jdbcCursorItemReader")
                .dataSource(dataSource)
                .sql("SELECT * FROM melon m " +
                        "WHERE NOT EXISTS " +
                        "(" +
                        "SELECT 1 FROM melon_exclude_settlement s " +
                        "WHERE m.lh_song_code = s.track_code" +
                        ") " +
                        "ORDER BY m.id")
                .beanRowMapper(Melon.class)
                .fetchSize(1000)
                .build();
    }

    @Bean
    public ItemProcessor<Melon, MelonFilteredDataWriterDto> melonFilteredDataProcessor() {
        return MelonFilteredDataWriterDto::of;
    }

    @Bean
    public FlatFileItemWriter<MelonFilteredDataWriterDto> melonFilteredDataWriter() {
        return new FlatFileItemWriterBuilder<MelonFilteredDataWriterDto>()
                .name("melonFilteredDataWriter")
                .encoding(StandardCharsets.UTF_8.name())
                .resource(new FileSystemResource(FileConstants.getMelonFilePath()))
                .headerCallback(writer -> writer.write(HEADER_NAMES))
                .delimited().delimiter(",")
                .names(COLUMN_NAMES)
                .build();
    }

    @Bean
    public Tasklet melonCalculateExcludedAmount() {
        return (contribution, chunkContext) -> {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            Double result = jdbcTemplate.queryForObject(
                    "SELECT SUM(m.total_royalty) FROM melon m " +
                            "INNER JOIN melon_exclude_settlement s " +
                            "ON m.lh_song_code = s.track_code", Double.class);
            TaskletResultStore.setResult(result);
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Tasklet melonClearTables() {
        return (contribution, chunkContext) -> {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            jdbcTemplate.update("DELETE FROM melon");
            jdbcTemplate.update("DELETE FROM melon_exclude_settlement");
            return RepeatStatus.FINISHED;
        };
    }
}