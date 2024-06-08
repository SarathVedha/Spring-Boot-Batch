package com.vedha.listener;

import com.vedha.event.RestBatchEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RunBatchListener {

    private final JobLauncher jobLauncher;

    private final Job restCsvImporterJob;

    private final Job csvImporterJob;

    @Async
    @EventListener(ApplicationReadyEvent.class) // Trigger the job when the application is ready
    public void onApplicationEvent() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {

        jobLauncher.run(csvImporterJob, new JobParametersBuilder().addLong("minEmployees", 1000L).toJobParameters());
    }

    @Async
    @EventListener
    public void onRestBatchEvent(RestBatchEvent restBatchEvent) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {

        jobLauncher.run(restCsvImporterJob, new JobParametersBuilder().addLong("minEmployees", restBatchEvent.getMinEmployees()).toJobParameters());
    }
}
