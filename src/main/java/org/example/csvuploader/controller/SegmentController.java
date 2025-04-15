package org.example.csvuploader.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.csvuploader.constant.FileConstants;
import org.example.csvuploader.converter.ExcelConverter;
import org.example.csvuploader.util.FileContentUtils;
import org.example.csvuploader.util.ZipUtils;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Date;

@Slf4j
@RestController
public class SegmentController {

    private final JobLauncher jobLauncher;
    private final ExcelConverter excelConverter;
    private final Job youtubeSegmentFileJob;
    private final String baseDirectory;

    public SegmentController(
            JobLauncher jobLauncher,
            ExcelConverter excelConverter,
            @Qualifier("youtubeSegmentFileJob") Job youtubeSegmentFileJob
    ) {
        this.jobLauncher = jobLauncher;
        this.excelConverter = excelConverter;
        this.youtubeSegmentFileJob = youtubeSegmentFileJob;
        this.baseDirectory = FileConstants.getBaseDirectory();
        FileContentUtils.ensureDirectoryExists(baseDirectory);
    }

    @PostMapping("/segment")
    public ResponseEntity<Resource> segmentYoutubeForZipFile(
            @RequestParam MultipartFile[] youtubeExcelFile
    ) throws Exception {
        String outputPath = baseDirectory + "/out.zip";

        try {
            for (int i = 0; i < youtubeExcelFile.length; i++) {
                String excelPath = baseDirectory + "/input_" + i + "xlsx";
                String csvInputPath = baseDirectory + "/segment_" + i + ".csv";
                MultipartFile multipartFile = youtubeExcelFile[i];
                File file = new File(excelPath);
                multipartFile.transferTo(file);
                excelConverter.excelToCsv(excelPath, csvInputPath);


                String originalFilename = multipartFile.getOriginalFilename();
                String[] nameWithExtension = originalFilename.split("\\.");
                String filename = nameWithExtension[0];
                String[] nameParts = filename.split("_");
                String artist = nameParts[nameParts.length - 1];

                JobParameters jobParameters = new JobParametersBuilder()
                        .addLong("time", System.currentTimeMillis())
                        .addDate("dateTime", new Date())
                        .addString("fileIndex", String.valueOf(i))
                        .addString("artist", artist)
                        .toJobParameters();

                jobLauncher.run(youtubeSegmentFileJob, jobParameters);
            }

            File zipFile = ZipUtils.zipDirectory(FileConstants.getYoutubeDirectoryPath(), outputPath);

            if (!zipFile.exists()) {
                throw new FileNotFoundException("Output File Not Founc : " + outputPath);
            }

            byte[] fileContent = Files.readAllBytes(zipFile.toPath());
            ByteArrayResource byteArrayResource = new ByteArrayResource(fileContent);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=youtube.zip")
                    .contentType(MediaType.parseMediaType("text/csv;charset=UTF-8"))
                    .header(HttpHeaders.CONTENT_ENCODING, StandardCharsets.UTF_8.name())
                    .body(byteArrayResource);
        } finally {
            cleanupDirectory(FileConstants.getYoutubeDirectoryPath());
            cleanupDirectory(FileConstants.getBaseDirectory());
        }
    }

    private void cleanupDirectory(String directoryPath) {
        File directory = new File(directoryPath);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && !file.delete()) {
                        log.warn("Failed to delete file: {}", file.getAbsolutePath());
                    }
                }
            }
            log.info("Cleaned up directory: {}", directoryPath);
        }
    }

}
