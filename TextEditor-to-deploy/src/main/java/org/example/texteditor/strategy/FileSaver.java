package org.example.texteditor.strategy;
import org.example.texteditor.model.File;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileSaver {
    private SaveStrategy strategy;

    public void setStrategy(SaveStrategy strategy) {
        this.strategy = strategy;
    }

    public void save(File file) {
        if (strategy == null) {
            System.out.println("⚠️ Стратегію не вибрано. Використовуємо TXT.");
            this.strategy = new SaveAsTxt();
        }

        String contentToWrite = strategy.formatContent(file);
        String pathStr = file.getFilePath();

        if (pathStr == null || pathStr.isBlank()) {
            System.out.println("ℹ️ Шлях до файлу не вказано. Фізичне збереження пропущено.");
            return;
        }

        try {
            Path path = Paths.get(pathStr);
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            try (FileWriter writer = new FileWriter(path.toFile(), StandardCharsets.UTF_8)) {
                writer.write(contentToWrite);
            }

            System.out.println("✅ Файл фізично оновлено за шляхом: " + path.toAbsolutePath());

        } catch (IOException e) {
            System.err.println("❌ Помилка запису файлу на диск: " + e.getMessage());
            e.printStackTrace();
        }
    }
}