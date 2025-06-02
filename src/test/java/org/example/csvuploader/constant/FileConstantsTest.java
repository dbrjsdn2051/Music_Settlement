package org.example.csvuploader.constant;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class FileConstantsTest {

    @Test
    void getBaseDirectory_정상경로반환() {
        // when
        String baseDirectory = FileConstants.getBaseDirectory();
        
        // then
        assertThat(baseDirectory).isNotNull();
        assertThat(baseDirectory).contains("csvuploader");
    }

    @Test
    void getInputFilePath_정상경로반환() {
        // when
        String inputFilePath = FileConstants.getInputFilePath();
        
        // then
        assertThat(inputFilePath).isNotNull();
        assertThat(inputFilePath).endsWith("input.csv");
        assertThat(inputFilePath).contains(FileConstants.getBaseDirectory());
    }

    @Test
    void getMelonFilePath_정상경로반환() {
        // when
        String melonFilePath = FileConstants.getMelonFilePath();
        
        // then
        assertThat(melonFilePath).isNotNull();
        assertThat(melonFilePath).endsWith("melon.csv");
        assertThat(melonFilePath).contains(FileConstants.getBaseDirectory());
    }

    @Test
    void getGenieFilePath_정상경로반환() {
        // when
        String genieFilePath = FileConstants.getGenieFilePath();
        
        // then
        assertThat(genieFilePath).isNotNull();
        assertThat(genieFilePath).endsWith("genie.csv");
    }

    @Test
    void getVibeFilePath_정상경로반환() {
        // when
        String vibeFilePath = FileConstants.getVibeFilePath();
        
        // then
        assertThat(vibeFilePath).isNotNull();
        assertThat(vibeFilePath).endsWith("vibe.csv");
    }

    @Test
    void getFloFilePath_정상경로반환() {
        // when
        String floFilePath = FileConstants.getFloFilePath();
        
        // then
        assertThat(floFilePath).isNotNull();
        assertThat(floFilePath).endsWith("flo.csv");
    }

    @Test
    void getYoutubeDirectoryPath_정상경로반환() {
        // when
        String youtubeDirPath = FileConstants.getYoutubeDirectoryPath();
        
        // then
        assertThat(youtubeDirPath).isNotNull();
        assertThat(youtubeDirPath).endsWith("youtube");
        
        // 디렉토리 생성 확인
        File youtubeDir = new File(youtubeDirPath);
        assertThat(youtubeDir).exists();
        assertThat(youtubeDir).isDirectory();
    }

    @Test
    void getVibePlusFilePath_정상경로반환() {
        // when
        String vibePlusFilePath = FileConstants.getVibePlusFilePath();
        
        // then
        assertThat(vibePlusFilePath).isNotNull();
        assertThat(vibePlusFilePath).endsWith("vibePlus.csv");
    }
}
