package org.example.csvuploader.tokenizer;

import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MelonTokenizer extends DelimitedLineTokenizer {

    @Override
    protected List<String> doTokenize(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char currentChar = line.charAt(i);

            if (currentChar == '"') {
                inQuotes = !inQuotes;
                currentToken.append(currentChar);
            } else if (currentChar == ',' && !inQuotes) {
                tokens.add(currentToken.toString().trim());
                currentToken = new StringBuilder();
            } else {
                currentToken.append(currentChar);
            }
        }
        tokens.add(currentToken.toString().trim());
        return tokens;
    }
}
