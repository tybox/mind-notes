package mindnotes.client.ui;

public interface NodeContainer {

	public abstract void removeNode(NodeWidget node);

	public abstract void addNode(NodeWidget node);

	public void onNodeLayoutInvalidated(NodeWidget node);

	public abstract int getNodeRelativeLeft(NodeWidget node);

	public abstract int getNodeRelativeTop(NodeWidget node);

}
