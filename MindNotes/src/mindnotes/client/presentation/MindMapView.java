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

	public void showActionsPanel(NodeView view, Node node);

	public void hideActionsPanel();

	public void updateLayout();
}
