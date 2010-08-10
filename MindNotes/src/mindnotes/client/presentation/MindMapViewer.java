package mindnotes.client.presentation;

import mindnotes.shared.model.EmbeddedObject;
import mindnotes.shared.model.MindMap;
import mindnotes.shared.model.Node;

public class MindMapViewer {
	private MindMapView _view;

	public void setView(MindMapView view) {
		_view = view;

	}

	private void setUpNodeView(NodeView nodeView, Node node) {

		nodeView.setText(node.getText());
		nodeView.setLocation(node.getNodeLocation());
		nodeView.setExpanded(node.isExpanded());

		for (Node child : node.getChildren()) {
			setUpNodeView(nodeView.createChild(), child);
		}

		for (EmbeddedObject object : node.getObjects()) {
			addEmbeddedObjectView(node, nodeView, object);
		}

	}

	private void addEmbeddedObjectView(final Node node, NodeView nodeView,
			final EmbeddedObject object) {
		nodeView.createEmbeddedObject(object.getType(), object.getData());

	}

	public void setMindMap(MindMap map) {
		_view.setTitle(map.getTitle());
		_view.holdLayout();
		NodeView rootNodeView = _view.getRootNodeView();
		rootNodeView.removeAll();

		setUpNodeView(rootNodeView, map.getRootNode());

		_view.resumeLayout();
	}
}
