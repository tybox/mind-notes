package mindnotes.shared.model;

import java.io.Serializable;

public class MindMap implements Serializable, MindMapBuilder {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2262820174504249978L;

	private Node _rootNode;
	private String _title;

	public MindMap() {
		_rootNode = new Node();
		_rootNode.setText("Root");
		_rootNode.setNodeLocation(NodeLocation.ROOT);
	}

	/**
	 * 
	 * @return
	 */
	public Node getRootNode() {
		return _rootNode;
	}

	/**
	 * 
	 * @param rootNode
	 */
	public void setRootNode(Node rootNode) {
		_rootNode = rootNode;
		_rootNode.setNodeLocation(NodeLocation.ROOT);
	}

	public void setTitle(String title) {
		_title = title;
	}

	public String getTitle() {
		return _title;
	}

	@Override
	public NodeBuilder createRootNode() {
		return _rootNode;
	}

	@Override
	public void copyTo(MindMapBuilder mmb) {
		mmb.setTitle(getTitle());
		getRootNode().copyTo(mmb.createRootNode());
	}

}
