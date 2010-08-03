package mindnotes.shared.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import mindnotes.shared.model.MindMapBuilder.NodeBuilder;

public class Node implements Serializable, NodeBuilder {

	private static final long serialVersionUID = 3281037573267611972L;

	private List<Node> _childNodes;
	private String _text;
	private List<EmbeddedObject> _objects;

	private Node _parent;
	private NodeLocation _nodeLocation;
	private boolean _expanded;

	public Node() {
		_childNodes = new ArrayList<Node>();
		_objects = new ArrayList<EmbeddedObject>();
		_expanded = true;
	}

	public List<EmbeddedObject> getObjects() {
		return _objects;
	}

	public void addObject(EmbeddedObject object) {
		_objects.add(object);
	}

	public void removeObject(EmbeddedObject object) {
		_objects.remove(object);
	}

	public void setParent(Node parent) {
		_parent = parent;
	}

	public Node getParent() {
		return _parent;
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

	public void insertBefore(Node newNode, Node before) {
		if (_childNodes.contains(newNode))
			return;
		int index = _childNodes.indexOf(before);
		_childNodes.add(index >= 0 ? index : 0, newNode);
		newNode.setParent(this);
	}

	public void insertAfter(Node newNode, Node after) {
		if (_childNodes.contains(newNode))
			return;
		int index = _childNodes.indexOf(after);
		_childNodes.add(index >= 0 ? index + 1 : _childNodes.size(), newNode);
		newNode.setParent(this);
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

	public List<Node> getChildren() {
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

	@Override
	public NodeBuilder createNode() {
		Node n = new Node();
		addChildNode(n);
		return n;
	}

	@Override
	public void copyTo(NodeBuilder nb) {
		nb.setExpanded(isExpanded());
		nb.setNodeLocation(getNodeLocation());
		nb.setText(getText());
		for (Node child : _childNodes) {
			child.copyTo(nb.createNode());
		}
		for (EmbeddedObject eo : _objects) {
			nb.addObject((EmbeddedObject) eo.makeClone());
		}
	}
}
