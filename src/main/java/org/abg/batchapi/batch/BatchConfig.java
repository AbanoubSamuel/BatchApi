package org.abg.batchapi.batch;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.abg.batchapi.entities.Visitor;
import org.abg.batchapi.repositories.VisitorPagingRepository;
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
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Configuration
@EnableBatchProcessing(isolationLevelForCreate = "ISOLATION_DEFAULT", transactionManagerRef = "platformTransactionManager")
@RequiredArgsConstructor
public class BatchConfig extends DefaultBatchConfiguration {


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
    public Job importVisitorsJob() {
        return new JobBuilder("importVisitorsJob", jobRepository)
                .start(importVisitorsStep(jobRepository, transactionManager))
                .build();
    }

    @Bean
    public Step importVisitorsStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("importVisitorsStep", jobRepository)
                .<Visitor, Visitor>chunk(200, transactionManager)
                .reader(visitorItemReader)
                .processor(itemProcessor())
                .writer(itemWriter(visitorRepository))
                .build();
    }

    @Bean
    public RepositoryItemReader<Visitor> itemReader(EntityManagerFactory entityManagerFactory,
                                                    VisitorPagingRepository visitorPagingRepository) {
        return new RepositoryItemReaderBuilder<Visitor>()
                .name("itemReader")
                .pageSize(200)
                .repository(visitorPagingRepository)
                .methodName("findAll")
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public ItemProcessor<Visitor, Visitor> itemProcessor() {
        return new VisitorItemProcessor();
    }

    @Bean
    public ItemWriter<Visitor> itemWriter(VisitorRepository repository) {
        return new RepositoryItemWriterBuilder<Visitor>()
                .repository(repository)
                .methodName("save")
                .build();
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
