package org.example.csvuploader.job.transform_csv;

import lombok.RequiredArgsConstructor;
import org.example.csvuploader.constant.FileConstants;
import org.example.csvuploader.dto.transform.vibe.VibePlusCsvReadDto;
import org.example.csvuploader.dto.transform.vibe.VibePlusCsvWriterDto;
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
public class VibePlusToCsv {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager tx;

    private static final String[] VIBE_READ_COLUMN_NAMES = new String[]{
            "settlementMonth",
            "serviceMonth",
            "companyCode",
            "companyName",
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
            "albumName",
            "companyAlbumCode",
            "albumName",
            "artistName",
            "hitCount",
            "vpsFinalRights"
    };

    private static final String[] VIBE_WRITER_COLUMN_NAME = new String[]{
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
    private static final int[] INCLUDE_FIELDS = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22};
    private static final String HEADER_NAMES = "정산 월,서비스월,업체코드,업체명,기획사코드,기획사명,계약코드," +
            "계약명,채널명,서비스코드,상품코드,서비스명,뮤비여부,무료여부," +
            "곡코드,업체곡코드,UCI,곡명,앨범코드,업체앨범코드,앨범명,아티스트명,히트수,인접권료";

    @Bean("vibePlusCsvJob")
    public Job vibePlusCsvJob(Step vibePlusCsvStep) {
        return new JobBuilder("vibePlusCsvJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(vibePlusCsvStep)
                .build();
    }

    @Bean
    @JobScope
    public Step vibePlusCsvStep(
            ItemReader<VibePlusCsvReadDto> vibePlusCsvItemReader,
            ItemProcessor<VibePlusCsvReadDto, VibePlusCsvWriterDto> vibePlusItemProcessor,
            ItemWriter<VibePlusCsvWriterDto> vibePlusCsvItemWriter

    ) {
        return new StepBuilder("vibePlusCsvStep", jobRepository)
                .<VibePlusCsvReadDto, VibePlusCsvWriterDto>chunk(100, tx)
                .reader(vibePlusCsvItemReader)
                .processor(vibePlusItemProcessor)
                .writer(vibePlusCsvItemWriter)
                .build();
    }

    @Bean
    @JobScope
    public FlatFileItemReader<VibePlusCsvReadDto> vibePlusCsvItemReader() {
        CustomDelimitedLineTokenizer tokenizer = new CustomDelimitedLineTokenizer();
        tokenizer.setNames(VIBE_READ_COLUMN_NAMES);

        tokenizer.setIncludedFields(INCLUDE_FIELDS);
        tokenizer.setStrict(false);

        return new FlatFileItemReaderBuilder<VibePlusCsvReadDto>()
                .name("vibePlusCsvItemReader")
                .resource(new FileSystemResource(FileConstants.getInputFilePath()))
                .encoding(StandardCharsets.UTF_8.name())
                .lineMapper(getLineMapper(tokenizer))
                .linesToSkip(1)
                .build();
    }

    private DefaultLineMapper<VibePlusCsvReadDto> getLineMapper(DelimitedLineTokenizer tokenizer) {
        DefaultLineMapper<VibePlusCsvReadDto> lineMapper = new DefaultLineMapper<>() {
            private long currentLine = 0;
            private long totalLines = 0;

            @Override
            public VibePlusCsvReadDto mapLine(String line, int lineNumber) throws Exception {

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
        lineMapper.setFieldSetMapper(new BeanWrapperFieldSetMapper<VibePlusCsvReadDto>() {{
            setTargetType(VibePlusCsvReadDto.class);
        }});
        return lineMapper;
    }

    @Bean
    @JobScope
    public ItemProcessor<VibePlusCsvReadDto, VibePlusCsvWriterDto> vibePlusItemProcessor() {
        return VibePlusCsvWriterDto::of;
    }

    @Bean
    public FlatFileItemWriter<VibePlusCsvWriterDto> vibePlusCsvItemWriter() {
        return new FlatFileItemWriterBuilder<VibePlusCsvWriterDto>()
                .name("vibePlusCsvItemWriter")
                .resource(new FileSystemResource(FileConstants.getVibePlusFilePath()))
                .headerCallback(writer -> writer.write(HEADER_NAMES))
                .delimited()
                .delimiter(",")
                .names(VIBE_WRITER_COLUMN_NAME)
                .build();
    }
}
