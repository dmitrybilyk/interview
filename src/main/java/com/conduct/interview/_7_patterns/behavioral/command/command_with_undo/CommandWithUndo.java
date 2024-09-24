package com.conduct.interview._7_patterns.behavioral.command.command_with_undo;

import java.util.Stack;

// Main class to demonstrate the Command pattern with undo functionality
public class CommandWithUndo {
    public static void main(String[] args) {
        // Create a TextEditor instance
        TextEditor textEditor = new TextEditor();

        // Create a CommandInvoker to handle commands
        CommandInvoker commandInvoker = new CommandInvoker();

        // Insert initial text into the text editor
        TextEditorCommand textEditorCommand = new TextEditorInsertCommand(
                textEditor, "Initial Text", textEditor.getContent().isEmpty() ? 0 : textEditor.getContent().length());
        commandInvoker.insertText(textEditorCommand);

        // Insert additional text
        TextEditorCommand textEditorCommand2 = new TextEditorInsertCommand(
                textEditor, "Additional Text", textEditor.getContent().isEmpty() ? 0 : textEditor.getContent().length());
        commandInvoker.insertText(textEditorCommand2);

        // Insert one more text
        TextEditorCommand textEditorCommand3 = new TextEditorInsertCommand(
                textEditor, "One More Text", textEditor.getContent().isEmpty() ? 0 : textEditor.getContent().length());
        commandInvoker.insertText(textEditorCommand3);

        // Print current content of the text editor
        System.out.println(textEditor.getContent());

        // Undo the last two insert operations
        commandInvoker.undoInsert();
        commandInvoker.undoInsert();

        // Print content after undoing the last two operations
        System.out.println(textEditor.getContent());
    }
}

// Class to invoke commands and handle undo operations
class CommandInvoker {
    private Stack<TextEditorCommand> textEditorCommands = new Stack<>();

    // Execute a command and push it onto the stack for undoing
    public void insertText(TextEditorCommand textEditorCommand) {
        textEditorCommand.execute(); // Execute the command (insert text)
        textEditorCommands.push(textEditorCommand); // Store the command in the stack
    }

    // Undo the last command
    public void undoInsert() {
        // Check if there is a command to undo
        if (!textEditorCommands.isEmpty()) {
            TextEditorCommand editorCommand = textEditorCommands.pop(); // Get the last command
            editorCommand.unExecute(); // Call the unExecute method to remove the last text
        }
    }
}

// Command interface for text editor operations
interface TextEditorCommand {
    void execute();   // Method to execute the command
    void unExecute(); // Method to undo the command
}

// Concrete command class for inserting text
class TextEditorInsertCommand implements TextEditorCommand {
    private TextEditor textEditor; // Reference to the text editor
    private String textToInsert;    // Text to be inserted
    private int position;           // Position at which to insert the text

    public TextEditorInsertCommand(TextEditor textEditor, String textToInsert, int position) {
        this.textEditor = textEditor;       // Initialize text editor reference
        this.textToInsert = textToInsert;   // Initialize text to insert
        this.position = position;           // Initialize position for insertion
    }

    // Execute the insert operation
    @Override
    public void execute() {
        textEditor.insertText(position, textToInsert);
    }

    // Undo the insert operation (remove the inserted text)
    @Override
    public void unExecute() {
        textEditor.deleteText(position, position + textToInsert.length());
    }
}

// Receiver class that holds the text content and performs operations on it
class TextEditor {
    private final StringBuilder textContent = new StringBuilder(); // Using StringBuilder for efficient string manipulation

    // Insert text at a specified position
    public void insertText(int position, String content) {
        textContent.insert(position, content);
    }

    // Delete text from a specified range
    public void deleteText(int positionFrom, int positionTo) {
        textContent.delete(positionFrom, positionTo);
    }

    // Get the current content of the text editor
    public String getContent() {
        return textContent.toString();
    }
}
