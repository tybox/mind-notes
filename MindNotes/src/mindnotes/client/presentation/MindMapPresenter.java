package mindnotes.client.presentation;

import mindnotes.client.model.MindMap;
import mindnotes.client.model.Node;

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
		public void nodeResize(NodeView sender) {
			_mindMapView.nodeLayoutChanged();
		}

		@Override
		public void nodeTextEdited(NodeView view, String oldText, String newText) {
			updateNodeText(view, _node, newText);
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
		if (view !=null) {
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
		_mindMapView.nodeLayoutChanged();
	}

	private void setUpNodeView(NodeView nodeView, Node node) {
		nodeView.setListener(new NodeActions(node));
		nodeView.setText(node.getText());
		nodeView.setLocation(node.getNodeLocation());
		
		for(Node child: node.getChildren()) {
			setUpNodeView(nodeView.createChild(), child);
		}
		
	}

	@Override
	public void addGesture() {
		addChild(_selection.selectedNodeView, _selection.selectedNode);
	}

	private void addChild(NodeView nodeView, Node node) {
		
		Node child = new Node();
		child.setText("New node");
		node.addChildNode(child);
		NodeView childView = nodeView.createChild();
		setUpNodeView(childView, child);
		_mindMapView.nodeLayoutChanged();
	}

	@Override
	public void deleteGesture() {
		deleteNode(_selection.selectedNodeView, _selection.selectedNode);
	}

	private void deleteNode(NodeView nodeView, Node node) {
		if (node.getParent() == null) return;
		nodeView.delete();
		
		node.getParent().removeChildNode(node);
		
		_mindMapView.nodeLayoutChanged();
		selectNode(null, null);
	}

	@Override
	public void clickGesture() {
		// user clicked on the working area and not on any particular widget; deselect
		selectNode(null, null);
	}

}
