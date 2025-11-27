package org.example.texteditor.Service.command;

import org.example.texteditor.Service.MacrosExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Stack;

@Service
@SessionScope
public class CommandHistory {

    private final Stack<Command> undoStack = new Stack<>();
    private final Stack<Command> redoStack = new Stack<>();
    private final MacrosExecutor macrosExecutor;

    public CommandHistory(MacrosExecutor macrosExecutor) {
        this.macrosExecutor = macrosExecutor;
    }

    public String execute(Command command, String currentContent) {
        if (command instanceof InsertMacroCommand) {
            ((InsertMacroCommand) command).setMacrosExecutor(this.macrosExecutor);
        }
        String newContent = command.execute(currentContent);
        undoStack.push(command);
        redoStack.clear();
        return newContent;
    }


    public String undo(String currentContent) {
        if (undoStack.isEmpty()) return currentContent;
        Command command = undoStack.pop();
        String restoredContent = command.undo();
        redoStack.push(command);
        return restoredContent;
    }

    public String redo(String currentContent) {
        if (redoStack.isEmpty()) return currentContent;
        Command command = redoStack.pop();
        String newContent = command.execute(currentContent);
        undoStack.push(command);
        return newContent;
    }
}