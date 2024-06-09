package com.vedha.listener;

import com.vedha.entity.FileEntity;
import com.vedha.event.RestBatchEvent;
import com.vedha.event.RestUploadEvent;
import com.vedha.repository.FileRepository;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class RunBatchListener {

    private final FileRepository fileRepository;

    private final Job restCsvUploadImporterJob;

    private final JobLauncher jobLauncher;

    private final Job restCsvImporterJob;

    private final Job csvImporterJob;

    @Async
    @EventListener(ApplicationReadyEvent.class) // Trigger the job when the application is ready
    public void onApplicationEvent() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException,
            JobParametersInvalidException, JobRestartException {

        // Trigger the job
        jobLauncher.run(csvImporterJob, new JobParametersBuilder()
                .addString("fileClassPath", "/files/organizations.csv")
                .addLong("minEmployees", 1000L)
                .toJobParameters()
        );
    }

    @Async
    @EventListener
    public void onRestBatchEvent(RestBatchEvent restBatchEvent) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException,
            JobParametersInvalidException, JobRestartException {

        // Trigger the job
        jobLauncher.run(restCsvImporterJob, new JobParametersBuilder()
                .addString("fileClassPath", "/files/organizations.csv")
                .addLong("minEmployees", restBatchEvent.getMinEmployees())
                .toJobParameters()
        );
    }

    @Async
    @EventListener
    public void onRestUploadBatchEvent(RestUploadEvent restUploadEvent) throws IOException, JobInstanceAlreadyCompleteException,
            JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {

        MultipartFile multipartFile = restUploadEvent.getMultipartFile();
        log.info("Received file: {}", multipartFile.getOriginalFilename());

        // Save the file to the database
        FileEntity build = FileEntity.builder()
                .fileName(multipartFile.getOriginalFilename())
                .fileType(multipartFile.getContentType())
                .fileSize(multipartFile.getSize())
                .fileData(multipartFile.getBytes())
                .build();

        FileEntity save = fileRepository.save(build);

        // Trigger the job
        jobLauncher.run(restCsvUploadImporterJob, new JobParametersBuilder()
                .addLong("fileId", save.getId())
                .toJobParameters()
        );
    }

}
