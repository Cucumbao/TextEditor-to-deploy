package org.example.texteditor.strategy;

import org.example.texteditor.model.File;

public class SaveAsXml implements SaveStrategy {
    @Override
    public String formatContent(File file) {
        String contentSafe = "<![CDATA[" + file.getContent() + "]]>";

        return "<file>\n" +
                "  <id>" + file.getId() + "</id>\n" +
                "  <name>" + file.getFileName() + "</name>\n" +
                "  <userId>" + file.getUser() + "</userId>\n" +
                "  <lastUpdate>" + file.getLastUpdate() + "</lastUpdate>\n" +
                "  <content>" + contentSafe + "</content>\n" +
                "</file>";
    }
}