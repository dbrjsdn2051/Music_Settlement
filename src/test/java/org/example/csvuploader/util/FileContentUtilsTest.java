package org.example.csvuploader.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class FileContentUtilsTest {

    @TempDir
    Path tempDir;

    private File testFile1;
    private File testFile2;
    private String testDirectory;

    @BeforeEach
    void setUp() throws IOException {
        testDirectory = tempDir.resolve("test-directory").toString();
        testFile1 = tempDir.resolve("test1.txt").toFile();
        testFile2 = tempDir.resolve("test2.txt").toFile();
        
        // 테스트 파일 생성
        Files.write(testFile1.toPath(), "test content 1".getBytes());
        Files.write(testFile2.toPath(), "test content 2".getBytes());
    }

    @Test
    void ensureDirectoryExists_새디렉토리생성() {
        // given
        String newDirectoryPath = tempDir.resolve("new-directory").toString();
        File newDirectory = new File(newDirectoryPath);
        
        // when
        FileContentUtils.ensureDirectoryExists(newDirectoryPath);
        
        // then
        assertThat(newDirectory).exists();
        assertThat(newDirectory).isDirectory();
    }

    @Test
    void ensureDirectoryExists_기존디렉토리존재() {
        // given
        String existingDirectoryPath = tempDir.toString();
        
        // when & then (예외 발생하지 않아야 함)
        FileContentUtils.ensureDirectoryExists(existingDirectoryPath);
        
        assertThat(new File(existingDirectoryPath)).exists();
    }

    @Test
    void cleanupFiles_단일파일삭제() {
        // given
        assertThat(testFile1).exists();
        
        // when
        FileContentUtils.cleanupFiles(testFile1.getAbsolutePath());
        
        // then
        assertThat(testFile1).doesNotExist();
    }

    @Test
    void cleanupFiles_다중파일삭제() {
        // given
        assertThat(testFile1).exists();
        assertThat(testFile2).exists();
        
        // when
        FileContentUtils.cleanupFiles(
            testFile1.getAbsolutePath(),
            testFile2.getAbsolutePath()
        );
        
        // then
        assertThat(testFile1).doesNotExist();
        assertThat(testFile2).doesNotExist();
    }

    @Test
    void cleanupFiles_존재하지않는파일() {
        // given
        String nonExistentFile = tempDir.resolve("non-existent.txt").toString();
        
        // when & then (예외 발생하지 않아야 함)
        FileContentUtils.cleanupFiles(nonExistentFile);
    }
}
