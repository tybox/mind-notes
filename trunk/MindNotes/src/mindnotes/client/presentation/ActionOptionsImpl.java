package mindnotes.client.presentation;

import mindnotes.shared.model.Node;
import mindnotes.shared.model.NodeLocation;

public class ActionOptionsImpl implements ActionOptions {

	private Node _node;

	public ActionOptionsImpl() {

	}

	public ActionOptionsImpl(Node node) {
		_node = node;
	}

	@Override
	public boolean canExpand() {
		return _node.getChildCount() > 0;
	}

	@Override
	public boolean canAddLeft() {
		return _node.getNodeLocation() == NodeLocation.LEFT
				|| _node.getNodeLocation() == NodeLocation.ROOT;
	}

	@Override
	public boolean canAddRight() {
		return _node.getNodeLocation() == NodeLocation.RIGHT
				|| _node.getNodeLocation() == NodeLocation.ROOT;
	}

	@Override
	public boolean canHaveSiblings() {
		return _node.getNodeLocation() != NodeLocation.ROOT;
	}

	@Override
	public boolean canDelete() {
		return _node.getNodeLocation() != NodeLocation.ROOT;
	}

	@Override
	public boolean isExpanded() {
		return _node.isExpanded();
	}

	public void setNode(Node node) {
		_node = node;
	}

	public Node getNode() {
		return _node;
	}

}
