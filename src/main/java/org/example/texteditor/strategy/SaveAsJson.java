package org.example.texteditor.strategy;

import org.example.texteditor.model.File;
import org.json.JSONObject;

public class SaveAsJson implements SaveStrategy {

    @Override
    public String formatContent(File file) {
        JSONObject obj = new JSONObject();
        obj.put("id", file.getId());
        obj.put("name", file.getFileName());
        obj.put("content", file.getContent());
        obj.put("userId", file.getUser());
        obj.put("lastUpdate", file.getLastUpdate());

        return obj.toString(4);
    }
}
