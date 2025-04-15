package org.example.csvuploader.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class JobListener implements JobExecutionListener {

    @Override
    public void afterJob(JobExecution jobExecution) {
        Duration between = Duration.between(jobExecution.getStartTime(), jobExecution.getEndTime());
        long processTime = between.toMillis();
        log.info("Batch Process Time = {}", processTime);
    }
}
