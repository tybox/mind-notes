package mindnotes.client.presentation;

import java.util.LinkedList;


public class UndoStack {
	private LinkedList<Action> _stack;
	private int _actionCache = 20;

	public UndoStack() {
		_stack = new LinkedList<Action>();

	}

	public void push(Action a) {
		if (_stack.size() >= _actionCache) {
			_stack.removeLast();
		}
		_stack.addFirst(a);
	}

	public void undo() {
		if (_stack.isEmpty())
			return;
		Action a = _stack.removeFirst();
		if (a != null)
			a.undoAction();
	}

}
