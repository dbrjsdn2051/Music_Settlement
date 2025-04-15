package org.example.csvuploader.constant;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@RequiredArgsConstructor
public class FileConstants {
    private static String BASE_DIRECTORY;
    private static final String INPUT_FILE_NAME = "input.csv";
    public static final String MELON_FILE_NAME = "melon.csv";
    public static final String GENIE_FILE_NAME = "genie.csv";
    public static final String VIBE_FILE_NAME = "vibe.csv";
    public static final String FLO_FILE_NAME = "flo.csv";
    public static final String VIBE_PLUS_FILE_NAME = "vibePlus.csv";
    public static final String YOUTUBE_DIRECTORY_NAME = "youtube";

    private final Environment environment;

    @PostConstruct
    private void init() {
        BASE_DIRECTORY = environment.getProperty("java.io.tmpdir") + "/csvuploader";
        createDirectoryIfNotExists(getYoutubeDirectoryPath());
    }

    private void createDirectoryIfNotExists(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public static String getBaseDirectory() {
        return BASE_DIRECTORY;
    }

    public static String getInputFilePath() {
        return BASE_DIRECTORY + File.separator + INPUT_FILE_NAME;
    }

    public static String getMelonFilePath() {
        return BASE_DIRECTORY + File.separator + MELON_FILE_NAME;
    }

    public static String getGenieFilePath() {
        return BASE_DIRECTORY + File.separator + GENIE_FILE_NAME;
    }

    public static String getVibeFilePath() {
        return BASE_DIRECTORY + File.separator + VIBE_FILE_NAME;
    }

    public static String getFloFilePath() {
        return BASE_DIRECTORY + File.separator + FLO_FILE_NAME;
    }

    public static String getYoutubeDirectoryPath() {
        return BASE_DIRECTORY + File.separator + YOUTUBE_DIRECTORY_NAME;
    }

    public static String getVibePlusFilePath() {
        return BASE_DIRECTORY + File.separator + VIBE_PLUS_FILE_NAME;
    }
}