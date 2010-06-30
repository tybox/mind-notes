package mindnotes.client.presentation;

import java.util.HashMap;
import java.util.Map;

import mindnotes.client.model.MindMap;
import mindnotes.client.model.Node;
import mindnotes.client.model.NodeLocation;
import mindnotes.client.presentation.KeyboardShortcuts.KeyBinding;
import mindnotes.client.ui.NodeWidget;

import com.google.gwt.user.client.Command;

public class MindMapPresenter implements MindMapView.Listener {

	public class AddAction implements Action {

		private NodeLocation _location;
		private Node _createdNode;

		public AddAction(NodeLocation location) {
			_location = location;
		}

		@Override
		public void doAction() {
			_createdNode = addChild(_selection.selectedNodeView,
					_selection.selectedNode, _location);
			selectNode(_createdNode);
		}

		@Override
		public void undoAction() {
			deleteNode(_createdNode);
		}

	}

	public class DeleteAction implements Action {

		private Node _removedNode;
		private Node _parentNode;

		public DeleteAction(Node removedNode) {
			_removedNode = removedNode;
		}

		@Override
		public void doAction() {
			_parentNode = _removedNode.getParent();
			deleteNode(_removedNode);
		}

		@Override
		public void undoAction() {
			insertChild(_parentNode, _removedNode);
			selectNode(_removedNode);
		}

	}

	public class CutAction implements Action {

		private Node _cutNode;
		private Node _parentNode;

		public CutAction() {
		}

		@Override
		public void doAction() {
			_cutNode = _selection.selectedNode;
			if (_cutNode == null)
				return;
			_parentNode = _cutNode.getParent();
			_clipboard.selectedNode = _cutNode;
			deleteNode(_cutNode);
		}

		@Override
		public void undoAction() {

			// TODO a mechanism for reporting that the action did nothing and
			// shouldn't be on the
			// undo stack
			if (_cutNode == null)
				return;
			insertChild(_parentNode, _cutNode);
			selectNode(_cutNode);
		}

	}

	public class PasteAction implements Action {

		private Node _pastedNode;

		public PasteAction() {

		}

		@Override
		public void doAction() {
			if (_clipboard.selectedNode == null)
				return;
			_pastedNode = _clipboard.selectedNode;
			insertChild(_selection.selectedNode, _pastedNode);
			selectNode(_pastedNode);
		}

		@Override
		public void undoAction() {
			deleteNode(_pastedNode);
		}

	}

	public class NodeActions implements NodeView.Listener {

		private Node _node;

		public NodeActions(Node node) {
			_node = node;
		}

		@Override
		public void nodeClicked(NodeView view) {
			selectNode(_node);
		}

		@Override
		public void nodeTextEdited(NodeView view, String oldText, String newText) {
			updateNodeText(view, _node, newText);
		}

		@Override
		public void nodeEditFinished(NodeWidget nodeWidget) {
			// editing of node was finished; deselect node
			selectNode(null);
		}

	}

	private Map<Node, NodeView> _nodeViews;
	private MindMapView _mindMapView;
	private MindMap _mindMap;
	private KeyboardShortcuts _keyboardShortcuts;
	private UndoStack _undoStack;

	private Selection _selection;
	private Selection _clipboard;

	public MindMapPresenter(MindMapView mindMapView) {
		_nodeViews = new HashMap<Node, NodeView>();
		_undoStack = new UndoStack();
		_keyboardShortcuts = new KeyboardShortcuts();
		initializeKeyboardBindings();
		_mindMapView = mindMapView;
		_mindMapView.setListener(this);
		_selection = new Selection();
		_clipboard = new Selection();
	}

	public void insertChild(Node parentNode, Node newNode) {
		if (parentNode.getNodeLocation() != NodeLocation.ROOT) {
			newNode.setNodeLocation(parentNode.getNodeLocation());
		}

		parentNode.addChildNode(newNode);

		NodeView nodeView = _nodeViews.get(parentNode);

		NodeView childView = nodeView.createChild();
		setUpNodeView(childView, newNode);
	}

	private void initializeKeyboardBindings() {
		_keyboardShortcuts.addBinding(new KeyBinding('z', true, false, false,
				new Command() {

					@Override
					public void execute() {
						_undoStack.undo();
					}
				}));
		_keyboardShortcuts.addBinding(new KeyBinding('x', true, false, false,
				new Command() {

					@Override
					public void execute() {
						CutAction action = new CutAction();
						action.doAction();
						_undoStack.push(action);
					}
				}));

		_keyboardShortcuts.addBinding(new KeyBinding('v', true, false, false,
				new Command() {

					@Override
					public void execute() {
						PasteAction action = new PasteAction();
						action.doAction();
						_undoStack.push(action);
					}
				}));
	}

	public void updateNodeText(NodeView view, Node node, String newText) {
		node.setText(newText);
		view.setText(newText);
	}

	public void selectNode(Node node) {
		if (_selection.selectedNode == node)
			return;
		if (_selection.selectedNodeView != null) {
			_selection.selectedNodeView.setSelected(false);
		}

		NodeView view = _nodeViews.get(node);

		_selection.selectedNode = node;
		_selection.selectedNodeView = view;
		if (view != null) {
			view.setSelected(true);
			_mindMapView.showActionsPanel(view, node);
		} else {
			_mindMapView.hideActionsPanel();
		}
	}

	public void setMindMap(MindMap mindMap) {
		_mindMap = mindMap;
		generateView();
	}

	private void generateView() {
		NodeView rootNodeView = _mindMapView.getRootNodeView();
		rootNodeView.removeAll();

		setUpNodeView(rootNodeView, _mindMap.getRootNode());
	}

	private void setUpNodeView(NodeView nodeView, Node node) {

		_nodeViews.put(node, nodeView);

		nodeView.setListener(new NodeActions(node));
		nodeView.setText(node.getText());
		nodeView.setLocation(node.getNodeLocation());
		nodeView.setExpanded(node.isExpanded());

		for (Node child : node.getChildren()) {
			setUpNodeView(nodeView.createChild(), child);
		}

	}

	/**
	 * 
	 * @param nodeView
	 * @param node
	 * @param loc
	 *            Suggested node location. In current layout, if <c>node's
	 *            parent</c> is not a root node, <c>loc</c> is ignored and
	 *            parent node location is used.
	 */
	private Node addChild(NodeView nodeView, Node node, NodeLocation loc) {

		Node child = new Node();
		child.setText("New node");
		if (node.getNodeLocation() == NodeLocation.ROOT) {
			child.setNodeLocation(loc);
		} else {
			child.setNodeLocation(node.getNodeLocation());
		}

		node.addChildNode(child);

		NodeView childView = nodeView.createChild();
		setUpNodeView(childView, child);
		return child;

	}

	@Override
	public void deleteGesture() {
		DeleteAction action = new DeleteAction(_selection.selectedNode);
		action.doAction();
		_undoStack.push(action);
	}

	private void deleteNode(Node node) {
		if (node.getParent() == null)
			return;

		deleteNodeView(node);

		node.getParent().removeChildNode(node);

		selectNode(node.getParent());
	}

	/**
	 * Delete NodeView for the specified Node and all children NodeViews as
	 * well.
	 * 
	 * @param node
	 */
	private void deleteNodeView(Node node) {
		for (Node child : node.getChildren()) {
			deleteNodeView(child);
		}
		NodeView nodeView = _nodeViews.get(node);

		if (nodeView != null) {
			_nodeViews.remove(nodeView);
			nodeView.delete();
		}
	}

	@Override
	public void clickGesture() {
		// user clicked on the working area and not on any particular widget;
		// deselect
		selectNode(null);
	}

	@Override
	public void addLeftGesture() {
		AddAction a = new AddAction(NodeLocation.LEFT);
		a.doAction();
		_undoStack.push(a);
	}

	@Override
	public void addRightGesture() {
		AddAction a = new AddAction(NodeLocation.RIGHT);
		a.doAction();
		_undoStack.push(a);
	}

	@Override
	public void addGesture() {
		AddAction a = new AddAction(null);
		a.doAction();
		_undoStack.push(a);
	}

	@Override
	public void expandGesture() {
		_selection.selectedNode.setExpanded(!_selection.selectedNode
				.isExpanded());
		_selection.selectedNodeView.setExpanded(_selection.selectedNode
				.isExpanded());

	}

	@Override
	public void keyboardShortcut(int keyCode, boolean meta, boolean shiftKey,
			boolean altKey) {
		// TODO this method shows the terminology is a little messed up;
		// qualifies for some refactoring
		_keyboardShortcuts.onShortcutPressed(keyCode, meta, shiftKey, altKey);

	}

}
