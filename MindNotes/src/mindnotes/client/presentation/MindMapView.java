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

		public void newMapGesture();

		public void titleChanged(String title);

		public void pasteGesture();

		public void cutGesture();

		public void copyGesture();

		public void editorExitGesture();

		public void ytVideoInsertGesture(String id);

		public void insertMenuGesture(int x, int y);

		public void imageInsertGesture(String url);

		public void mapInsertGesture();

		public void shareClickGesture();

		public boolean windowClosing();

		public void reconnectGesture();

		public void textInsertGesture();
	}

	public NodeView getRootNodeView();

	public CloudActionsView getCloudActionsView();

	public MindMapSelectionView getMindMapSelectionView();

	public ShareOptionsView showShareDialog();

	public void showInsertMenu(int x, int y, String text);

	public void setListener(Listener l);

	public void showActions(NodeView view, ActionOptions options);

	public void hideActions();

	public String askForDocumentTitle();

	public void setTitle(String title);

	public void showMessage(String string);

	public void updateLayout();

	public abstract void resumeLayout();

	public abstract void holdLayout();

}
