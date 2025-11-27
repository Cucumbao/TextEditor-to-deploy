package org.example.texteditor.model;

public class Macro {
    private Long id;
    private String name;
    private String commands;
    private Long userId;

    public Macro() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCommands() { return commands; }
    public void setCommands(String command) { this.commands = command; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    @Override
    public String toString() {
        return "model.Macro{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", command='" + commands + '\'' +
                '}';
    }
}
