package org.example.texteditor.Service.command;

public class UpdateContentCommand implements Command {

    private final String newContent;
    private String previousState;

    public UpdateContentCommand(String newContent) {
        this.newContent = newContent;
    }

    @Override
    public String execute(String currentContent) {
        this.previousState = currentContent != null ? currentContent : "";
        return this.newContent;
    }

    @Override
    public String undo() {
        return this.previousState;
    }
}
