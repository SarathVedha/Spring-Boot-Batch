package com.vedha.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomStepListener implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {

        log.warn("Step execution started for step: {}", stepExecution.getStepName());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {

        if (stepExecution.getStatus().isRunning()) {

            stepExecution.setExitStatus(new ExitStatus(stepExecution.getExitStatus().getExitCode(), "Step is still running"));
            log.error("Step is still running {}", stepExecution.getSummary());
        } else if (stepExecution.getStatus().isUnsuccessful()) {

            stepExecution.setExitStatus(new ExitStatus(stepExecution.getExitStatus().getExitCode(), "Step failed"));
            log.error("Step failed {}", stepExecution.getSummary());
        } else {

            stepExecution.setExitStatus(new ExitStatus(stepExecution.getExitStatus().getExitCode(), "Step completed successfully"));
            log.error("Step completed {}", stepExecution.getSummary());
        }

        log.warn("Step execution completed for step: {}", stepExecution.getStepName());
        log.warn("Step execution status: {}", stepExecution.getStatus());
        log.warn("Step execution exit status: {}", stepExecution.getExitStatus());

        log.warn("Step execution read count: {}", stepExecution.getReadCount());
        log.warn("Step execution write count: {}", stepExecution.getWriteCount());
        log.warn("Step execution commit count: {}", stepExecution.getCommitCount());
        log.warn("Step execution rollback count: {}", stepExecution.getRollbackCount());

        log.warn("Step execution filter count: {}", stepExecution.getFilterCount());
        log.warn("Step execution skip count: {}", stepExecution.getSkipCount());
        log.warn("Step execution process skip count: {}", stepExecution.getProcessSkipCount());
        log.warn("Step execution read skip count: {}", stepExecution.getReadSkipCount());
        log.warn("Step execution write skip count: {}", stepExecution.getWriteSkipCount());

        log.warn("Step execution summary: {}", stepExecution.getSummary());

        return stepExecution.getExitStatus();
    }
}
