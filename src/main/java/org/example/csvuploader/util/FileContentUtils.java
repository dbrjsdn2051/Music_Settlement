package org.example.csvuploader.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;

@Slf4j
@Component
public class FileContentUtils {

    public static void ensureDirectoryExists(String path) {
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

    public static void cleanupFiles(String... filePaths) {
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

}
