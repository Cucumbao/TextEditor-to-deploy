package org.example.texteditor.Service;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class DocxService {

    public String readDocx(InputStream inputStream) {
        try (XWPFDocument document = new XWPFDocument(inputStream);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            return extractor.getText();

        } catch (IOException e) {
            e.printStackTrace();
            return "Помилка читання файлу Word: " + e.getMessage();
        }
    }
}
