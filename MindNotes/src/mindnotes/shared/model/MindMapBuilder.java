package mindnotes.shared.model;

/**
 * A MindMapBuilder is an entity that can hold information about a mind map and
 * copy that information to another MindMapBuilder.
 * 
 * @author dominik
 * 
 */
public interface MindMapBuilder {
	public interface NodeBuilder {
		public void setText(String text);

		public void setNodeLocation(NodeLocation location);

		public void setExpanded(boolean expanded);

		public NodeBuilder createNode();

		public void copyTo(NodeBuilder nb);

		void addObject(EmbeddedObject object);

	}

	public void setTitle(String title);

	public NodeBuilder createRootNode();

	public void copyTo(MindMapBuilder mmb);

}
