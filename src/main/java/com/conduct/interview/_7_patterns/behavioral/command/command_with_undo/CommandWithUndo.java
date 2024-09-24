package com.conduct.interview._7_patterns.behavioral.command.command_with_undo;

import java.util.Stack;

public class CommandWithUndo {
    public static void main(String[] args) {
        TextEditor textEditor = new TextEditor();
        CommandInvoker commandInvoker = new CommandInvoker();
        TextEditorCommand textEditorCommand = new TextEditorInsertCommand(
                textEditor, "Initial Text", textEditor.getContent().isEmpty() ? 0 : textEditor.getContent().length());
        commandInvoker.insertText(textEditorCommand);
        TextEditorCommand textEditorCommand2 = new TextEditorInsertCommand(
                textEditor, "Additional Text", textEditor.getContent().isEmpty() ? 0 : textEditor.getContent().length());
        commandInvoker.insertText(textEditorCommand2);
        TextEditorCommand textEditorCommand3 = new TextEditorInsertCommand(
                textEditor, "One More Text", textEditor.getContent().isEmpty() ? 0 : textEditor.getContent().length());
        commandInvoker.insertText(textEditorCommand3);
        System.out.println(textEditor.getContent());
        commandInvoker.undoInsert();
        commandInvoker.undoInsert();
        System.out.println(textEditor.getContent());
    }

}

class CommandInvoker {
    private Stack<TextEditorCommand> textEditorCommands = new Stack<>();

    public void insertText(TextEditorCommand textEditorCommand) {
        textEditorCommand.execute();
        textEditorCommands.push(textEditorCommand);
    }

    public void undoInsert() {
        TextEditorCommand editorCommand = textEditorCommands.pop();
        editorCommand.unExecute();
    }

}


interface TextEditorCommand {
    void execute();
    void unExecute();
}

class TextEditorInsertCommand implements TextEditorCommand {
    private TextEditor textEditor;
    private String textToInsert;
    private int position;

    public TextEditorInsertCommand(TextEditor textEditor, String textToInsert, int position) {
        this.textEditor = textEditor;
        this.textToInsert = textToInsert;
        this.position = position;
    }

    @Override
    public void execute() {
        textEditor.insertText(position, textToInsert);
    }

    @Override
    public void unExecute() {
        textEditor.deleteText(position, position + textEditor.getContent().length());
    }
}

class TextEditor {
    private final StringBuilder textContent = new StringBuilder();

    public void insertText(int position, String content) {
        textContent.insert(position, content);
    }

    public void deleteText(int positionFrom, int positionTo) {
        textContent.delete(positionFrom, positionTo);
    }

    public String getContent() {
        return textContent.toString();
    }

}
