package org.example.csvuploader.job.segment;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.example.csvuploader.constant.FileConstants;
import org.example.csvuploader.domain.Youtube;
import org.example.csvuploader.dto.segment.YoutubeSegmentInsertReaderDto;
import org.example.csvuploader.dto.segment.YoutubeSegmentWriterDto;
import org.example.csvuploader.item_writer.MultiFileItemWriter;
import org.example.csvuploader.tokenizer.CustomDelimitedLineTokenizer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
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
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class YoutubeSegmentJob {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager tx;
    private final DataSource dataSource;
    private final EntityManagerFactory emf;
    private final MultiFileItemWriter multiFileItemWriter;

    private static final String[] COLUMN_NAMES = new String[]{
            "adjustmentType",
            "country",
            "dayValue",
            "videoId",
            "videoChannelId",
            "assetId",
            "assetChannelId",
            "assetTitle",
            "assetLabels",
            "assetType",
            "customId",
            "isrc",
            "upc",
            "grid",
            "artist",
            "album",
            "label",
            "claimType",
            "contentType",
            "offer",
            "ownedViews",
            "monetizedViewsAudio",
            "monetizedViewsAudioVisual",
            "monetizedViews",
            "youTubeRevenueSplit",
            "partnerRevenueProRata",
            "partnerRevenuePerSubMin",
            "partnerRevenue",
            "exchangeRate",
            "partnerRevenueKrw",
            "settlementRate",
            "performanceRightFee",
            "finalSettlementAmount"
    };

    private static final String HEADER_NAMES =
            "Adjustment Type,Country,Day,Video ID,Video Channel ID,Asset ID,Asset Channel ID,Asset Title," +
                    "Asset Labels,Asset Type,Custom ID,ISRC,UPC,GRID,Artist,Album,Label,Claim Type,Content Type,Offer," +
                    "Owned Views,Monetized Views : Audio,Monetized Views : Audio Visual,Monetized Views," +
                    "YouTube Revenue Split,Partner Revenue : Pro Rata,Partner Revenue : Per Sub Min,Partner Revenue,환율," +
                    "Partner Revenue (KRW),정산요율,실연권요율,최종 정산금";

    @Bean("youtubeSegmentFileJob")
    public Job youtubeSegmentFileJob(
            Step youtubeOriginalFileStep,
            Step youtubeSegmentFileStep,
            Step youtubeClearTableStep
    ) {
        return new JobBuilder("youtubeClearTableStep", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(youtubeOriginalFileStep)
                .next(youtubeSegmentFileStep)
                .next(youtubeClearTableStep)
                .build();
    }

    @Bean
    @JobScope
    public Step youtubeOriginalFileStep(
            ItemReader<YoutubeSegmentInsertReaderDto> youtubeOriginalFileItemReader,
            ItemProcessor<YoutubeSegmentInsertReaderDto, Youtube> youtubeOriginalFileItemProcessor,
            ItemWriter<Youtube> youtubeOriginalFileItemWriter
    ) {
        return new StepBuilder("youtubeOriginalFileStep", jobRepository)
                .<YoutubeSegmentInsertReaderDto, Youtube>chunk(1000, tx)
                .reader(youtubeOriginalFileItemReader)
                .processor(youtubeOriginalFileItemProcessor)
                .writer(youtubeOriginalFileItemWriter)
                .build();
    }

    @Bean
    @JobScope
    public Step youtubeSegmentFileStep(
            ItemReader<Youtube> youtubeSegmentFileItemReader,
            ItemProcessor<Youtube, YoutubeSegmentWriterDto> youtubeSegmentFileItemProcessor
    ) {
        return new StepBuilder("youtubeSegmentFileStep", jobRepository)
                .<Youtube, YoutubeSegmentWriterDto>chunk(80000, tx)
                .reader(youtubeSegmentFileItemReader)
                .processor(youtubeSegmentFileItemProcessor)
                .writer(multiFileItemWriter)
                .build();
    }

    @Bean
    @JobScope
    public Step youtubeClearTableStep() {
        return new StepBuilder("youtubeClearTableStep", jobRepository)
                .tasklet(youtubeClearTables(), tx)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<YoutubeSegmentInsertReaderDto> youtubeOriginalFileItemReader(
            @Value("#{jobParameters['fileIndex']}") String fileIndex
    ) {
        CustomDelimitedLineTokenizer tokenizer = new CustomDelimitedLineTokenizer();
        tokenizer.setNames(COLUMN_NAMES);
        tokenizer.setDelimiter(",");
        tokenizer.setStrict(false);

        DefaultLineMapper<YoutubeSegmentInsertReaderDto> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
            setTargetType(YoutubeSegmentInsertReaderDto.class);
        }});

        return new FlatFileItemReaderBuilder<YoutubeSegmentInsertReaderDto>()
                .name("youtubeOriginalFileItemReader")
                .linesToSkip(1)
                .resource(new FileSystemResource(FileConstants.getBaseDirectory() + File.separator + "segment_" + fileIndex + ".csv")) // TODO : File Input Path
                .lineMapper(lineMapper)
                .build();
    }

    @Bean
    public ItemProcessor<YoutubeSegmentInsertReaderDto, Youtube> youtubeOriginalFileItemProcessor() {
        return YoutubeSegmentInsertReaderDto::from;
    }

    @Bean
    public JpaItemWriter<Youtube> youtubeOriginalFileItemWriter() {
        JpaItemWriter<Youtube> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(emf);
        return writer;
    }

    @Bean
    public JdbcCursorItemReader<Youtube> youtubeSegmentFileItemReader() {
        return new JdbcCursorItemReaderBuilder<Youtube>()
                .name("youtubeSegmentFileItemReader")
                .dataSource(dataSource)
                .sql("select * from youtube")
                .beanRowMapper(Youtube.class)
                .fetchSize(80000)
                .build();
    }

    @Bean
    public ItemProcessor<Youtube, YoutubeSegmentWriterDto> youtubeSegmentFileItemProcessor() {
        return YoutubeSegmentWriterDto::of;
    }

    @Bean
    public Tasklet youtubeClearTables() {
        return (contribution, chunkContext) -> {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            jdbcTemplate.update("DELETE FROM youtube");
            return RepeatStatus.FINISHED;
        };
    }
}
