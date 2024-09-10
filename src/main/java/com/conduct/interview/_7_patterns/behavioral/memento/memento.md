Memento is a pattern whose goal is to make it possible for Originator object to save it's state 
to be restored later. Memento is an internal class with the same fields as the Originator.
Caretaker uses save/restore methods of the Originator to save and restore memento object from 
itself.