package org.example.csvuploader.tokenizer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.file.transform.FieldSet;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MelonTokenizerTest {

    private MelonTokenizer melonTokenizer;

    @BeforeEach
    void setUp() {
        melonTokenizer = new MelonTokenizer();
        melonTokenizer.setNames(new String[]{"field1", "field2", "field3", "field4"});
    }

    @Test
    void 일반적인_CSV라인_토큰화() {
        // given
        String line = "value1,value2,value3,value4";

        // when
        FieldSet fieldSet = melonTokenizer.tokenize(line);

        // then
        assertThat(fieldSet.getFieldCount()).isEqualTo(4);
        assertThat(fieldSet.readString("field1")).isEqualTo("value1");
        assertThat(fieldSet.readString("field2")).isEqualTo("value2");
        assertThat(fieldSet.readString("field3")).isEqualTo("value3");
        assertThat(fieldSet.readString("field4")).isEqualTo("value4");
    }

    @Test
    void 따옴표가_포함된_CSV라인_토큰화() {
        // given
        String line = "\"value with, comma\",\"normal value\",\"value with \"\"quotes\"\"\",simple";

        // when
        FieldSet fieldSet = melonTokenizer.tokenize(line);

        // then
        assertThat(fieldSet.getFieldCount()).isEqualTo(4);
        assertThat(fieldSet.readString("field1")).isEqualTo("\"value with, comma\"");
        assertThat(fieldSet.readString("field2")).isEqualTo("\"normal value\"");
        assertThat(fieldSet.readString("field3")).isEqualTo("\"value with \"\"quotes\"\"\"");
        assertThat(fieldSet.readString("field4")).isEqualTo("simple");
    }

    @Test
    void 빈값이_포함된_CSV라인_토큰화() {
        // given
        String line = "value1,,value3,";

        // when
        FieldSet fieldSet = melonTokenizer.tokenize(line);

        // then
        assertThat(fieldSet.getFieldCount()).isEqualTo(4);
        assertThat(fieldSet.readString("field1")).isEqualTo("value1");
        assertThat(fieldSet.readString("field2")).isEmpty();
        assertThat(fieldSet.readString("field3")).isEqualTo("value3");
        assertThat(fieldSet.readString("field4")).isEmpty();
    }

    @Test
    void 복잡한_따옴표_조합_토큰화() {
        // given
        String line = "\"테스트곡\",\"SONG001\",\"아티스트, 이름\",\"LH001\"";

        // when
        FieldSet fieldSet = melonTokenizer.tokenize(line);

        // then
        assertThat(fieldSet.getFieldCount()).isEqualTo(4);
        assertThat(fieldSet.readString("field1")).isEqualTo("\"테스트곡\"");
        assertThat(fieldSet.readString("field2")).isEqualTo("\"SONG001\"");
        assertThat(fieldSet.readString("field3")).isEqualTo("\"아티스트, 이름\"");
        assertThat(fieldSet.readString("field4")).isEqualTo("\"LH001\"");
    }

    @Test
    void 단일필드_토큰화() {
        // given
        melonTokenizer.setNames(new String[]{"singleField"});
        String line = "single value";

        // when
        FieldSet fieldSet = melonTokenizer.tokenize(line);

        // then
        assertThat(fieldSet.getFieldCount()).isEqualTo(1);
        assertThat(fieldSet.readString("singleField")).isEqualTo("single value");
    }

    @Test
    void 공백문자_처리() {
        // given
        melonTokenizer.setNames(new String[]{"field1", "field2", "field3"});
        String line = " value1 , value2 , value3 ";

        // when
        FieldSet fieldSet = melonTokenizer.tokenize(line);

        // then
        assertThat(fieldSet.getFieldCount()).isEqualTo(3);
        assertThat(fieldSet.readString("field1")).isEqualTo("value1");
        assertThat(fieldSet.readString("field2")).isEqualTo("value2");
        assertThat(fieldSet.readString("field3")).isEqualTo("value3");
    }

    @Test
    void doTokenize_메서드_직접_테스트() {
        // given
        String line = "\"value1,with,comma\",value2,\"value3\"";

        // when
        List<String> tokens = melonTokenizer.doTokenize(line);

        // then
        assertThat(tokens).hasSize(3);
        assertThat(tokens.get(0)).isEqualTo("\"value1,with,comma\"");
        assertThat(tokens.get(1)).isEqualTo("value2");
        assertThat(tokens.get(2)).isEqualTo("\"value3\"");
    }
}
