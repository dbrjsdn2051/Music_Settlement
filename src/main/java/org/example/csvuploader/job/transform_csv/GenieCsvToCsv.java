package org.example.csvuploader.job.transform_csv;

import lombok.RequiredArgsConstructor;
import org.example.csvuploader.constant.FileConstants;
import org.example.csvuploader.dto.transform.genie.GenieCsvReadDto;
import org.example.csvuploader.dto.transform.genie.GenieCsvWriterDto;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.nio.charset.StandardCharsets;

@Configuration
@RequiredArgsConstructor
public class GenieCsvToCsv {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager tx;
    private static final String[] READ_COLUMN_NAMES = new String[]{
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
            "copyrightFee",
            "performanceRightFee"
    };

    private static final String[] WRITER_COLUMN_NAMES = new String[]{
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
    private static final int[] INCLUDE_FIELDS = new int[]{0, 1, 2, 3, 4, 5, 7, 8, 9, 10, 11, 13, 14, 15};
    private static final String HEADER_NAMES = "정산 월,판매 월,곡명,음반명,아티스트명," +
            "LP명,곡LID,음반LID,곡외부코드,음반외부코드,DN," +
            "ST,판매금액,정산금액,인접권료,저작권료,실연권료";

    @Bean("genieCsvJob")
    public Job genieCsvJob(Step genieCsvStep) {
        return new JobBuilder("genieCsvJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(genieCsvStep)
                .build();
    }

    @Bean
    @JobScope
    public Step genieCsvStep(
            ItemReader<GenieCsvReadDto> genieCsvItemReader,
            ItemProcessor<GenieCsvReadDto, GenieCsvWriterDto> addMonthProcess,
            ItemWriter<GenieCsvWriterDto> genieCsvItemWriter
    ) {
        return new StepBuilder("genieCsvStep", jobRepository)
                .<GenieCsvReadDto, GenieCsvWriterDto>chunk(100, tx)
                .reader(genieCsvItemReader)
                .processor(addMonthProcess)
                .writer(genieCsvItemWriter)
                .build();
    }

    @Bean
    public FlatFileItemReader<GenieCsvReadDto> genieCsvItemReader() {
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();

        tokenizer.setNames(READ_COLUMN_NAMES);
        tokenizer.setIncludedFields(INCLUDE_FIELDS);
        tokenizer.setStrict(true);

        DefaultLineMapper<GenieCsvReadDto> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(new BeanWrapperFieldSetMapper<GenieCsvReadDto>() {{
            setTargetType(GenieCsvReadDto.class);
        }});

        return new FlatFileItemReaderBuilder<GenieCsvReadDto>()
                .name("genieCsvItemReader")
                .resource(new FileSystemResource(FileConstants.getInputFilePath()))
                .encoding("CP949")
                .linesToSkip(1)
                .lineMapper(lineMapper)
                .build();
    }

    @Bean
    @JobScope
    public ItemProcessor<GenieCsvReadDto, GenieCsvWriterDto> addMonthProcess(
    ) {
        return GenieCsvWriterDto::of;
    }

    @Bean
    public FlatFileItemWriter<GenieCsvWriterDto> genieCsvItemWriter() {
        return new FlatFileItemWriterBuilder<GenieCsvWriterDto>()
                .name("genieCsvItemWriter")
                .resource(new FileSystemResource(FileConstants.getGenieFilePath()))
                .headerCallback(writer -> writer.write(HEADER_NAMES))
                .encoding(StandardCharsets.UTF_8.name())
                .delimited()
                .delimiter(",")
                .names(WRITER_COLUMN_NAMES)
                .build();
    }
}
