package org.example.csvuploader.job.transform_csv;

import lombok.RequiredArgsConstructor;
import org.example.csvuploader.constant.FileConstants;
import org.example.csvuploader.dto.transform.melon.MelonCsvReadDto;
import org.example.csvuploader.dto.transform.melon.MelonCsvWriterDto;
import org.example.csvuploader.tokenizer.MelonTokenizer;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.nio.charset.StandardCharsets;

@Configuration
@RequiredArgsConstructor
public class MelonCsvToCsv {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager tx;
    private static final String[] READ_COLUMN_NAMES = new String[]{
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

    private static final String[] WRITER_COLUMN_NAMES = new String[]{
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

    private static final int[] INCLUDE_FIELDS = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
    private static final String HEADER_NAMES = "정산 월,판매 월,곡명,곡코드,아티스트,LH곡코드," +
                                               "음반명,음반코드,LH음반코드,바코드,대표아티스트,발매일," +
                                               "기획사,음반사,정보이용료,ST,DL,저작인접권료";

    @Bean("melonCsvJob")
    public Job melonCsvJob(Step melonCsvStep) {
        return new JobBuilder("melonCsvJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(melonCsvStep)
                .build();
    }

    @Bean
    @JobScope
    public Step melonCsvStep(
            ItemReader<MelonCsvReadDto> melonCsvItemReader,
            ItemProcessor<MelonCsvReadDto, MelonCsvWriterDto> addMonthProcessor,
            ItemWriter<MelonCsvWriterDto> melonCsvItemWriter
    ) {
        return new StepBuilder("melonCsvStep", jobRepository)
                .<MelonCsvReadDto, MelonCsvWriterDto>chunk(100, tx)
                .reader(melonCsvItemReader)
                .processor(addMonthProcessor)
                .writer(melonCsvItemWriter)
                .build();
    }

    @Bean
    public FlatFileItemReader<MelonCsvReadDto> melonCsvItemReader() {
        MelonTokenizer tokenizer = new MelonTokenizer();
        tokenizer.setNames(READ_COLUMN_NAMES);
        tokenizer.setIncludedFields(INCLUDE_FIELDS);
        tokenizer.setStrict(false);

        DefaultLineMapper<MelonCsvReadDto> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(new BeanWrapperFieldSetMapper<MelonCsvReadDto>() {{
            setTargetType(MelonCsvReadDto.class);
        }});

        return new FlatFileItemReaderBuilder<MelonCsvReadDto>()
                .name("melonCsvItemReader")
                .resource(new FileSystemResource(FileConstants.getInputFilePath()))
                .encoding(StandardCharsets.UTF_8.name())
                .linesToSkip(5)
                .lineMapper(lineMapper)
                .build();
    }

    @Bean
    @JobScope
    public ItemProcessor<MelonCsvReadDto, MelonCsvWriterDto> addMonthProcessor(
    ) {
        return MelonCsvWriterDto::of;
    }

    @Bean
    public FlatFileItemWriter<MelonCsvWriterDto> melonCsvItemWriter() {
        return new FlatFileItemWriterBuilder<MelonCsvWriterDto>()
                .name("melonCsvItemWriter")
                .resource(new FileSystemResource(FileConstants.getMelonFilePath()))
                .headerCallback(writer -> writer.write(HEADER_NAMES))
                .delimited()
                .delimiter(",")
                .names(WRITER_COLUMN_NAMES)
                .build();
    }
}
