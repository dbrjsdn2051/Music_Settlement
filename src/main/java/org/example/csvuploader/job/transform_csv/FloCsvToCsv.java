package org.example.csvuploader.job.transform_csv;

import lombok.RequiredArgsConstructor;
import org.example.csvuploader.constant.FileConstants;
import org.example.csvuploader.dto.transform.flo.FloCsvReadDto;
import org.example.csvuploader.dto.transform.flo.FloCsvWriterDto;
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

import java.nio.charset.StandardCharsets;

@Configuration
@RequiredArgsConstructor
public class FloCsvToCsv {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager tx;
    private static final String[] FLO_COLUMN_NAMES = new String[]{
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

    private static final int[] INCLUDE_FIELDS = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 26, 27, 28, 29};
    private static final String HEADER_NAMES = "No,정산 월,판매 월,회사코드,회사명,계약코드,계약명,정산코드,정산코드명," +
            "판매타입,상품타입,대분류,중분류,소분류,앨범코드,앨범명,앨범가수명," +
            "UPC,업체앨범코드,곡코드,곡명,UCI,ISRC,업체곡코드,가수명,기획사명,히트수,정산금액";

    @Bean("floCsvJob")
    public Job floCsvJob(Step floCsvStep) {
        return new JobBuilder("floCsvJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(floCsvStep)
                .build();
    }

    @Bean
    @JobScope
    public Step floCsvStep(
            ItemReader<FloCsvReadDto> floCsvItemReader,
            ItemProcessor<FloCsvReadDto, FloCsvWriterDto> floCsvItemProcessor,
            ItemWriter<FloCsvWriterDto> floCsvItemWriter
    ) {
        return new StepBuilder("floCsvStep", jobRepository)
                .<FloCsvReadDto, FloCsvWriterDto>chunk(100, tx)
                .reader(floCsvItemReader)
                .processor(floCsvItemProcessor)
                .writer(floCsvItemWriter)
                .build();
    }

    @Bean
    public FlatFileItemReader<FloCsvReadDto> floCsvItemReader() {
        DelimitedLineTokenizer tokenizer = new CustomDelimitedLineTokenizer();
        tokenizer.setNames(FLO_COLUMN_NAMES);
        tokenizer.setStrict(false);
        tokenizer.setIncludedFields(INCLUDE_FIELDS);

        DefaultLineMapper<FloCsvReadDto> lineMapper = getLineMapper(tokenizer);

        return new FlatFileItemReaderBuilder<FloCsvReadDto>()
                .name("floCsvItemReader")
                .resource(new FileSystemResource(FileConstants.getInputFilePath()))
                .encoding(StandardCharsets.UTF_8.name())
                .linesToSkip(1)
                .lineMapper(lineMapper)
                .build();
    }

    private DefaultLineMapper<FloCsvReadDto> getLineMapper(DelimitedLineTokenizer tokenizer) {
        DefaultLineMapper<FloCsvReadDto> lineMapper = new DefaultLineMapper<>() {
            @Override
            public FloCsvReadDto mapLine(String line, int lineNumber) throws Exception {
                if (line.contains("합계")) {
                    return null;
                }
                return super.mapLine(line, lineNumber);
            }
        };

        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(new BeanWrapperFieldSetMapper<FloCsvReadDto>() {{
            setTargetType(FloCsvReadDto.class);
        }});
        return lineMapper;
    }

    @Bean
    @JobScope
    public ItemProcessor<FloCsvReadDto, FloCsvWriterDto> floCsvItemProcessor() {
        return FloCsvWriterDto::of;
    }

    @Bean
    public FlatFileItemWriter<FloCsvWriterDto> floCsvItemWriter() {
        return new FlatFileItemWriterBuilder<FloCsvWriterDto>()
                .name("floCsvItemWriter")
                .resource(new FileSystemResource(FileConstants.getFloFilePath()))
                .headerCallback(writer -> writer.write(HEADER_NAMES))
                .delimited()
                .delimiter(",")
                .names(FLO_COLUMN_NAMES)
                .build();
    }
}
