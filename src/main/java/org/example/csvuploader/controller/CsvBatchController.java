package org.example.csvuploader.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.csvuploader.constant.FileConstants;
import org.example.csvuploader.converter.ExcelConverter;
import org.example.csvuploader.util.FileContentUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.util.Date;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/batch")
public class CsvBatchController {

    private final JobLauncher jobLauncher;
    private final Map<String, Job> jobMap;
    private final String baseDirectory;
    private final ExcelConverter excelConverter;

    public CsvBatchController(
            JobLauncher jobLauncher,
            @Qualifier("melonCsvJob") Job melonCsvJob,
            @Qualifier("genieCsvJob") Job genieCsvJob,
            @Qualifier("vibeCsvJob") Job vibeCsvJob,
            @Qualifier("floCsvJob") Job floCsvJob,
            @Qualifier("vibePlusCsvJob") Job vibPlushCsvJob,
            ExcelConverter excelConverter
    ) {
        this.jobLauncher = jobLauncher;
        this.jobMap = Map.of(
                "melon", melonCsvJob,
                "genie", genieCsvJob,
                "vibe", vibeCsvJob,
                "flo", floCsvJob,
                "vibePlus", vibPlushCsvJob
        );
        this.baseDirectory = FileConstants.getBaseDirectory();
        this.excelConverter = excelConverter;
        FileContentUtils.ensureDirectoryExists(this.baseDirectory);
    }

    @PostMapping("/{processType}")
    public ResponseEntity<Resource> processExcelToCsv(
            @RequestParam MultipartFile file,
            @PathVariable String processType
    ) throws Exception {
        return processExcelFile(processType, file);
    }


    private ResponseEntity<Resource> processExcelFile(
            String serviceType,
            MultipartFile file
    ) throws Exception {
        String originalFilename = file.getOriginalFilename();
        int idx = originalFilename.lastIndexOf(".");
        String fileExtensions = originalFilename.substring(idx + 1);
        String excelPath = baseDirectory + "/input.xlsx";
        String csvInputPath = baseDirectory + "/input.csv";
        String outputPath = baseDirectory + "/" + serviceType + ".csv";

        try {
            if ("xlsx".equalsIgnoreCase(fileExtensions) || "xls".equalsIgnoreCase(fileExtensions)) {
                File excelFile = new File(excelPath);
                file.transferTo(excelFile);
                excelConverter.excelToCsv(excelPath, csvInputPath);
            } else {
                File csvFile = new File(csvInputPath);
                file.transferTo(csvFile);
            }

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
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=" + serviceType + ".csv")
                    .contentType(MediaType.parseMediaType("text/csv;charset=UTF-8"))
                    .header(HttpHeaders.CONTENT_ENCODING, "UTF-8")
                    .body(byteArrayResource);
        } finally {
            FileContentUtils.cleanupFiles(excelPath, csvInputPath, outputPath);
        }
    }
}