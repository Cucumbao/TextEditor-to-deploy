package org.example.texteditor.model;

public class Bookmark {
    private Long id;
    private int lineNumber;
    private String description;
    private Long fileid;


    public Bookmark() {
    }

    // Геттери та сеттери
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }


    public int getLineNumber() { return lineNumber; }
    public void setLineNumber(int lineNumber) { this.lineNumber = lineNumber; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getFileid() { return fileid; }
    public void setFileid(Long fileid) { this.fileid = fileid; }

    @Override
    public String toString() {
        return "model.Bookmark.json{" +
                "id=" + id +
                ", lineNumber=" + lineNumber +
                ", description='" + description + '\'' +
                ", fileid=" + fileid +
                '}';
    }
}
