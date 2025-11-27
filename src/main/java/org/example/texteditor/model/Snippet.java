package org.example.texteditor.model;

public class Snippet {
    private Long id;
    private String name;
    private String content;
    private Long userId;

    public Snippet() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }


    @Override
    public String toString() {
        return "model.Snippet.json{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ",\n content='" + content + '\'' +
                '}';
    }
}



