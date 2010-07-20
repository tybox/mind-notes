package mindnotes.client.presentation;

import java.util.HashSet;
import java.util.Set;

import mindnotes.shared.model.Node;

/**
 * This class is responsible for keeping track of which nodes are selected. A
 * selected node is a node that would be affected by cut/copy/delete operations,
 * as well as dragging. there might be more than one selected node.
 * 
 * The current node is a node that will be affected by text edit actions,
 * navigation and adding new elements (new nodes will be added relative to this
 * node). There may be at most one current node.
 * 
 * @author dominik
 * 
 */
public class Selection {
	private Set<Node> _selectedNodes;
	private Node _currentNode;

	public Selection() {
		_selectedNodes = new HashSet<Node>();
	}

	public Set<Node> getSelection() {
		return _selectedNodes;
	}

	public void setSelection(Set<Node> selectedNodes) {
		_selectedNodes.clear();
		_selectedNodes.addAll(selectedNodes);
	}

	public Node getCurrentNode() {
		return _currentNode;
	}

	public void setCurrentNode(Node currentNode) {
		_currentNode = currentNode;
	}

	public void addToSelection(Set<Node> selectedNodes) {
		_selectedNodes.addAll(selectedNodes);
	}

	public void addToSelection(Node node) {
		_selectedNodes.add(node);
	}

	public void removeFromSelection(Node node) {
		_selectedNodes.remove(node);
	}

}
