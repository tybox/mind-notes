package mindnotes.client.presentation;

import mindnotes.client.presentation.KeyboardShortcuts.KeyBinding;
import mindnotes.client.ui.NodeWidget;
import mindnotes.shared.model.Node;

import com.google.gwt.user.client.Command;

/**
 * A Gesture is a user-initiated command. It can be a button click, a keyboard
 * press or anything like that. The Gesture class defines what action should be
 * taken by the MindMapPresenter when faced with certain gestures.
 * 
 * @author dominik
 * 
 */
public class Gestures implements MindMapView.Listener {

	private MindMapEditor _mindMapEditor;

	private KeyboardShortcuts _keyboardShortcuts;

	public Gestures(MindMapEditor mindMapPresenter) {
		_mindMapEditor = mindMapPresenter;
		if (_mindMapEditor == null)
			throw new NullPointerException();
		_keyboardShortcuts = new KeyboardShortcuts();
		initializeKeyboardBindings();

	}

	@Override
	public void deleteGesture() {
		_mindMapEditor.deleteSelection();
	}

	/**
	 * The editor area is clicked; deselect
	 */
	@Override
	public void clickGesture() {
		_mindMapEditor.deselect();
	}

	@Override
	public void addLeftGesture() {
		_mindMapEditor.addLeft();

	}

	@Override
	public void addRightGesture() {
		_mindMapEditor.addRight();

	}

	@Override
	public void addUpGesture() {
		_mindMapEditor.addUp();
	}

	@Override
	public void addDownGesture() {
		_mindMapEditor.addDown();
	}

	@Override
	public void expandGesture() {
		_mindMapEditor.toggleExpand();

	}

	@Override
	public void keyboardShortcut(int keyCode, boolean meta, boolean shiftKey,
			boolean altKey) {
		// TODO this method shows the terminology is a little messed up;
		// qualifies for some refactoring
		_keyboardShortcuts.onShortcutPressed(keyCode, meta, shiftKey, altKey);

	}

	@Override
	public void saveToCloudGesture() {
		_mindMapEditor.saveToCloud();
	}

	@Override
	public void loadFromCloudGesture() {
		_mindMapEditor.loadFromCloudWithDialog();

	}

	@Override
	public void saveLocalGesture() {
		_mindMapEditor.saveLocal();
	}

	public static class NodeGestures implements NodeView.Listener {

		private Node _node;
		private MindMapEditor _editor;

		public NodeGestures(Node node, MindMapEditor editor) {
			_node = node;
			_editor = editor;
			if (_node == null || _editor == null)
				throw new NullPointerException();
		}

		@Override
		public void nodeClickedGesture(NodeView view) {
			_editor.setCurrentNode(_node);
		}

		@Override
		public void nodeDoubleClickedGesture(NodeView sender) {
			_editor.enterTextMode(_node);
		}

		@Override
		public void nodeTextEditedGesture(NodeView view, String oldText,
				String newText) {
			_editor.setNodeText(_node, newText);
		}

		@Override
		public void nodeEditFinishedGesture(NodeWidget nodeWidget) {
			// editing of node was finished; exit node text mode
			_editor.exitTextMode();
		}

	}

	private void initializeKeyboardBindings() {
		_keyboardShortcuts.addBinding(new KeyBinding('z', true, false, false,
				new Command() {

					@Override
					public void execute() {
						_mindMapEditor.undo();
					}
				}));
		_keyboardShortcuts.addBinding(new KeyBinding('x', true, false, false,
				new Command() {

					@Override
					public void execute() {
						_mindMapEditor.cut();
					}
				}));

		_keyboardShortcuts.addBinding(new KeyBinding('v', true, false, false,
				new Command() {

					@Override
					public void execute() {
						_mindMapEditor.paste();
					}
				}));
	}

}
