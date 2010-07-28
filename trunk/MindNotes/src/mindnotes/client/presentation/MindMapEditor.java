package mindnotes.client.presentation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mindnotes.client.storage.CloudStorage;
import mindnotes.client.storage.LocalMapStorage;
import mindnotes.client.storage.Storage;
import mindnotes.shared.model.MindMap;
import mindnotes.shared.model.MindMapInfo;
import mindnotes.shared.model.Node;
import mindnotes.shared.model.NodeLocation;
import mindnotes.shared.services.UserInfo;
import mindnotes.shared.services.UserInfoService;
import mindnotes.shared.services.UserInfoServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class MindMapEditor {

	private class AddAction implements Action {

		private Node _createdNode;
		private NodeLocation _location;

		public AddAction(NodeLocation location) {
			_location = location;
		}

		@Override
		public void doAction() {
			_createdNode = addChild(_selection.getCurrentNode(), _location);
			setCurrentNode(_createdNode);
		}

		@Override
		public void undoAction() {
			deleteNode(_createdNode);
		}

	}

	private class AddSiblingAction implements Action {

		private Node _createdNode;
		private boolean _above;

		public AddSiblingAction(boolean above) {
			_above = above;
		}

		@Override
		public void doAction() {
			Node current = _selection.getCurrentNode();
			_createdNode = insertChild(current.getParent(), current, _above);

			setCurrentNode(_createdNode);
		}

		@Override
		public void undoAction() {
			deleteNode(_createdNode);
		}

	}

	private class CutAction implements Action {

		private Node _cutNode;
		private Node _parentNode;

		public CutAction() {
		}

		@Override
		public void doAction() {
			// TODO make this action work on the whole selection
			_cutNode = _selection.getCurrentNode();
			if (_cutNode == null)
				return;
			_parentNode = _cutNode.getParent();
			_clipboard.setCurrentNode(_cutNode);
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
			setCurrentNode(_cutNode);
		}

	}

	private class DeleteAction implements Action {

		private Node _parentNode;
		private Node _removedNode;

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
			setCurrentNode(_removedNode);
		}

	}

	private class ExpandAction implements Action {

		private boolean _expandOnUndo;
		private Node _subject;

		public ExpandAction(Node subject) {
			_subject = subject;
			if (_subject == null)
				throw new NullPointerException();
		}

		@Override
		public void doAction() {
			_subject.setExpanded(!_subject.isExpanded());

			_nodeViews.get(_subject).setExpanded(_subject.isExpanded());
			_expandOnUndo = !_subject.isExpanded();
		}

		@Override
		public void undoAction() {
			_subject.setExpanded(_expandOnUndo);
			_nodeViews.get(_subject).setExpanded(_expandOnUndo);
		}

	}

	private class PasteAction implements Action {

		private Node _pastedNode;

		public PasteAction() {

		}

		@Override
		public void doAction() {
			if (_clipboard.getCurrentNode() == null)
				return;
			_pastedNode = _clipboard.getCurrentNode();
			insertChild(_selection.getCurrentNode(), _pastedNode);
			setCurrentNode(_pastedNode);
		}

		@Override
		public void undoAction() {
			deleteNode(_pastedNode);
		}

	}

	private Selection _clipboard;
	private MindMap _mindMap;
	private MindMapView _mindMapView;

	private Map<Node, NodeView> _nodeViews;

	private Selection _selection;
	private UndoStack _undoStack;

	private Storage _cloudStorage;
	private Storage _localStorage;

	UserInfoServiceAsync _userInfoService = GWT.create(UserInfoService.class);

	public MindMapEditor(MindMapView mindMapView) {
		_nodeViews = new HashMap<Node, NodeView>();
		_undoStack = new UndoStack();

		_mindMapView = mindMapView;
		_mindMapView.setListener(new Gestures(this));
		_selection = new Selection();
		_clipboard = new Selection();
		_cloudStorage = new CloudStorage();
		if (com.google.code.gwt.storage.client.Storage.isSupported()) {
			_localStorage = new LocalMapStorage();
		}

	}

	/**
	 * 
	 */
	private void updateUserInfo() {
		// ask asynchronously for user info
		_userInfoService.getUserInfo(new AsyncCallback<UserInfo>() {

			@Override
			public void onFailure(Throwable caught) {
				_mindMapView.setUserInfo(null, null);
			}

			@Override
			public void onSuccess(UserInfo result) {
				_mindMapView.setUserInfo(result.getEmail(),
						result.getLogoutURL());

			}
		});
	}

	/**
	 * 
	 * @param node
	 * @param loc
	 *            Suggested node location. In current layout, if <c>node's
	 *            parent</c> is not a root node, <c>loc</c> is ignored and
	 *            parent node location is used.
	 */
	private Node addChild(Node node, NodeLocation loc) {

		Node child = new Node();
		child.setText("New node");
		if (node.getNodeLocation() == NodeLocation.ROOT) {
			child.setNodeLocation(loc);
		} else {
			child.setNodeLocation(node.getNodeLocation());
		}

		node.addChildNode(child);

		NodeView childView = _nodeViews.get(node).createChild();
		setUpNodeView(childView, child);
		return child;

	}

	private Node insertChild(Node parent, Node current, boolean above) {
		Node child = new Node();
		child.setText("New node");
		child.setNodeLocation(current.getNodeLocation());

		if (above) {
			parent.insertBefore(child, current);
		} else {
			parent.insertAfter(child, current);
		}

		NodeView parentView = _nodeViews.get(parent);
		NodeView currentView = _nodeViews.get(current);
		NodeView childView;
		if (above) {
			childView = parentView.createChildBefore(currentView);
		} else {
			childView = parentView.createChildAfter(currentView);
		}

		setUpNodeView(childView, child);
		return child;
	}

	private void deleteNode(Node node) {
		if (node.getParent() == null)
			return;

		deleteNodeView(node);

		node.getParent().removeChildNode(node);

		setCurrentNode(node.getParent());
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

	private void doUndoableAction(Action a) {
		a.doAction();
		_undoStack.push(a);
	}

	private void generateView() {
		_mindMapView.setTitle(_mindMap.getTitle());
		NodeView rootNodeView = _mindMapView.getRootNodeView();

		rootNodeView.removeAll();

		setUpNodeView(rootNodeView, _mindMap.getRootNode());
	}

	private void setUpNodeView(NodeView nodeView, Node node) {

		_nodeViews.put(node, nodeView);

		nodeView.setListener(new Gestures.NodeGestures(node, this));
		nodeView.setText(node.getText());
		nodeView.setLocation(node.getNodeLocation());
		nodeView.setExpanded(node.isExpanded());

		for (Node child : node.getChildren()) {
			setUpNodeView(nodeView.createChild(), child);
		}

	}

	public void add() {
		doUndoableAction(new AddAction(null));
	}

	public void addLeft() {
		doUndoableAction(new AddAction(NodeLocation.LEFT));
	}

	public void addRight() {
		doUndoableAction(new AddAction(NodeLocation.RIGHT));
	}

	public void addUp() {
		doUndoableAction(new AddSiblingAction(true));
	}

	public void addDown() {
		doUndoableAction(new AddSiblingAction(false));
	}

	public void cut() {
		doUndoableAction(new CutAction());
	}

	public void deleteSelection() {
		doUndoableAction(new DeleteAction(_selection.getCurrentNode()));
	}

	public void deselect() {
		// user clicked on the working area and not on any particular widget;
		// deselect
		setCurrentNode(null);
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

	public void load(MindMapInfo map, boolean local) {
		Storage s = local ? getLocalStorage() : getCloudStorage();
		s.loadMindMap(map, new AsyncCallback<MindMap>() {

			@Override
			public void onFailure(Throwable caught) {
				throw new RuntimeException(caught);
			}

			@Override
			public void onSuccess(MindMap result) {
				if (result != null)
					setMindMap(result);
				else
					throw new NullPointerException("result is null");
			}
		});
	}

	private Storage getLocalStorage() {
		if (_localStorage == null) {
			_localStorage = new LocalMapStorage();
		}
		return _localStorage;
	}

	private Storage getCloudStorage() {
		return _cloudStorage;
	}

	public void loadFromCloudWithDialog() {

		final MindMapSelectionView selectionView = _mindMapView
				.getMindMapSelectionView();

		selectionView.setListener(new MindMapSelectionView.Listener() {

			@Override
			public void mindMapChosen(MindMapInfo map, boolean local) {
				load(map, local);
			}

			@Override
			public void mindMapRemove(MindMapInfo map, boolean local) {
				remove(map, local);
			}
		});

		// show the selection (most probably a dialog) to the
		// user
		selectionView.askForCloudDocumentSelection();

		_cloudStorage.getStoredMaps(new AsyncCallback<List<MindMapInfo>>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO what happens on failure? make nice dialogs

			}

			@Override
			public void onSuccess(List<MindMapInfo> result) {
				selectionView.setMindMaps(result);
			}
		});
		_localStorage.getStoredMaps(new AsyncCallback<List<MindMapInfo>>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO what happens on failure? make nice dialogs

			}

			@Override
			public void onSuccess(List<MindMapInfo> result) {
				selectionView.setLocalMindMaps(result);
			}
		});
	}

	protected void remove(MindMapInfo map, boolean local) {
		Storage s = local ? getLocalStorage() : getCloudStorage();
		s.remove(map, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				// XXX what should happen here?
				throw new RuntimeException(caught);
			}

			@Override
			public void onSuccess(Void result) {

			}
		});
	}

	public void paste() {
		doUndoableAction(new PasteAction());
	}

	public void saveToCloud() {
		if (_mindMap.getTitle() == null) {
			String title = _mindMapView.askForDocumentTitle();
			if (title == null)
				return;
			_mindMap.setTitle(title);
		}

		_cloudStorage.saveMindMap(_mindMap, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				// XXX what should happen here?
				throw new RuntimeException(caught);
			}

			@Override
			public void onSuccess(Void result) {
				Window.alert("Successfully saved.");
			}
		});
	}

	public void setCurrentNode(Node node) {
		setCurrentNode(node, false);
	}

	private void setCurrentNode(Node node, boolean enterTextMode) {
		Node oldCurrentNode = _selection.getCurrentNode();

		// check if the new current node is different from the last one
		if (oldCurrentNode != node) {
			// update old current node ui
			if (oldCurrentNode != null) {
				_nodeViews.get(oldCurrentNode).setSelectionState(
						SelectionState.DESELECTED);
			}

			// update selection state
			_selection.setCurrentNode(node);
		}
		// update new current node ui
		if (node != null) {
			NodeView nodeView = _nodeViews.get(node);

			if (enterTextMode) {
				nodeView.setSelectionState(SelectionState.TEXT_EDITING);
			} else {
				nodeView.setSelectionState(SelectionState.CURRENT);
			}

			// TODO save the copy of actionoptionsimpl for future use
			_mindMapView.showActions(nodeView, new ActionOptionsImpl(node));
		} else {
			_mindMapView.hideActions();
		}
	}

	public void setMindMap(MindMap mindMap) {
		_mindMap = mindMap;
		generateView();
		updateUserInfo();
	}

	public void setNodeText(Node node, String newText) {
		node.setText(newText);
		_nodeViews.get(node).setText(newText);
	}

	public void toggleExpand() {
		doUndoableAction(new ExpandAction(_selection.getCurrentNode()));
	}

	public void undo() {
		_undoStack.undo();
	}

	public void enterTextMode(Node node) {
		setCurrentNode(node, true);
	}

	public void exitTextMode() {
		setCurrentNode(_selection.getCurrentNode(), false);
	}

	public void saveLocal() {

		getLocalStorage().saveMindMap(_mindMap, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.toString();

				throw new RuntimeException(caught);
			}

			@Override
			public void onSuccess(Void result) {
				Window.alert("Successfully saved.");
			}
		});

	}

	public void newMindMap() {

		DeferredCommand.addCommand(new Command() {

			@Override
			public void execute() {
				final MindMap mm = new MindMap();
				mm.setTitle("New Untitled Mind Map");
				Node n = new Node();
				n.setText("<b>New Mind Map</b>");
				Node n1 = new Node();
				n1.setText("<b>A bubble</b>");
				n1.setNodeLocation(NodeLocation.LEFT);
				Node n2 = new Node();
				n2.setText("<b>Another bubble</b>");
				n2.setNodeLocation(NodeLocation.RIGHT);
				n.addChildNode(n1);
				n.addChildNode(n2);
				mm.setRootNode(n);
				setMindMap(mm);
			}
		});
	}

	public void setTitle(String title) {
		_mindMap.setTitle(title);
		_mindMapView.setTitle(title);
	}

}
