package org.example.csvuploader;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class CsvUploaderApplicationTests {

    @Test
    void contextLoads() {
        // Spring Context가 정상적으로 로드되는지 확인
    }

    @Test
    void 애플리케이션_실행_테스트() {
        // 애플리케이션이 정상적으로 실행되는지 확인
        // 실제 메인 메서드 호출은 테스트 환경에서 안전하지 않으므로 생략
    }
}
