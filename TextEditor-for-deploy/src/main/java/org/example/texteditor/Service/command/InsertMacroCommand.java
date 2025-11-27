package org.example.texteditor.Service.command;

import org.example.texteditor.Service.MacrosExecutor;

public class InsertMacroCommand implements Command {

    private final Long macroId;
    private final Integer cursorPosition;
    private MacrosExecutor macrosExecutor;

    private String previousState;
    private String cachedResult;

    public InsertMacroCommand(Long macroId, Integer cursorPosition) {
        this.macroId = macroId;
        this.cursorPosition = cursorPosition;
    }

    public void setMacrosExecutor(MacrosExecutor macrosExecutor) {
        this.macrosExecutor = macrosExecutor;
    }

    @Override
    public String execute(String content) {
        if (this.macrosExecutor == null) {
            throw new IllegalStateException("MacrosExecutor не був встановлений перед виконанням!");
        }

        this.previousState = content != null ? content : "";

        if (this.cachedResult == null) {
            try {
                this.cachedResult = macrosExecutor.executeAndReturnResult(macroId);
            } catch (Exception e) {
                throw new RuntimeException("Помилка виконання макросу: " + e.getMessage(), e);
            }
        }

        if (cachedResult == null || cachedResult.isEmpty()) return this.previousState;

        StringBuilder sb = new StringBuilder(this.previousState);
        if (cursorPosition != null && cursorPosition >= 0 && cursorPosition <= sb.length()) {
            sb.insert(cursorPosition, cachedResult);
        } else {
            if (!sb.isEmpty() && !this.previousState.endsWith("\n")) sb.append("\n");
            sb.append(cachedResult);
        }
        return sb.toString();
    }

    @Override
    public String undo() {
        return this.previousState;
    }
}