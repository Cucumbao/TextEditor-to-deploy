package org.example.texteditor.strategy;

import org.example.texteditor.model.File;

public class SaveAsTxt implements SaveStrategy {
    @Override
    public String formatContent(File file) {
        return file.getContent();
    }
}
