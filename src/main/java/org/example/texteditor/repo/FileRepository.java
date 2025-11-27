package org.example.texteditor.repo;

import org.example.texteditor.db.FileDAO;
import org.example.texteditor.model.File;
import org.example.texteditor.strategy.FileSaver;
import org.example.texteditor.strategy.SaveAsJson;
import org.example.texteditor.strategy.SaveAsTxt;
import org.example.texteditor.strategy.SaveAsXml;

import java.util.List;

public class FileRepository implements Repository<File> {
    private final FileDAO fileDAO;

    public FileRepository(FileDAO fileDAO) {
        this.fileDAO = fileDAO;
    }

    @Override
    public List<File> findAll() {
        List<File> files = fileDAO.getAllFiles();
        System.out.println(files);
        return files;
    }

    @Override
    public File findById(Long id) {
        File file = fileDAO.getFileById(id);
        if (file != null) {
            System.out.println(file);
            return file;
        } else {
            System.out.println("❌ Файл з id=" + id + " не знайдено.");
            return null;
        }
    }

    @Override
    public void save(File file) {
        fileDAO.saveFile(file);
        System.out.println("💾 Файл збережено або оновлено.");
        if (file.getFilePath() != null && !file.getFilePath().isBlank()) {

            try {
                FileSaver fileSaver = new FileSaver();
                String fileName = file.getFileName().toLowerCase();

                if (fileName.endsWith(".json")) {
                    fileSaver.setStrategy(new SaveAsJson());
                } else if (fileName.endsWith(".xml")) {
                    fileSaver.setStrategy(new SaveAsXml());
                } else {
                    fileSaver.setStrategy(new SaveAsTxt());
                }
                fileSaver.save(file);

            } catch (Exception e) {
                System.err.println("❌ [Disk] Не вдалося зберегти файл на диск: " + e.getMessage());
            }
        }
    }



    @Override
    public boolean delete(Long id) {
        boolean deleted = fileDAO.deleteFile(id);
        if (deleted) {
            System.out.println("🗑️ Файл з id=" + id + " видалено.");
        } else {
            System.out.println("❌ Файл з id=" + id + " не знайдено.");
        }
        return deleted;
    }
}
