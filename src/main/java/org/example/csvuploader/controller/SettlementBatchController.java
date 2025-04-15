package org.example.csvuploader.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.csvuploader.constant.FileConstants;
import org.example.csvuploader.converter.ExcelConverter;
import org.example.csvuploader.util.TaskletResultStore;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

@Slf4j
@RestController
public class SettlementBatchController {

    private final JobLauncher jobLauncher;
    private final Map<String, Job> jobMap;
    private final String baseDirectory;
    private final ExcelConverter excelConverter;

    public SettlementBatchController(
            JobLauncher jobLauncher,
            @Qualifier("melonSettlementCsvJob") Job melonSettlementCsvJob,
            @Qualifier("genieSettlementCsvJob") Job genieSettlementCsvJob,
            @Qualifier("vibeSettlementCsvJob") Job vibeSettlementCsvJob,
            @Qualifier("floSettlementCsvJob") Job floSettlementCsvJob,
            ExcelConverter excelConverter
    ) {
        this.jobLauncher = jobLauncher;
        this.jobMap = Map.of(
                "melon", melonSettlementCsvJob,
                "genie", genieSettlementCsvJob,
                "vibe", vibeSettlementCsvJob,
                "flo", floSettlementCsvJob
        );
        this.baseDirectory = FileConstants.getBaseDirectory();
        this.excelConverter = excelConverter;
        ensureDirectoryExists(baseDirectory);
    }

    @PostMapping("/settlement/{processType}")
    public ResponseEntity<Resource> settlementProcess(
            @RequestParam MultipartFile file,
            @RequestParam MultipartFile externalFile,
            @PathVariable String processType
    ) throws Exception {
        return processExcelFile(processType, file, externalFile);
    }

    @GetMapping("/settlement/result")
    public ResponseEntity<String> getTaskletResult() {
        if (TaskletResultStore.getResult() == null) {
            return ResponseEntity.status(HttpStatus.OK).body("0");
        }

        String result = TaskletResultStore.getResult();
        TaskletResultStore.clearResult();
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    private ResponseEntity<Resource> processExcelFile(
            String serviceType,
            MultipartFile file,
            MultipartFile externalFile
    ) throws Exception {
        String csvInputPath = baseDirectory + "/settlement.csv";
        String outputPath = baseDirectory + "/" + serviceType + ".csv";
        String excelPath = baseDirectory + "/input.xlsx";
        String externalCsvPath = baseDirectory + "/externalFile.csv";

        try {
            File excelFile = new File(excelPath);
            externalFile.transferTo(excelFile);
            excelConverter.excelToCsv(excelPath, externalCsvPath);

            File csvFile = new File(csvInputPath);
            file.transferTo(csvFile);

            Job job = jobMap.get(serviceType);
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .addDate("datetime", new Date())
                    .toJobParameters();

            jobLauncher.run(job, jobParameters);


            File outputFile = new File(outputPath);
            if (!outputFile.exists()) {
                throw new FileNotFoundException("Output file not found: " + outputPath);
            }

            byte[] fileContent = Files.readAllBytes(outputFile.toPath());
            ByteArrayResource byteArrayResource = new ByteArrayResource(fileContent);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + serviceType + ".csv")
                    .contentType(MediaType.parseMediaType("text/csv;charset=UTF-8"))
                    .header(HttpHeaders.CONTENT_ENCODING, StandardCharsets.UTF_8.name())
                    .body(byteArrayResource);
        } finally {
            cleanupFiles(excelPath, csvInputPath, outputPath);
        }
    }

    private void cleanupFiles(String... filePaths) {
        Arrays.stream(filePaths).forEach(filePath -> {
            try {
                File file = new File(filePath);
                if (file.exists() && !file.delete()) {
                    log.warn("Failed to delete file: {}", filePath);
                } else {
                    log.info("Successfully deleted file: {}", filePath);
                }
            } catch (Exception e) {
                log.error("Error while deleting file: {}", filePath, e);
            }
        });
    }

    private void ensureDirectoryExists(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                log.info("Created directory: {}", path);
                return;
            }
            log.error("Failed to create directory: {}", path);
        }
    }
}
