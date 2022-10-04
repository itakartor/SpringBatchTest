package com.kartor.batchProcess;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

@Slf4j
public class CoperturaJobListener implements JobExecutionListener {
    @Override
    public void beforeJob(JobExecution jobExecution) {
      log.info("Prima del JOB");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("Dopo il JOB");
    }
}
