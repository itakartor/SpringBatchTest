package com.kartor.batchProcess;

import com.kartor.batchProcess.ftp.CoperturaFtpDTO;
import com.kartor.batchProcess.ftp.FtpClient;
import com.kartor.batchProcess.job.CoperturaItemProcessor;
import com.kartor.batchProcess.job.CoperturaItemReader;
import com.kartor.batchProcess.job.CoperturaItemWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.List;

@EnableBatchProcessing
@Configuration
// @EnableScheduling
@EnableAsync
@Slf4j
public class BatchConfiguration {

    @Value("${ftp.file.coperture.separator:/}")
    private String ftpFileSeparator;

    @Value("${ftp.coperture.url}")
    private String ftpUrl;

    @Value("${ftp.coperture.port}")
    private Integer ftpPort;

    @Value("${ftp.coperture.user}")
    private String ftpUser;

    @Value("${ftp.coperture.password}")
    private String ftpPassword;

    @Value("${ftp.upload.coperture.path}")
    private String ftpBasePath;

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    public CoperturaJobListener coperturaJobListener() {
        return new CoperturaJobListener();
    }
    @Bean
    public CoperturaStepListener coperturaStepListener() {
        return new CoperturaStepListener();
    }

    @Bean
    @Qualifier("ftpClient")
    public FtpClient ftpClient() {
        return new FtpClient(ftpUrl, ftpPort, ftpUser, ftpPassword, ftpBasePath, ftpFileSeparator);
    }

    @Bean
    public CoperturaItemReader<List<CoperturaFtpDTO>> reader() {
        return new CoperturaItemReader<>();
    }

    @Bean
    public CoperturaItemProcessor processor() {
        return new CoperturaItemProcessor();
    }

    @Bean
    public CoperturaItemWriter<List<CoperturaFtpDTO>> write() {
        return new CoperturaItemWriter<>();
    }


    @Bean
    public Job importCopertureJob() {
        return jobBuilderFactory.get("importCopertureJob")
                .listener(coperturaJobListener())
                .preventRestart().start(step1()).on("COMPLETED")
                //.flow(step1())
                .end().build().build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .listener(coperturaStepListener())
                .<List<CoperturaFtpDTO>, List<CoperturaFtpDTO>> chunk(1)
                .reader(reader())
                .processor(processor())
                .writer(write())
                .faultTolerant()
                .retryLimit(3)
                .retry(Exception.class)
                .build();
                //.readerIsTransactionalQueue()

    }
}
