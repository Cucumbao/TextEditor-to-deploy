package org.example.texteditor.Service.command;

public interface Command {
    String execute(String content);
    String undo();
}

