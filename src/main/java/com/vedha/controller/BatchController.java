package com.vedha.controller;

import com.vedha.event.RestBatchEvent;
import com.vedha.event.RestUploadEvent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/batch")
@RequiredArgsConstructor
@Tag(name = "Batch", description = "Batch API")
public class BatchController {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Operation(summary = "Start Batch Job", description = "Start the batch job with the minimum number of employees", tags = {"Batch"})
    @ApiResponse(responseCode = "200", description = "HTTP Status 200 OK")
    @PostMapping(value = "/start", consumes = MediaType.ALL_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> startRestJob(@RequestParam("minEmployees") Long minEmployees) {

        log.info("Starting the job with minEmployees: {}", minEmployees);

        // Publish the event to start the job
        applicationEventPublisher.publishEvent(RestBatchEvent.builder().minEmployees(minEmployees).build());

        return ResponseEntity.ok("Job started successfully");
    }

    @Operation(summary = "File Upload Job", description = "Upload file to start the batch job", tags = {"Batch"})
    @ApiResponse(responseCode = "200", description = "HTTP Status 200 OK")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> uploadRestJob(@RequestParam MultipartFile multipartFile) {

        log.info("Uploading the file: {}", multipartFile.getOriginalFilename());

        // Publish the event to stop the job
        applicationEventPublisher.publishEvent(RestUploadEvent.builder().multipartFile(multipartFile).build());

        return ResponseEntity.ok("Job started successfully with file upload: " + multipartFile.getOriginalFilename());
    }
}
