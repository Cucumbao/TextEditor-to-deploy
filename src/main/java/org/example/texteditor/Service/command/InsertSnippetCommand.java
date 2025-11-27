package org.example.texteditor.Service.command;

public class InsertSnippetCommand implements Command {

    private final String snippetContent;
    private final Integer cursorPosition;
    private String previousState;

    public InsertSnippetCommand(String snippetContent, Integer cursorPosition) {
        this.snippetContent = snippetContent;
        this.cursorPosition = cursorPosition;
    }

    @Override
    public String execute(String content) {
        this.previousState = content != null ? content : "";
        StringBuilder sb = new StringBuilder(this.previousState);
        String baseContent = this.previousState;

        boolean isValidPosition = cursorPosition != null
                && cursorPosition >= 0
                && cursorPosition <= baseContent.length();

        if (isValidPosition) {
            sb.insert(cursorPosition, snippetContent);
        } else {
            if (!sb.isEmpty() && !baseContent.endsWith("\n")) {
                sb.append("\n");
            }
            sb.append(snippetContent);
        }

        return sb.toString();
    }

    @Override
    public String undo() {
        return this.previousState;
    }
}
