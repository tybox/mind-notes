package mindnotes.client.presentation;

import mindnotes.client.model.Node;

public interface MindMapView {
	
	public interface Listener {
		public void deleteGesture();
		public void clickGesture();
		public void addLeftGesture();
		public void addRightGesture();
		public void addGesture();
	}
	
	public void setListener(Listener l);
	
	public NodeView getRootNodeView();
	
	/**
	 * This message is sent when node layout, but not node structure, changes.
	 * Example might be a node changing its content in a way that its size changes, 
	 * affecting the layout of nodes around (which should be done automatically via CSS) and
	 * arrows (which are rendered on a canvas)
	 */
	public void nodeLayoutChanged();

	public void showActionsPanel(NodeView view, Node node);

	public void hideActionsPanel();
}
