package org.example.csvuploader.util;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
public class TaskletResultStore {

    @Getter
    private static String result;

    @PostConstruct
    public void init() {
        result = "0";
    }

    public static void setResult(Double value) {
        result = String.valueOf(value);
    }

    public static void clearResult() {
        result = null;
    }
}
