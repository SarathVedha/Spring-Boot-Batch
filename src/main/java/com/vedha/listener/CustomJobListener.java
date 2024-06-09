package com.vedha.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomJobListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {

        log.error("Job started {}", jobExecution.getJobInstance().getJobName());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {

        if (jobExecution.getStatus().isRunning()) {

            jobExecution.setExitStatus(new ExitStatus(jobExecution.getExitStatus().getExitCode(), "Job is still running"));
            log.error("Job is still running {}", jobExecution.getJobInstance());
        } else if (jobExecution.getStatus().isUnsuccessful()) {

            jobExecution.setExitStatus(new ExitStatus(jobExecution.getExitStatus().getExitCode(), "Job failed".concat(" ").concat(jobExecution.getExitStatus().getExitDescription())));
            log.error("Job failed {}", jobExecution.getJobInstance());
        } else {

            jobExecution.setExitStatus(new ExitStatus(jobExecution.getExitStatus().getExitCode(), "Job completed successfully"));
            log.error("Job completed {}", jobExecution.getJobInstance());
        }
    }
}
