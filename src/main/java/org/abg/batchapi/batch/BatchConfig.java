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
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Sort;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing(isolationLevelForCreate = "ISOLATION_DEFAULT", transactionManagerRef = "platformTransactionManager")
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
    public Job updateVisitorAddressJob() {
        return new JobBuilder("updateVisitorAddressJob", jobRepository).start(updateVisitorAddressStep(jobRepository,
                transactionManager
        )).build();
    }


    @Bean
    public Step updateVisitorAddressStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("updateVisitorAddressStep", jobRepository).<Visitor, Visitor>chunk(200,
                        transactionManager
                )
                .reader(visitorItemReader)
                .processor(itemProcessor())
                .writer(itemWriter(visitorRepository))
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public RepositoryItemReader<Visitor> itemReader(EntityManagerFactory entityManagerFactory, VisitorPagingRepository visitorPagingRepository) {
        return new RepositoryItemReaderBuilder<Visitor>().name("itemReader")
                .repository(visitorPagingRepository)
                .methodName("findAll")
                .pageSize(200)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public ItemProcessor<Visitor, Visitor> itemProcessor() {
        return new VisitorItemProcessor(restTemplate);
    }

    @Bean
    public RepositoryItemWriter<Visitor> itemWriter(VisitorRepository repository) {
        return new RepositoryItemWriterBuilder<Visitor>().repository(repository).methodName("save").build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor("spring_batch");
    }

}


