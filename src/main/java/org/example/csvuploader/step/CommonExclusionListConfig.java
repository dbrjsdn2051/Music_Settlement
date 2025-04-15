package org.example.csvuploader.step;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.example.csvuploader.constant.FileConstants;
import org.example.csvuploader.domain.excluce.CommonExcludeSettlement;
import org.example.csvuploader.dto.settlement.common.CommonExcludeSettlementReaderDto;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
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
public class CommonExclusionListConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager tx;
    private final EntityManagerFactory emf;

    private static final String[] COLUMN_NAMES = new String[]{"trackCode", "memberShip", "songCode"};
    private static final int[] INCLUDED_FIELDS = new int[]{0, 1, 2};

    @JobScope
    @Bean("commonExclusionListStep")
    public Step commonExclusionListStep(
            ItemReader<CommonExcludeSettlementReaderDto> commonExclusionListReader,
            ItemProcessor<CommonExcludeSettlementReaderDto, CommonExcludeSettlement> commonExclusionListProcessor,
            ItemWriter<CommonExcludeSettlement> commonExcludeSettlementJpaItemWriter
    ) {
        return new StepBuilder("genieExclusionListStep", jobRepository)
                .<CommonExcludeSettlementReaderDto, CommonExcludeSettlement>chunk(1000, tx)
                .reader(commonExclusionListReader)
                .processor(commonExclusionListProcessor)
                .writer(commonExcludeSettlementJpaItemWriter)
                .build();
    }

    @Bean
    public FlatFileItemReader<CommonExcludeSettlementReaderDto> commonExclusionListReader() {
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames(COLUMN_NAMES);
        tokenizer.setIncludedFields(INCLUDED_FIELDS);
        tokenizer.setStrict(false);

        DefaultLineMapper<CommonExcludeSettlementReaderDto> lineMapper = new DefaultLineMapper<CommonExcludeSettlementReaderDto>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
            setTargetType(CommonExcludeSettlementReaderDto.class);
        }});

        return new FlatFileItemReaderBuilder<CommonExcludeSettlementReaderDto>()
                .name("commonExclusionListReader")
                .encoding(StandardCharsets.UTF_8.name())
                .resource(new FileSystemResource(FileConstants.getBaseDirectory() + "/externalFile.csv"))
                .lineMapper(lineMapper)
                .linesToSkip(1)
                .build();
    }

    @Bean
    public ItemProcessor<CommonExcludeSettlementReaderDto, CommonExcludeSettlement> commonExclusionListProcessor() {
        return CommonExcludeSettlementReaderDto::from;
    }

    @Bean
    public JpaItemWriter<CommonExcludeSettlement> commonExcludeSettlementJpaItemWriter() {
        JpaItemWriter<CommonExcludeSettlement> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(emf);
        return writer;
    }
}
