package com.conduct.interview._7_patterns.behavioral.memento;


/**
 * Memento pattern allows to create snapshot of the object without the unveiling it's internal state
 */
public class Memento {
	public static void main(String[] args) {
		FileWriterCaretaker caretaker = new FileWriterCaretaker();

        FileWriterUtil fileWriter = new FileWriterUtil("data.txt");
        fileWriter.write("First Set of Data\n");
        System.out.println(fileWriter+"\n\n");

        // lets save the file
        caretaker.save(fileWriter);
        //now write something else
        fileWriter.write("Second Set of Data\n");

        //checking file contents
        System.out.println(fileWriter+"\n\n");

        //lets undo to last save
        caretaker.undo(fileWriter);

        //checking file content again
        System.out.println(fileWriter+"\n\n");
	}
}

class FileWriterUtil {

    private String fileName;
    private StringBuilder content;

    public FileWriterUtil(String file){
        this.fileName=file;
        this.content=new StringBuilder();
    }

    public Memento save() {
        return new Memento(this.fileName, this.content);
    }

    @Override
    public String toString() {
        return this.content.toString();
    }

    public void undoToLastSave(Object obj) {
        Memento memento = (Memento) obj;
        this.fileName = memento.fileName;
        this.content = memento.content;
    }

    public void write(String str) {
        content.append(str);
    }

    private class Memento {
        private String fileName;
        private StringBuilder content;

        public Memento(String file, StringBuilder content) {
            this.fileName = file;
            //notice the deep copy so that Memento and FileWriterUtil content variables don't refer to same object
            this.content = new StringBuilder(content);
        }
    }
}

class FileWriterCaretaker {

    private Object obj;

    public void save(FileWriterUtil fileWriter) {
        this.obj = fileWriter.save();
    }

    public void undo(FileWriterUtil fileWriter) {
        fileWriter.undoToLastSave(obj);
    }
}