package mindnotes.client.presentation;

public interface MindMapView {

	public interface Listener {
		public void deleteGesture();

		public void clickGesture();

		public void addLeftGesture();

		public void addRightGesture();

		public void expandGesture();

		public void keyboardShortcut(int keyCode, boolean meta,
				boolean shiftKey, boolean altKey);

		public void saveToCloudGesture();

		public void loadFromCloudGesture();

		public void addUpGesture();

		public void addDownGesture();

		public void saveLocalGesture();
	}

	public void setListener(Listener l);

	public NodeView getRootNodeView();

	public void showActions(NodeView view, ActionOptions options);

	public void hideActions();

	public void updateLayout();

	public MindMapSelectionView getMindMapSelectionView();

	public String askForDocumentTitle();

	public void setUserInfo(String email, String logoutURL);
}
