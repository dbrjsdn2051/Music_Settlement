package org.example.csvuploader;

import org.example.csvuploader.constant.FileConstants;
import org.example.csvuploader.domain.Melon;
import org.example.csvuploader.util.FileContentUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 프로젝트의 기본 기능들을 테스트하는 간단한 테스트 클래스
 */
class SimpleTest {

    @TempDir
    Path tempDir;

    @Test
    void Melon_도메인_객체_생성_테스트() {
        // given & when
        Melon melon = Melon.builder()
                .id(1L)
                .songName("테스트곡")
                .artist("테스트아티스트")
                .totalRoyalty(1000.0)
                .build();

        // then
        assertThat(melon.getId()).isEqualTo(1L);
        assertThat(melon.getSongName()).isEqualTo("테스트곡");
        assertThat(melon.getArtist()).isEqualTo("테스트아티스트");
        assertThat(melon.getTotalRoyalty()).isEqualTo(1000.0);
    }

    @Test
    void FileConstants_경로_설정_테스트() {
        // when
        String baseDirectory = FileConstants.getBaseDirectory();
        String inputFilePath = FileConstants.getInputFilePath();
        String melonFilePath = FileConstants.getMelonFilePath();

        // then
        assertThat(baseDirectory).isNotNull();
        assertThat(inputFilePath).contains("input.csv");
        assertThat(melonFilePath).contains("melon.csv");
        assertThat(inputFilePath).startsWith(baseDirectory);
        assertThat(melonFilePath).startsWith(baseDirectory);
    }

    @Test
    void FileContentUtils_디렉토리_생성_테스트() throws Exception {
        // given
        String testDirPath = tempDir.resolve("test-directory").toString();
        File testDir = new File(testDirPath);

        // when
        FileContentUtils.ensureDirectoryExists(testDirPath);

        // then
        assertThat(testDir).exists();
        assertThat(testDir).isDirectory();
    }

    @Test
    void FileContentUtils_파일_삭제_테스트() throws Exception {
        // given
        File testFile = tempDir.resolve("test-file.txt").toFile();
        Files.write(testFile.toPath(), "test content".getBytes());
        assertThat(testFile).exists();

        // when
        FileContentUtils.cleanupFiles(testFile.getAbsolutePath());

        // then
        assertThat(testFile).doesNotExist();
    }

    @Test
    void 문자열_기본_처리_테스트() {
        // given
        String csvLine = "\"테스트곡\",\"아티스트\",1000";

        // when
        String[] parts = csvLine.split(",");

        // then
        assertThat(parts).hasSize(3);
        assertThat(parts[0]).contains("테스트곡");
        assertThat(parts[1]).contains("아티스트");
        assertThat(parts[2]).isEqualTo("1000");
    }

    @Test
    void 기본_자바_기능_테스트() {
        // given
        String original = "  test string  ";

        // when
        String trimmed = original.trim();
        String upper = trimmed.toUpperCase();

        // then
        assertThat(trimmed).isEqualTo("test string");
        assertThat(upper).isEqualTo("TEST STRING");
    }
}
