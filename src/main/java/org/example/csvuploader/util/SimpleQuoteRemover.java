package org.example.csvuploader.util;

import java.lang.reflect.Field;

public class SimpleQuoteRemover {
    /**
     * 리플렉션을 사용하여 DTO의 모든 문자열 필드에서 앞뒤 3개 따옴표 제거
     */
    public static <T> T removeTripleQuotes(T dto) throws IllegalAccessException {
        if (dto == null) {
            return null;
        }

        Class<?> dtoClass = dto.getClass();
        Field[] fields = dtoClass.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            if (field.getType() == String.class) {
                String value = (String) field.get(dto);
                if (value != null && value.length() >= 6) { // 최소 6글자(앞뒤 각 3개 따옴표) 이상
                    if (value.startsWith("\"\"\"") && value.endsWith("\"\"\"")) {
                        // 앞 3개, 뒤 3개 따옴표 제거
                        String processed = value.substring(2, value.length() - 2);
                        field.set(dto, processed);
                    }
                }
            }
        }

        return dto;
    }

    public static <T> T removeSingleQuotes(T dto) throws IllegalAccessException {
        if (dto == null) {
            return null;
        }

        Class<?> dtoClass = dto.getClass();
        Field[] fields = dtoClass.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            if (field.getType() == String.class) {
                String value = (String) field.get(dto);
                if (value != null && value.length() >= 2) {
                    if (value.startsWith("\"") && value.endsWith("\"")) {
                        String substring = value.substring(1, value.length() - 1);
                        field.set(dto, substring);
                    }
                }
            }
        }

        return dto;
    }

}
