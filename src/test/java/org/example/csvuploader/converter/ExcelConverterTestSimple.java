package org.example.csvuploader.converter;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExcelConverterSimpleTest {

    private ExcelConverter excelConverter;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        excelConverter = new ExcelConverter();
    }

    @Test
    void XLSX파일을_CSV로_변환_성공() throws Exception {
        // given
        File excelFile = createTestXlsxFile();
        File csvFile = tempDir.resolve("output.csv").toFile();

        // when
        excelConverter.excelToCsv(excelFile.getAbsolutePath(), csvFile.getAbsolutePath());

        // then
        assertThat(csvFile).exists();
        List<String> lines = Files.readAllLines(csvFile.toPath());
        assertThat(lines).isNotEmpty();
    }

    @Test
    void 존재하지않는_파일_예외처리() {
        // given
        String nonExistentFile = tempDir.resolve("non-existent.xlsx").toString();
        String outputFile = tempDir.resolve("output.csv").toString();

        // when & then
        assertThatThrownBy(() -> excelConverter.excelToCsv(nonExistentFile, outputFile))
                .isInstanceOf(Exception.class);
    }

    private File createTestXlsxFile() throws IOException {
        File file = tempDir.resolve("test.xlsx").toFile();

        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream fos = new FileOutputStream(file)) {

            Sheet sheet = workbook.createSheet("TestSheet");

            // 헤더 행
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Name");
            headerRow.createCell(1).setCellValue("Age");

            // 데이터 행
            Row dataRow = sheet.createRow(1);
            dataRow.createCell(0).setCellValue("John");
            dataRow.createCell(1).setCellValue(25);

            workbook.write(fos);
        }

        return file;
    }
}
