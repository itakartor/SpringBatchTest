package com.kartor.batchProcess;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.swing.*;

@SpringBootApplication
public class BatchProcessApplication {

	public static void main(String[] args) {
		SpringApplication.run(BatchProcessApplication.class, args);
	}
}
