package mindnotes.client.presentation;

/**
 * Undoable Action is an editor action that can be undone. Create a new instance
 * of a class implementing UndoableAction for each action taken by the user.
 * These actions are then put on the undo stack.
 * 
 * Some actions might, under certain conditions, not change the stade of the
 * edited document (e.g. pasting when the clipboard is empty). If the doAction()
 * method returns true, it means the document state has changed.
 * 
 * @author dominik
 * 
 */
public interface UndoableAction {
	/**
	 * Perform all operations associated with this action.
	 * 
	 * @return true if the edited document's state was changed as a result of
	 *         this action
	 */
	public boolean doAction();

	/**
	 * Undo the action. Must be called after a call to doAction(); it can be
	 * assumed that the document state is the same as AFTER the call to
	 * doAction(); undoAction() must revert the document state to the same state
	 * as before the doAction(); call.
	 */
	public void undoAction();
}
