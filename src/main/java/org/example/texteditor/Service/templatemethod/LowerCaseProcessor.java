package org.example.texteditor.Service.templatemethod;
import org.springframework.stereotype.Component;

@Component("lower")
public class LowerCaseProcessor extends TextCaseProcessor {
    @Override
    protected String convert(String text) {
        return text.toLowerCase();
    }
}
