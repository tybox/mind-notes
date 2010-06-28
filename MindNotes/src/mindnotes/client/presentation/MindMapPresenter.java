package mindnotes.client.presentation;

import mindnotes.client.model.MindMap;
import mindnotes.client.model.Node;
import mindnotes.client.model.NodeLocation;
import mindnotes.client.ui.NodeWidget;

public class MindMapPresenter implements MindMapView.Listener {

	public class NodeActions implements NodeView.Listener {

		private Node _node;

		public NodeActions(Node node) {
			_node = node;
		}

		@Override
		public void nodeClicked(NodeView view) {
			selectNode(view, _node);
		}

		@Override
		public void nodeTextEdited(NodeView view, String oldText, String newText) {
			updateNodeText(view, _node, newText);
		}

		@Override
		public void nodeEditFinished(NodeWidget nodeWidget) {
			// editing of node was finished; deselect node
			selectNode(null, null);
		}

	}

	private MindMapView _mindMapView;
	private MindMap _mindMap;

	private Selection _selection;

	public MindMapPresenter(MindMapView mindMapView) {
		_mindMapView = mindMapView;
		_mindMapView.setListener(this);
		_selection = new Selection();
	}

	public void updateNodeText(NodeView view, Node node, String newText) {
		node.setText(newText);
		view.setText(newText);
	}

	public void selectNode(NodeView view, Node node) {
		if (_selection.selectedNodeView != null) {
			_selection.selectedNodeView.setSelected(false);
		}
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
		nodeView.setListener(new NodeActions(node));
		nodeView.setText(node.getText());
		nodeView.setLocation(node.getNodeLocation());

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
	private void addChild(NodeView nodeView, Node node, NodeLocation loc) {

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
		selectNode(childView, child);
	}

	@Override
	public void deleteGesture() {
		deleteNode(_selection.selectedNodeView, _selection.selectedNode);
	}

	private void deleteNode(NodeView nodeView, Node node) {
		if (node.getParent() == null)
			return;
		nodeView.delete();

		node.getParent().removeChildNode(node);

		selectNode(null, null);
	}

	@Override
	public void clickGesture() {
		// user clicked on the working area and not on any particular widget;
		// deselect
		selectNode(null, null);
	}

	@Override
	public void addLeftGesture() {
		addChild(_selection.selectedNodeView, _selection.selectedNode,
				NodeLocation.LEFT);
	}

	@Override
	public void addRightGesture() {
		addChild(_selection.selectedNodeView, _selection.selectedNode,
				NodeLocation.RIGHT);
	}

	@Override
	public void addGesture() {
		addChild(_selection.selectedNodeView, _selection.selectedNode, null);
	}

	@Override
	public void expandGesture() {
		_selection.selectedNodeView.toggleExpansion();

	}

}
