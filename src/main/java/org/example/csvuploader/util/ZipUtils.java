package org.example.csvuploader.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

    public static File zipDirectory(String sourceDirPath, String zipFilePath) throws IOException {
        File zipFile = new File(zipFilePath);

        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            Path sourceDirPathObj = Paths.get(sourceDirPath);
            List<Path> files = listFiles(sourceDirPathObj);

            for (Path filePath : files) {
                String zipEntryName = sourceDirPathObj.relativize(filePath).toString();
                ZipEntry zipEntry = new ZipEntry(zipEntryName);
                zos.putNextEntry(zipEntry);

                try (FileInputStream fis = new FileInputStream(filePath.toFile())) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }
                }
                zos.closeEntry();
            }
        }

        return zipFile;
    }

    private static List<Path> listFiles(Path sourceDirPath) throws IOException {
        try (Stream<Path> walk = Files.walk(sourceDirPath)) {
            return walk
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        }
    }

}
