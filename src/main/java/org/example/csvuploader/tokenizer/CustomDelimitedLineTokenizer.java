package org.example.csvuploader.tokenizer;

import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;

public class CustomDelimitedLineTokenizer extends DelimitedLineTokenizer {

    private static final char DEFAULT_QUOTE_CHARACTER = '"';

    @Override
    protected boolean isQuoteCharacter(char c) {
        return c == DEFAULT_QUOTE_CHARACTER;
    }
}
