package mindnotes.client.ui;

public interface NodeContainer {

	public abstract void removeNode(NodeWidget node);

	public abstract void addNode(NodeWidget node);

	public void onNodeLayoutInvalidated(NodeWidget node);

}
