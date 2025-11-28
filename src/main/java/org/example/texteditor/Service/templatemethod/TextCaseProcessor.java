package org.example.texteditor.Service.templatemethod;

public abstract class TextCaseProcessor {

    public final String processText(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        String result = convert(text);

        return result;
    }

    protected abstract String convert(String text);
}
