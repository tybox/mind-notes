package mindnotes.client.model;

import java.util.ArrayList;
import java.util.List;

public class Node {

	private List<Node> _childNodes;
	private String _text;
	private Node _parent;
	private NodeLocation _nodeLocation;
	private boolean _expanded;

	public void setParent(Node parent) {
		_parent = parent;
	}

	public Node getParent() {
		return _parent;
	}

	public Node() {
		_childNodes = new ArrayList<Node>();
		_expanded = true;
	}

	public int getChildCount() {
		return _childNodes.size();
	}

	public void addChildNode(Node n) {
		if (!_childNodes.contains(n)) {
			_childNodes.add(n);
			n.setParent(this);
		}
	}

	public void removeChildNode(Node n) {
		_childNodes.remove(n);
		n.setParent(null);
	}

	public void setText(String text) {
		_text = text;
	}

	public String getText() {
		return _text;
	}

	public boolean hasChildNode(Node n) {
		return _childNodes.contains(n);
	}

	public Iterable<Node> getChildren() {
		return _childNodes;
	}

	public void setNodeLocation(NodeLocation nodeLocation) {
		_nodeLocation = nodeLocation;
	}

	public NodeLocation getNodeLocation() {
		return _nodeLocation;
	}

	public boolean isExpanded() {
		return _expanded;
	}

	public void setExpanded(boolean expanded) {
		_expanded = expanded;
	}
}
