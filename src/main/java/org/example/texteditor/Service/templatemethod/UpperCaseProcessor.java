package org.example.texteditor.Service.templatemethod;

import org.springframework.stereotype.Component;

@Component("upper")
public class UpperCaseProcessor extends TextCaseProcessor {
    @Override
    protected String convert(String text) {
        return text.toUpperCase();
    }
}
