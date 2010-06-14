package mindnotes.client.model;

public class MindMap {
	private Node _rootNode;
	
	public MindMap() {
		_rootNode = new Node();
		_rootNode.setText("Root");
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
	}
	
	
}
