package org.example.texteditor.model;

public class File {
    private Long id;
    private String fileName;
    private String filePath;
    private String content;
    private Long userid;
    private String lastUpdate;

    public File() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getFilePath() { return filePath; }

    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }


    public Long getUser() { return userid; }
    public void setUser(Long user) { this.userid = user; }

    public String getLastUpdate() { return lastUpdate; }
    public void setLastUpdate(String lastUpdate) { this.lastUpdate = lastUpdate; }

    @Override
    public String toString() {
        return "model.File.json{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", user=" + (userid) +
                ", content=" + (content) +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}
