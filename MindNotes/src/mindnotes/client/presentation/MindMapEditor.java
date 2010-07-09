package mindnotes.client.presentation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mindnotes.shared.model.MindMap;
import mindnotes.shared.model.MindMapInfo;
import mindnotes.shared.model.Node;
import mindnotes.shared.model.NodeLocation;
import mindnotes.shared.services.MindmapStorageService;
import mindnotes.shared.services.MindmapStorageServiceAsync;
import mindnotes.shared.services.UserInfo;
import mindnotes.shared.services.UserInfoService;
import mindnotes.shared.services.UserInfoServiceAsync;

import com.google.gwt.core.client.GWT;
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
			_createdNode = addChild(_selection.selectedNodeView,
					_selection.selectedNode, _location);
			selectNode(_createdNode);
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
			selectNode(_removedNode);
		}

	}

	private class ExpandAction implements Action {

		private boolean _expandOnUndo;
		private Selection _subject;

		public ExpandAction(Selection subject) {
			_subject = subject;
			if (_subject == null)
				throw new NullPointerException();
		}

		@Override
		public void doAction() {
			_subject.selectedNode.setExpanded(!_subject.selectedNode
					.isExpanded());
			_subject.selectedNodeView.setExpanded(_subject.selectedNode
					.isExpanded());
			_expandOnUndo = !_subject.selectedNode.isExpanded();
		}

		@Override
		public void undoAction() {
			_subject.selectedNode.setExpanded(_expandOnUndo);
			_subject.selectedNodeView.setExpanded(_expandOnUndo);
		}

	}

	private class PasteAction implements Action {

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

	private Selection _clipboard;
	private MindMap _mindMap;
	private MindMapView _mindMapView;

	private Map<Node, NodeView> _nodeViews;

	private Selection _selection;
	private UndoStack _undoStack;

	MindmapStorageServiceAsync _mindmapStorage = GWT
			.create(MindmapStorageService.class);
	UserInfoServiceAsync _userInfoService = GWT.create(UserInfoService.class);

	public MindMapEditor(MindMapView mindMapView) {
		_nodeViews = new HashMap<Node, NodeView>();
		_undoStack = new UndoStack();

		_mindMapView = mindMapView;
		_mindMapView.setListener(new Gestures(this));
		_selection = new Selection();
		_clipboard = new Selection();
	}

	/**
	 * 
	 */
	private void updateUserInfo() {
		// ask asynchronously for user info
		_userInfoService.getUserInfo(new AsyncCallback<UserInfo>() {

			@Override
			public void onFailure(Throwable caught) {
				_mindMapView.setUserEmail(null);
				_mindMapView.setLogoutLink(null);
			}

			@Override
			public void onSuccess(UserInfo result) {
				_mindMapView.setLogoutLink(result.getLogoutURL());
				_mindMapView.setUserEmail(result.getEmail());
			}
		});
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

	private void doUndoableAction(Action a) {
		a.doAction();
		_undoStack.push(a);
	}

	private void generateView() {
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

	public void cut() {
		doUndoableAction(new CutAction());
	}

	public void deleteSelection() {
		doUndoableAction(new DeleteAction(_selection.selectedNode));
	}

	public void deselect() {
		// user clicked on the working area and not on any particular widget;
		// deselect
		selectNode(null);
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

	public void loadFromCloud(MindMapInfo map) {
		_mindmapStorage.loadMindmap(map.getKey(), new AsyncCallback<MindMap>() {

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

	public void loadFromCloudWithDialog() {

		_mindmapStorage
				.getAvailableMindmaps(new AsyncCallback<List<MindMapInfo>>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO what happens on failure? make nice dialogs

					}

					@Override
					public void onSuccess(List<MindMapInfo> result) {
						MindMapSelectionView selectionView = _mindMapView
								.getMindMapSelectionView();
						selectionView.setMindMaps(result);
						selectionView
								.setListener(new MindMapSelectionView.Listener() {

									@Override
									public void mindMapChosen(MindMapInfo map) {
										loadFromCloud(map);
									}
								});

						// show the selection (most probably a dialog) to the
						// user
						selectionView.askForCloudDocumentSelection();
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

		_mindmapStorage.saveMindmap(_mindMap, new AsyncCallback<Void>() {

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
		updateUserInfo();
	}

	public void setNodeText(Node node, String newText) {
		node.setText(newText);
		_nodeViews.get(node).setText(newText);
	}

	public void toggleExpand() {
		doUndoableAction(new ExpandAction(_selection));
	}

	public void undo() {
		_undoStack.undo();
	}

}
