package org.example.csvuploader.job.transform_csv;

import lombok.RequiredArgsConstructor;
import org.example.csvuploader.constant.FileConstants;
import org.example.csvuploader.dto.transform.vibe.VibeCsvReadDto;
import org.example.csvuploader.dto.transform.vibe.VibeCsvWriterDto;
import org.example.csvuploader.tokenizer.CustomDelimitedLineTokenizer;
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

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;

@Configuration
@RequiredArgsConstructor
public class VibeCsvToCsv {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager tx;
    private static final String[] VIBE_COLUMN_NAMES = new String[]{
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

    private static final int[] BASIC_INCLUDE_FIELDS = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 15, 16, 17, 18, 19, 20, 21, 22, 23, 26};
    private static final String HEADER_NAMES = "정산 월,서비스월,업체코드,업체명,기획사코드,기획사명,계약코드," +
            "계약명,채널명,서비스코드,상품코드,서비스명,뮤비여부,무료여부," +
            "곡코드,업체곡코드,UCI,곡명,앨범코드,업체앨범코드,앨범명,아티스트명,히트수,인접권료";


    @Bean("vibeCsvJob")
    public Job vibeCsvJob(Step vibeCsvStep) {
        return new JobBuilder("vibeCsvJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(vibeCsvStep)
                .build();
    }

    @Bean
    @JobScope
    public Step vibeCsvStep(
            ItemReader<VibeCsvReadDto> vibeCsvItemReader,
            ItemProcessor<VibeCsvReadDto, VibeCsvWriterDto> vibeCsvItemProcessor,
            ItemWriter<VibeCsvWriterDto> vibeCsvItemWriter
    ) {
        return new StepBuilder("vibeCsvStep", jobRepository)
                .<VibeCsvReadDto, VibeCsvWriterDto>chunk(100, tx)
                .reader(vibeCsvItemReader)
                .processor(vibeCsvItemProcessor)
                .writer(vibeCsvItemWriter)
                .build();
    }

    @Bean
    public FlatFileItemReader<VibeCsvReadDto> vibeCsvItemReader() {
        DelimitedLineTokenizer tokenizer = new CustomDelimitedLineTokenizer();
        tokenizer.setNames(VIBE_COLUMN_NAMES);
        tokenizer.setIncludedFields(BASIC_INCLUDE_FIELDS);
        tokenizer.setStrict(false);

        DefaultLineMapper<VibeCsvReadDto> lineMapper = getLineMapper(tokenizer);

        return new FlatFileItemReaderBuilder<VibeCsvReadDto>()
                .name("vibeCsvItemReader")
                .resource(new FileSystemResource(FileConstants.getInputFilePath()))
                .encoding(StandardCharsets.UTF_8.name())
                .linesToSkip(1)
                .lineMapper(lineMapper)
                .build();
    }

    private DefaultLineMapper<VibeCsvReadDto> getLineMapper(DelimitedLineTokenizer tokenizer) {
        DefaultLineMapper<VibeCsvReadDto> lineMapper = new DefaultLineMapper<>() {
            private long currentLine = 0;
            private long totalLines = 0;

            @Override
            public VibeCsvReadDto mapLine(String line, int lineNumber) throws Exception {
                if (totalLines == 0) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(FileConstants.getInputFilePath()))) {
                        totalLines = reader.lines().count() - 1;
                    }
                }

                currentLine++;
                if (currentLine == totalLines) {
                    currentLine = 0;
                    totalLines = 0;
                    return null;
                }

                return super.mapLine(line, lineNumber);
            }
        };

        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(new BeanWrapperFieldSetMapper<VibeCsvReadDto>() {{
            setTargetType(VibeCsvReadDto.class);
        }});
        return lineMapper;
    }

    @Bean
    @JobScope
    public ItemProcessor<VibeCsvReadDto, VibeCsvWriterDto> vibeCsvItemProcessor() {
        return VibeCsvWriterDto::of;
    }

    @Bean
    public FlatFileItemWriter<VibeCsvWriterDto> vibeCsvItemWriter() {
        return new FlatFileItemWriterBuilder<VibeCsvWriterDto>()
                .name("vibeCsvItemWriter")
                .resource(new FileSystemResource(FileConstants.getVibeFilePath()))
                .headerCallback(writer -> writer.write(HEADER_NAMES))
                .delimited()
                .delimiter(",")
                .names(VIBE_COLUMN_NAMES)
                .build();
    }
}
