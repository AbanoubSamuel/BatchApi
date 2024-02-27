package org.abg.batchapi.batch;

import lombok.RequiredArgsConstructor;
import org.abg.batchapi.entities.Visitor;
import org.abg.batchapi.repositories.VisitorRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableBatchProcessing(dataSourceRef = "batchDataSource", transactionManagerRef = "batchTransactionManager")
@RequiredArgsConstructor
public class BatchConfig extends DefaultBatchConfiguration {

    @Autowired
    private Visitor visitor;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private final RestTemplate restTemplate;
    @Autowired
    private VisitorRepository visitorRepository;
    @Autowired
    private ItemReader<Visitor> visitorItemReader;
    @Autowired
    private PlatformTransactionManager transactionManager;


    @Bean
    public Visitor visitors() {
        return new Visitor();
    }

    @Bean
    public Job importVisitorsJob() {
        return new JobBuilder("importVisitorsJob", jobRepository)
                .start(importVisitorsStep(jobRepository, visitor, transactionManager))
                .build();
    }

    @Bean
    public Step importVisitorsStep(JobRepository jobRepository, Visitor visitor, PlatformTransactionManager transactionManager) {
        return new StepBuilder("importVisitorsStep", jobRepository)
                .<Visitor, Visitor>chunk(100, transactionManager)
                .reader(visitorItemReader)
                .processor(itemProcessor(restTemplate))
                .writer(itemWriter())
                .build();
    }

    @Bean
    public ItemProcessor<Visitor, Visitor> itemProcessor(RestTemplate restTemplate) {
        return new VisitorItemProcessor(restTemplate);
    }

    @Bean
    public ItemWriter<Visitor> itemWriter() {
        return visitorRepository::saveAll;
    }

    @Bean
    public FlatFileItemReader<Visitor> flatFileItemReader(@Value("${inputFile}") Resource inputFile) {
        FlatFileItemReader<Visitor> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setName("DEVAL");
        flatFileItemReader.setLinesToSkip(1);
        flatFileItemReader.setResource(inputFile);
        flatFileItemReader.setLineMapper(lineMapper());
        return flatFileItemReader;
    }

    @Bean
    public LineMapper<Visitor> lineMapper() {
        DefaultLineMapper<Visitor> defaultLineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setNames("id", "firstName", "lastName", "emailAddress", "phoneNumber", "address", "strVisitDate");
        lineTokenizer.setStrict(false); // Set strict property to false
        defaultLineMapper.setLineTokenizer(lineTokenizer);
        BeanWrapperFieldSetMapper<Visitor> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Visitor.class);
        defaultLineMapper.setFieldSetMapper(fieldSetMapper);
        return defaultLineMapper;
    }

}
