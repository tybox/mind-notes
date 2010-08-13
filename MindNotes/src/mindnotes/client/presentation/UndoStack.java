package mindnotes.client.presentation;

import java.util.LinkedList;


public class UndoStack {
	private LinkedList<UndoableAction> _stack;
	private int _actionCache = 20;

	public UndoStack() {
		_stack = new LinkedList<UndoableAction>();

	}

	public void push(UndoableAction a) {
		if (_stack.size() >= _actionCache) {
			_stack.removeLast();
		}
		_stack.addFirst(a);
	}

	public void undo() {
		if (_stack.isEmpty())
			return;
		UndoableAction a = _stack.removeFirst();
		if (a != null)
			a.undoAction();
	}

}
