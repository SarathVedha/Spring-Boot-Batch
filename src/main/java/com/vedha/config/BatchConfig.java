package com.vedha.config;

import com.vedha.entity.OrganizationEntity;
import com.vedha.listener.CustomJobListener;
import com.vedha.listener.CustomStepListener;
import com.vedha.service.CustomItemProcessor;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@EnableAsync
@EnableBatchProcessing
public class BatchConfig {

    // Reader
    @Bean
    public ItemReader<OrganizationEntity> reader() {

        return new FlatFileItemReaderBuilder<OrganizationEntity>()
                .name("organizationItemReader")
                .resource(new ClassPathResource("/files/organizations.csv"))
                .linesToSkip(1)
                .delimited()
                .delimiter(",")

                // order should match with the csv file and the field name entity for the mapping
                // index and organizationId will be ignored in the mapping to the entity
                .names("index", "organizationId", "name", "website", "country", "description", "founded", "industry", "employees")

                // BeanWrapperFieldSetMapper is used to map the fields to the entity object with the same name automatically,
                // but we are not using this because we are ignoring the index and organizationId
//                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{setTargetType(OrganizationEntity.class);}})

                // Custom fieldSetMapper to map the fields to the entity object
                .fieldSetMapper(fieldSet ->
                        OrganizationEntity.builder()
                                .name(fieldSet.readString("name"))
                                .website(fieldSet.readString("website"))
                                .country(fieldSet.readString("country"))
                                .description(fieldSet.readString("description"))
                                .founded(fieldSet.readString("founded"))
                                .industry(fieldSet.readString("industry"))
                                .employees(fieldSet.readLong("employees"))
                                .build()
                )
                .build();
    }

    // Processor
    @Bean
    @JobScope // to get the job parameters
    public ItemProcessor<OrganizationEntity, OrganizationEntity> processor(@Value("#{jobParameters['minEmployees']}") Long minEmployees) {
        return organization -> {

            if (organization.getEmployees() > minEmployees) {
                return organization;
            } else {
                log.warn("organization skipped: {}", organization);
                return null;
            }
        };
    }

    // Writer
    @Bean
    public ItemWriter<OrganizationEntity> writer(EntityManagerFactory entityManagerFactory) {

        return new JpaItemWriterBuilder<OrganizationEntity>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    // Step
    //  is a single operation in the job like read, process, write or any other operation, but it should contain reader, writer
    @Bean
    public Step csvImporterStep(ItemReader<OrganizationEntity> reader, ItemWriter<OrganizationEntity> writer,
                                ItemProcessor<OrganizationEntity, OrganizationEntity> processor,
                                PlatformTransactionManager transactionManager, JobRepository jobRepository, CustomStepListener customStepListener) {

        return new StepBuilder("csvImporterStep", jobRepository)
                .listener(customStepListener)
                .<OrganizationEntity, OrganizationEntity>chunk(1000, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .allowStartIfComplete(true)
                .build();
    }

    // Job
    @Bean
    public Job csvImporterJob(Step csvImporterStep, JobRepository jobRepository, CustomJobListener customJobListener) {

        return new JobBuilder("csvImporterJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(customJobListener)
                .flow(csvImporterStep)
                .end()
                .build();
    }

    @Bean
    public Step restCsvImporterStep(ItemReader<OrganizationEntity> reader, ItemWriter<OrganizationEntity> writer, CustomItemProcessor processor, // Custom ItemProcessor
                                    PlatformTransactionManager transactionManager, JobRepository jobRepository, CustomStepListener customStepListener) {

        return new StepBuilder("restCsvImporterStep", jobRepository)
                .listener(customStepListener)
                .<OrganizationEntity, OrganizationEntity>chunk(500, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .allowStartIfComplete(true)
                .build();
    }

    // Job
    @Bean
    public Job restCsvImporterJob(Step restCsvImporterStep, JobRepository jobRepository, CustomJobListener customJobListener) {

        return new JobBuilder("restCsvImporterJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(customJobListener)
                .flow(restCsvImporterStep)
                .end()
                .build();
    }

}
