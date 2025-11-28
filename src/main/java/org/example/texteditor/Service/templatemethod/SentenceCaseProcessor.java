package org.example.texteditor.Service.templatemethod;

import org.springframework.stereotype.Component;

@Component("sentence")
public class SentenceCaseProcessor extends TextCaseProcessor {

    @Override
    protected String convert(String text) {
        char[] chars = text.toLowerCase().toCharArray();
        boolean capitalizeNext = true;

        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (capitalizeNext && Character.isLetter(c)) {
                chars[i] = Character.toUpperCase(c);
                capitalizeNext = false;
            }
            else if (c == '.' || c == '!' || c == '?') {
                capitalizeNext = true;
            }
        }

        return new String(chars);
    }
}
