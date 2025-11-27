package org.example.texteditor.strategy;
import org.example.texteditor.model.File;

public interface SaveStrategy {
    String formatContent(File file);
}
