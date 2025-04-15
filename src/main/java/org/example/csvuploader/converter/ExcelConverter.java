package org.example.csvuploader.converter;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStrings;
import org.springframework.stereotype.Component;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ExcelConverter {

    public void excelToCsv(String excelPath, String csvPath) throws Exception {
        // 최대 배열 크기 설정 (필요하다면 유지)
        IOUtils.setByteArrayMaxOverride(Integer.MAX_VALUE);

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(csvPath), StandardCharsets.UTF_8))) {

            // 스트리밍 방식으로 XLSX 파일 처리
            OPCPackage pkg = OPCPackage.open(new File(excelPath));
            XSSFReader reader = new XSSFReader(pkg);
            SharedStrings sst = reader.getSharedStringsTable();

            // SAX 파서 설정
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(false);
            ContentHandler handler = new SheetHandler(sst, writer);

            // XML 리더 생성 및 설정
            org.xml.sax.XMLReader parser = factory.newSAXParser().getXMLReader();
            parser.setContentHandler(handler);

            // 첫 번째 시트 처리
            InputStream sheetStream = reader.getSheetsData().next();
            InputSource sheetSource = new InputSource(sheetStream);
            try {
                parser.parse(sheetSource);
            } finally {
                sheetStream.close();
            }

            pkg.close();
            writer.flush();
        } catch (Exception e) {
            log.error("Error converting Excel to CSV", e);
            throw e;
        }
    }

    // 시트 처리를 위한 SAX 핸들러
    private class SheetHandler extends DefaultHandler {
        private final SharedStrings sst;
        private final BufferedWriter writer;
        private StringBuilder cellValue;
        private boolean isCellValueElement;
        private String cellReference;
        private String cellType;
        private int currentCol;
        private int currentRow = -1;
        private final List<String> rowData = new ArrayList<>();
        private boolean isProcessingFormula;

        public SheetHandler(SharedStrings sst, BufferedWriter writer) {
            this.sst = sst;
            this.writer = writer;
        }

        @Override
        public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
            if ("row".equals(name)) {
                // 새 행 시작
                rowData.clear();
                String rowNumStr = attributes.getValue("r");
                if (rowNumStr != null) {
                    currentRow = Integer.parseInt(rowNumStr) - 1; // 1-기반을 0-기반으로 변환
                } else {
                    currentRow++;
                }
                currentCol = 0;
            } else if ("c".equals(name)) {
                // 셀 시작
                cellReference = attributes.getValue("r");
                cellType = attributes.getValue("t");
                isProcessingFormula = false;

                // 빈 셀 채우기 - A1, C1과 같은 셀 참조에서 열 위치 계산
                if (cellReference != null) {
                    int thisCol = getCellColumn(cellReference);
                    // 빈 셀 채우기
                    while (currentCol < thisCol) {
                        rowData.add("");
                        currentCol++;
                    }
                }

                cellValue = new StringBuilder();
            } else if ("f".equals(name)) {
                // 수식 셀
                isProcessingFormula = true;
                cellValue = new StringBuilder();
            } else if ("v".equals(name) || "t".equals(name)) {
                // 값 요소
                isCellValueElement = true;
                cellValue = new StringBuilder();
            }
        }

        @Override
        public void endElement(String uri, String localName, String name) throws SAXException {
            if ("v".equals(name) || "t".equals(name)) {
                isCellValueElement = false;

                // 공유 문자열 처리
                if ("s".equals(cellType) && !isProcessingFormula) {
                    try {
                        int idx = Integer.parseInt(cellValue.toString());
                        cellValue = new StringBuilder(sst.getItemAt(idx).toString());
                    } catch (Exception e) {
                        log.warn("Failed to get shared string at index {}", cellValue, e);
                    }
                }
            } else if ("c".equals(name)) {
                // 셀 종료, 행 데이터에 추가
                String finalValue = processValueByType(cellValue.toString(), cellType);
                rowData.add(escapeCsvValue(finalValue));
                currentCol++;
            } else if ("row".equals(name)) {
                // 행 종료, CSV 행 쓰기
                try {
                    if (!isEmptyRow(rowData)) {
                        String line = String.join(",", rowData);
                        writer.write(line);
                        writer.write("\n");
                    }
                } catch (IOException e) {
                    throw new SAXException("Failed to write CSV row", e);
                }
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (isCellValueElement && !isProcessingFormula) {
                cellValue.append(ch, start, length);
            }
        }

        private String processValueByType(String value, String type) {
            if (value == null || value.isEmpty()) {
                return "";
            }

            try {
                // 공유 문자열 타입은 이미 문자열로 처리됨
                if ("s".equals(type) || "str".equals(type) || "inlineStr".equals(type)) {
                    return value.trim();
                }

                // 불리언 타입만 특별히 처리
                if ("b".equals(type)) {
                    return "1".equals(value) ? "TRUE" : "FALSE";
                }

                // 모든 다른 타입(숫자 포함)은 원래 값 그대로 반환
                return value.trim();
            } catch (Exception e) {
                log.warn("Failed to process cell value: {}", value, e);
                return value;
            }
        }

        // A1, B5 같은 셀 참조에서 열 번호 추출 (0-기반)
        private int getCellColumn(String cellReference) {
            // 숫자 제거 (A1 -> A)
            String columnReference = cellReference.replaceAll("\\d+", "");
            int column = 0;

            // A -> 0, B -> 1, Z -> 25, AA -> 26 등으로 변환
            for (int i = 0; i < columnReference.length(); i++) {
                column = column * 26 + (columnReference.charAt(i) - 'A' + 1);
            }

            return column - 1; // 0-기반 인덱스
        }
    }

    // CSV 값 이스케이프
    private String escapeCsvValue(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }

        try {
            // 값에 쉼표, 큰따옴표 또는 개행문자가 포함되어 있는 경우
            if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
                // 큰따옴표를 이스케이프하고 전체를 큰따옴표로 감싸기
                return "\"" + value.replace("\"", "\"\"") + "\"";
            }
            return value;
        } catch (Exception e) {
            log.warn("Failed to escape CSV value: {}", value);
            return "";
        }
    }

    // 빈 행인지 확인
    private boolean isEmptyRow(List<String> rowData) {
        return rowData.stream().allMatch(String::isEmpty);
    }
}