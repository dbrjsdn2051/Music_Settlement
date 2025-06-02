# CsvUploader 테스트 코드 가이드

## 📋 프로젝트 개요

CsvUploader 프로젝트는 음악 스트리밍 플랫폼(Melon, Genie, FLO, Vibe 등)의 정산 데이터를 처리하는 Spring Boot 기반 애플리케이션입니다. 이 문서는 프로젝트에 추가된 테스트 코드들을 설명합니다.

## 🧪 테스트 구조

```
src/test/java/
├── org/example/csvuploader/
│   ├── CsvUploaderApplicationTests.java     # 애플리케이션 컨텍스트 테스트
│   ├── SimpleTest.java                      # 기본 기능 테스트
│   ├── TestConfig.java                      # 테스트 설정
│   ├── constant/
│   │   └── FileConstantsTest.java          # 파일 경로 상수 테스트
│   ├── converter/
│   │   └── ExcelConverterTestSimple.java   # Excel 변환기 테스트
│   ├── domain/
│   │   └── MelonTest.java                  # Melon 도메인 모델 테스트
│   ├── dto/transform/melon/
│   │   └── MelonCsvWriterDtoTest.java      # Melon DTO 변환 테스트
│   ├── tokenizer/
│   │   └── MelonTokenizerTest.java         # CSV 토크나이저 테스트
│   └── util/
│       ├── FileContentUtilsTest.java      # 파일 유틸리티 테스트
│       └── ZipUtilsTest.java               # 압축 유틸리티 테스트
└── resources/
    ├── application-test.yml                # 테스트 환경 설정
    └── test-data/
        └── melon-test.csv                  # 테스트 데이터
```

## ✅ 작성된 테스트 유형

### 1. **단위 테스트 (Unit Tests)**
- **도메인 모델 테스트**: `MelonTest.java`
  - 빌더 패턴을 통한 객체 생성
  - Getter/Setter 동작 확인
  - Null 값 처리

- **DTO 변환 테스트**: `MelonCsvWriterDtoTest.java`
  - CSV 읽기 DTO에서 쓰기 DTO로 변환
  - 특수 문자 처리
  - 날짜 자동 생성 확인

- **유틸리티 테스트**: `FileContentUtilsTest.java`
  - 디렉토리 생성 기능
  - 파일 삭제 기능
  - 예외 상황 처리

### 2. **컴포넌트 테스트**
- **토크나이저 테스트**: `MelonTokenizerTest.java`
  - CSV 라인 파싱
  - 따옴표 처리
  - 빈 값 처리

- **Excel 변환기 테스트**: `ExcelConverterTestSimple.java`
  - XLSX 파일 변환
  - 예외 상황 처리

### 3. **설정 테스트**
- **파일 상수 테스트**: `FileConstantsTest.java`
  - 파일 경로 설정 확인
  - 디렉토리 자동 생성 확인

### 4. **통합 테스트**
- **애플리케이션 테스트**: `CsvUploaderApplicationTests.java`
  - Spring 컨텍스트 로딩
  - 기본 애플리케이션 실행

## 🚀 테스트 실행 방법

### 전체 테스트 실행
```bash
./gradlew test
```

### 특정 테스트 클래스 실행
```bash
./gradlew test --tests "MelonTest"
```

### 테스트 결과 확인
테스트 실행 후 다음 경로에서 상세 결과를 확인할 수 있습니다:
```
build/reports/tests/test/index.html
```

## 📊 테스트 커버리지

현재 구현된 테스트들은 다음 영역을 커버합니다:

✅ **도메인 모델** - Melon 엔티티  
✅ **DTO 변환 로직** - MelonCsvWriterDto  
✅ **파일 처리 유틸리티** - FileContentUtils  
✅ **CSV 토크나이저** - MelonTokenizer  
✅ **Excel 변환기** - ExcelConverter  
✅ **설정 및 상수** - FileConstants  

## 🔧 테스트 환경 설정

### 테스트 프로파일
- 파일: `src/test/resources/application-test.yml`
- 인메모리 H2 데이터베이스 사용
- 로깅 레벨 최소화
- Spring Batch 자동 실행 비활성화

### 테스트 데이터
- 파일: `src/test/resources/test-data/melon-test.csv`
- Melon 형식의 샘플 CSV 데이터 포함

## 📝 테스트 작성 가이드

### 명명 규칙
- 테스트 메서드명: `기능설명_상황_예상결과()` (한글 사용)
- 예: `빌더패턴으로_Melon객체생성()`, `null값_처리()`

### 테스트 구조
```java
@Test
void 테스트메서드명() {
    // given - 테스트 데이터 준비
    
    // when - 테스트 실행
    
    // then - 결과 검증
}
```

### 주요 어노테이션
- `@Test`: 테스트 메서드 표시
- `@SpringBootTest`: Spring 컨텍스트가 필요한 통합 테스트
- `@ActiveProfiles("test")`: 테스트 프로파일 활성화
- `@TempDir`: 임시 디렉토리 생성 (JUnit 5)

## 🛠 향후 개선 방향

### 추가할 테스트 영역
1. **컨트롤러 테스트** - REST API 엔드포인트
2. **서비스 레이어 테스트** - 비즈니스 로직 (추가 예정)
3. **Repository 테스트** - 데이터 접근 계층 (추가 예정)
4. **통합 테스트** - 전체 배치 프로세스
5. **성능 테스트** - 대용량 파일 처리

### 테스트 품질 향상
1. **Mock 활용** - 외부 의존성 격리
2. **파라미터화 테스트** - 다양한 입력값 테스트
3. **테스트 커버리지** - Jacoco 플러그인 추가
4. **CI/CD 통합** - GitHub Actions 등

## 🔍 테스트 실행 결과

현재 구현된 테스트들은 모두 성공적으로 실행되며, 다음과 같은 기능들을 검증합니다:

- ✅ 도메인 객체 생성 및 조작
- ✅ 파일 시스템 작업 (생성, 삭제)
- ✅ CSV 데이터 파싱 및 변환
- ✅ Excel 파일 변환
- ✅ 애플리케이션 기본 설정

이러한 테스트 코드들을 통해 코드의 안정성을 보장하고, 리팩토링 시 회귀 버그를 방지할 수 있습니다.
