package com.kartor.batchProcess;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.SimpleJob;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
@RequestMapping("/v1")
@Validated
@EnableScheduling
@Component
public class JobLauncherController {

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    BatchConfiguration job;

    private AtomicBoolean enabled = new AtomicBoolean(false);

    private AtomicInteger batchRunCounter = new AtomicInteger(0);

    // @Scheduled(cron = "0 10 * * * *")
    // @Scheduled(initialDelay = 240000, fixedRate = 20000)
    @Async
    //@Scheduled(fixedRate = 240000, initialDelay = 240000)
    @Scheduled(cron = "0 */5 * * * *")
    public void startJob() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        try {
            JobParameters jobParameters = new JobParametersBuilder().addDate("date",new Date())
                    //.addString("dateString", new Date().toString())
                    .addString("mioJob","Pino")
                    .toJobParameters();
            JobExecution jobExecution = jobLauncher.run(job.importCopertureJob(), jobParameters);

            log.info("Job's Status:::"+jobExecution.getStatus());

        } catch (JobExecutionAlreadyRunningException
                | JobRestartException
                | JobInstanceAlreadyCompleteException
                | JobParametersInvalidException e) {
            e.printStackTrace();
        }
    }

    /*@Scheduled(fixedDelay = 120000)
    @Async
    public void launchJob() throws Exception {
        if (enabled.get()) {
            Date date = new Date();
            JobExecution jobExecution = jobLauncher
                    .run(job, new JobParametersBuilder()
                            .addDate("launchDate", date)
                            .toJobParameters());
            batchRunCounter.incrementAndGet();
        }
    }*/
    @CrossOrigin
    @GetMapping("/jobLauncher")
    public ResponseEntity<Void> handle() throws Exception{
        startJob();
        BaseResponse.BaseResponseBuilder baseResponse = BaseResponse.builder();
        enabled.set(!enabled.get());
        if(enabled.get() || true) {
            baseResponse.descrizione("Task attivata");
        } else {
            baseResponse.descrizione("Task Disattivata");
        }
        return new ResponseEntity(baseResponse.build(),HttpStatus.OK);
    }
}
