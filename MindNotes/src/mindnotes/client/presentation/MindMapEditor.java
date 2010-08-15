package mindnotes.client.presentation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mindnotes.client.storage.CloudStorage;
import mindnotes.client.storage.LocalMapStorage;
import mindnotes.client.storage.Storage;
import mindnotes.shared.model.EmbeddedObject;
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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.InvocationException;

public class MindMapEditor {

	public class DragDropAction implements UndoableAction {

		private Node _parent;

		private Node _child;
		private int _index;
		private NodeLocation _location;

		private Node _oldParent;
		private int _oldIndex;
		private NodeLocation _oldLocation;

		public DragDropAction(Node node, int index, NodeLocation location) {
			_child = node;
			_index = index;
			_location = location;

		}

		public void setParent(Node parent) {
			_parent = parent;
		}

		@Override
		public boolean doAction() {
			_oldParent = _child.getParent();

			_oldIndex = _oldParent.getChildren().indexOf(_child);
			_oldLocation = _child.getNodeLocation();

			_oldParent.removeChildNode(_child);

			_child.setNodeLocation(_location, true);

			_parent.addChildNode(_child);

			_mindMapView.holdLayout();
			NodeView childView = _nodeViews.get(_child);
			_nodeViews.get(_oldParent).moveChild(childView,
					_nodeViews.get(_parent), _index, _location);

			_mindMapView.resumeLayout();
			return _oldParent != _parent;
		}

		@Override
		public void undoAction() {

			_parent.removeChildNode(_child);

			_child.setNodeLocation(_oldLocation);

			_oldParent.addChildNode(_child);

			_mindMapView.holdLayout();

			NodeView childView = _nodeViews.get(_child);
			_nodeViews.get(_parent).moveChild(childView,
					_nodeViews.get(_oldParent), _oldIndex, _oldLocation);
			_mindMapView.resumeLayout();

		}

	}

	private class AddAction implements UndoableAction {

		private Node _createdNode;
		private NodeLocation _location;

		public AddAction(NodeLocation location) {
			_location = location;
		}

		@Override
		public boolean doAction() {
			_createdNode = addChild(_selection.getCurrentNode(), _location);
			setCurrentNode(_createdNode);
			return true;
		}

		@Override
		public void undoAction() {
			deleteNode(_createdNode);
		}

	}

	private class AddSiblingAction implements UndoableAction {

		private Node _createdNode;
		private boolean _above;

		public AddSiblingAction(boolean above) {
			_above = above;
		}

		@Override
		public boolean doAction() {
			Node current = _selection.getCurrentNode();
			_createdNode = insertChild(current.getParent(), current, _above);

			setCurrentNode(_createdNode);
			return true;
		}

		@Override
		public void undoAction() {
			deleteNode(_createdNode);
		}

	}

	private class CutAction implements UndoableAction {

		private Node _cutNode;
		private Node _parentNode;

		public CutAction() {
		}

		@Override
		public boolean doAction() {
			// TODO make this action work on the whole selection
			_cutNode = _selection.getCurrentNode();
			if (_cutNode == null)
				return false;
			_parentNode = _cutNode.getParent();
			_clipboard.setCurrentNode(_cutNode);
			deleteNode(_cutNode);

			return true;
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

	private class DeleteAction implements UndoableAction {

		private Node _parentNode;
		private Node _removedNode;

		public DeleteAction(Node removedNode) {
			_removedNode = removedNode;
		}

		@Override
		public boolean doAction() {
			_parentNode = _removedNode.getParent();
			deleteNode(_removedNode);
			return true;
		}

		@Override
		public void undoAction() {
			insertChild(_parentNode, _removedNode);
			setCurrentNode(_removedNode);
		}

	}

	private class ExpandAction implements UndoableAction {

		private boolean _expandOnUndo;
		private Node _subject;

		public ExpandAction(Node subject) {
			_subject = subject;
			if (_subject == null)
				throw new NullPointerException();
		}

		@Override
		public boolean doAction() {
			_subject.setExpanded(!_subject.isExpanded());

			_nodeViews.get(_subject).setExpanded(_subject.isExpanded());
			_expandOnUndo = !_subject.isExpanded();
			return true;
		}

		@Override
		public void undoAction() {
			_subject.setExpanded(_expandOnUndo);
			_nodeViews.get(_subject).setExpanded(_expandOnUndo);
		}

	}

	private class PasteAction implements UndoableAction {

		private Node _pastedNode;

		public PasteAction() {

		}

		@Override
		public boolean doAction() {
			if (_clipboard.getCurrentNode() == null)
				return false;
			_pastedNode = _clipboard.getCurrentNode();
			insertChild(_selection.getCurrentNode(), _pastedNode);
			setCurrentNode(_pastedNode);
			return true;
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

	private CloudStorage _cloudStorage;
	private Storage _localStorage;

	private String _currentMapKey;

	/**
	 * The editor is dirty if there are any unsaved changes in the map.
	 */
	private boolean _isDirty;

	UserInfoServiceAsync _userInfoService = GWT.create(UserInfoService.class);
	private DragDropAction _dragDropAction;

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

	public void updateUserInfo() {
		updateUserInfo(false);
	}

	/**
	 * 
	 */
	public void updateUserInfo(final boolean reconnecting) {
		// ask asynchronously for user info
		_userInfoService.getUserInfo(new AsyncCallback<UserInfo>() {

			@Override
			public void onFailure(Throwable caught) {
				// most likely a connection error;
				_mindMapView.getCloudActionsView().setOffline();
				if (reconnecting)
					_mindMapView
							.showMessage("Cannot reach MindNotes server. Are you online?");

			}

			@Override
			public void onSuccess(UserInfo result) {
				if (result.isLoggedIn()) {
					_mindMapView.getCloudActionsView().setOnline(
							result.getEmail(), result.getLogoutURL());
				} else {
					// the cloud is available, but user not logged in;
					_mindMapView.getCloudActionsView().setNotLoggedIn(
							result.getLoginURL());
				}

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

		_mindMapView.holdLayout();
		NodeView childView = _nodeViews.get(node).createChild();
		setUpNodeView(childView, child);
		_mindMapView.resumeLayout();
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
		_mindMapView.holdLayout();
		NodeView parentView = _nodeViews.get(parent);
		NodeView currentView = _nodeViews.get(current);
		NodeView childView;
		if (above) {
			childView = parentView.createChildBefore(currentView);
		} else {
			childView = parentView.createChildAfter(currentView);
		}

		setUpNodeView(childView, child);
		_mindMapView.resumeLayout();
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

	private void doUndoableAction(UndoableAction a) {
		if (a.doAction()) {
			_isDirty = true;
			System.out.println("D");
			_undoStack.push(a);
		}
	}

	private void generateView() {
		_mindMapView.setTitle(_mindMap.getTitle());
		_mindMapView.holdLayout();
		NodeView rootNodeView = _mindMapView.getRootNodeView();
		rootNodeView.removeAll();

		setUpNodeView(rootNodeView, _mindMap.getRootNode());

		_mindMapView.resumeLayout();

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

		for (EmbeddedObject object : node.getObjects()) {
			addEmbeddedObjectView(node, object);
		}

	}

	/**
	 * @param object
	 */
	private void addEmbeddedObject(Node node, EmbeddedObject object) {
		node.addObject(object);
		addEmbeddedObjectView(node, object);
	}

	/**
	 * @param node
	 * @param object
	 */
	private void addEmbeddedObjectView(final Node node,
			final EmbeddedObject object) {
		final NodeView nodeView = _nodeViews.get(node);
		final EmbeddedObjectView videoView = nodeView.createEmbeddedObject(
				object.getType(), object.getData());

		// who said Java doesn't have closures?
		videoView.setListener(new EmbeddedObjectView.Listener() {

			@Override
			public void onEmbeddedObjectViewRemove() {
				node.removeObject(object);
				nodeView.removeEmbeddedObject(videoView);
			}

			@Override
			public void onDataChanged(String newData) {
				object.setData(newData);
			}
		});
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

	public void load(final MindMapInfo map, boolean local) {
		Storage s = local ? getLocalStorage() : getCloudStorage();
		s.loadMindMap(map, new AsyncCallback<MindMap>() {

			@Override
			public void onFailure(Throwable caught) {

				if (caught instanceof InvocationException) {
					// connection to the server failed for some reason
					updateUserInfo(true);
				}

			}

			@Override
			public void onSuccess(MindMap result) {
				if (result != null) {

					setMindMap(result);
					_currentMapKey = map.getKey();
					_isDirty = false;
				} else
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
				selectionView.setMindMaps(null);

				if (caught instanceof InvocationException) {
					// connection to the server failed for some reason
					updateUserInfo(true);
				}
			}

			@Override
			public void onSuccess(List<MindMapInfo> result) {
				selectionView.setMindMaps(result);
			}
		});
		_localStorage.getStoredMaps(new AsyncCallback<List<MindMapInfo>>() {

			@Override
			public void onFailure(Throwable caught) {
				selectionView.setLocalMindMaps(null);

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
				_mindMapView
						.showMessage("Oops! There was an error during deleting. Try again later.");
				if (caught instanceof InvocationException) {
					// connection to the server failed for some reason
					updateUserInfo();
				}
			}

			@Override
			public void onSuccess(Void result) {
				/* successfully is a funny word. */
				_mindMapView.showMessage("Removed successfully.");
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

		_cloudStorage.saveMindMap(_mindMap, new AsyncCallback<MindMapInfo>() {

			@Override
			public void onFailure(Throwable caught) {
				_mindMapView
						.showMessage("Oops! There was an error during saving. Try saving locally for now."
								+ caught);
				if (caught instanceof InvocationException) {
					// connection to the server failed for some reason
					updateUserInfo(true);
				}
			}

			@Override
			public void onSuccess(MindMapInfo result) {
				_currentMapKey = result.getKey();
				_mindMapView.showMessage("Succesfully saved.");
				_isDirty = false;
			}
		});
	}

	public void setCurrentNode(Node node) {
		setCurrentNode(node, false);
	}

	private void setCurrentNode(Node node, boolean enterTextMode) {
		Node oldCurrentNode = _selection.getCurrentNode();
		_mindMapView.holdLayout();
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
		_mindMapView.resumeLayout();
	}

	public void setMindMap(MindMap mindMap) {
		_currentMapKey = null;
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

	/**
	 * Opens the text editor for the given node. If node is null, opens text
	 * editor for the current node.
	 * 
	 * @param node
	 */
	public void enterTextMode(Node node) {
		if (node == null)
			node = _selection.getCurrentNode();
		setCurrentNode(node, true);
	}

	public void exitTextMode() {
		setCurrentNode(_selection.getCurrentNode(), false);
	}

	public void saveLocal() {

		getLocalStorage().saveMindMap(_mindMap,
				new AsyncCallback<MindMapInfo>() {

					@Override
					public void onFailure(Throwable caught) {
						_mindMapView
								.showMessage("Oops! Local save was not successful. Check if local saving is supported!");
					}

					@Override
					public void onSuccess(MindMapInfo result) {
						_mindMapView
								.showMessage("Successfully saved on this device.");
						_isDirty = false;
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
				n.setText("New Mind Map");
				Node n1 = new Node();
				n1.setText("A bubble");
				n1.setNodeLocation(NodeLocation.LEFT);
				Node n2 = new Node();
				n2.setText("Another bubble");
				n2.setNodeLocation(NodeLocation.RIGHT);
				n.addChildNode(n1);
				n.addChildNode(n2);
				mm.setRootNode(n);
				setMindMap(mm);
				_isDirty = false;
			}
		});
	}

	public void setTitle(String title) {
		_mindMap.setTitle(title);
		_mindMapView.setTitle(title);
	}

	public void copy() {
		/* take note that copy (contrary to paste or cut) is not undoable*/
		Node n = new Node();
		_selection.getCurrentNode().copyTo(n);
		_clipboard.setCurrentNode(n);
	}

	public void navigateLeft() {
		navigateToSide(NodeLocation.LEFT);
	}

	public void navigateRight() {
		navigateToSide(NodeLocation.RIGHT);
	}

	/**
	 * Change current node from previous one to one of the nodes on the left or
	 * right. A node which is left or right to the current node is either one of
	 * its children or its parent.
	 * 
	 * @param where
	 */
	private void navigateToSide(NodeLocation where) {
		Node n = _selection.getCurrentNode();
		if (n.getNodeLocation() == where) {
			// we are going deeper into children, f.e. moving left from a node
			// that is on the left
			if (n.getChildCount() <= 0)
				return;
			setCurrentNode(n.getChildren().iterator().next());

		} else if (n.getNodeLocation() == NodeLocation.ROOT) {
			// moving from the root, which has no parent and children in either
			// direction
			if (n.getChildCount() <= 0)
				return;
			for (Node child : n.getChildren()) {
				if (child.getNodeLocation() == where) {
					setCurrentNode(child);
					return;
				}
			}
		} else {
			// moving towards the parent (moving right on left-sided node)
			setCurrentNode(n.getParent());
		}

	}

	public void navigateUp() {
		Node n = _selection.getCurrentNode();
		if (n.getNodeLocation() == NodeLocation.ROOT)
			return;
		Node parent = n.getParent();
		int index = parent.getChildren().indexOf(n);
		if (index > 0) {

			// search siblings for the first sibling after current node that is
			// on the same side
			for (int i = index - 1; i >= 0; i--) {
				Node candidate = parent.getChildren().get(i);
				if (candidate.getNodeLocation() == n.getNodeLocation()) {
					setCurrentNode(candidate);
					return;
				}

			}
		}
	}

	public void navigateDown() {
		Node n = _selection.getCurrentNode();
		if (n.getNodeLocation() == NodeLocation.ROOT)
			return;
		Node parent = n.getParent();
		int index = parent.getChildren().indexOf(n);
		if (index < parent.getChildCount() - 1) {

			// search siblings for the first sibling after current node that is
			// on the same side
			for (int i = index + 1; i < parent.getChildCount(); i++) {
				Node candidate = parent.getChildren().get(i);
				if (candidate.getNodeLocation() == n.getNodeLocation()) {
					setCurrentNode(candidate);
					return;
				}

			}
		}
	}

	public void insertYouTubeVideo(String id) {
		EmbeddedObject video = new EmbeddedObject("youtube", id);
		addEmbeddedObject(_selection.getCurrentNode(), video);

	}

	public void setDragDropChild(Node node, int index, NodeLocation location) {
		_dragDropAction = new DragDropAction(node, index, location);
	}

	public void setDragDropParent(Node node) {
		_dragDropAction.setParent(node);
	}

	public void doDragDropAction() {
		doUndoableAction(_dragDropAction);
		_dragDropAction = null;
	}

	public void insertImage(String url) {
		EmbeddedObject image = new EmbeddedObject("image", url);
		addEmbeddedObject(_selection.getCurrentNode(), image);

	}

	public void insertMap() {
		EmbeddedObject map = new EmbeddedObject("map", "");
		addEmbeddedObject(_selection.getCurrentNode(), map);
	}

	public void insertRichText() {
		EmbeddedObject text = new EmbeddedObject("richtext", "type text here");
		addEmbeddedObject(_selection.getCurrentNode(), text);
	}

	public void showInsertMenu(int x, int y) {
		_mindMapView
				.showInsertMenu(x, y, _selection.getCurrentNode().getText());
	}

	public void showShareDialog() {
		final ShareOptionsView view = _mindMapView.showShareDialog();
		view.setListener(new ShareOptionsView.Listener() {

			@Override
			public void makePublicGesture() {
				setMapPublic(true);
				view.setLink(getMindNotesViewerURL());

			}

			@Override
			public void makeNotPublicGesture() {
				setMapPublic(false);

			}
		});
		if (_currentMapKey == null) {
			view.setMapNotUploaded();
			return;
		}
		_cloudStorage.getService().getMapPublic(_currentMapKey,
				new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						_mindMapView
								.showMessage("Oops! There was an error during retrieving sharing preferences.");
						if (caught instanceof InvocationException) {
							// connection to the server failed for some reason
							updateUserInfo(true);
						}
					}

					@Override
					public void onSuccess(Boolean result) {
						view.setMapPublished(result);
						if (result) {
							view.setLink(getMindNotesViewerURL());

						}
					}
				});

	}

	public void setMapPublic(boolean b) {
		_cloudStorage.getService().setMapPublic(_currentMapKey, b,
				new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						_mindMapView
								.showMessage("Oops! There was an error during setting sharing preferences.");
						if (caught instanceof InvocationException) {
							// connection to the server failed for some reason
							updateUserInfo(true);
						}
					}

					@Override
					public void onSuccess(Void result) {
						_mindMapView
								.showMessage("Sharing preferences changed.");
					}
				});
	}

	/**
	 * @return
	 */
	private String getMindNotesViewerURL() {
		if (GWT.isScript()) {
			return "http://mind-notes.appspot.com/MindNotesViewer.html?map="
					+ _currentMapKey;
		} else {
			return "http://127.0.0.1:8888/MindNotesViewer.html?gwt.codesvr=127.0.0.1:9997&map="
					+ _currentMapKey;
		}
	}

	public boolean isDirty() {
		return _isDirty;
	}

}
