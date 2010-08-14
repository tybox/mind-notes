package mindnotes.client.presentation;

public interface CloudActionsView {

	public interface Listener {
		public void newClicked();

		public void loadFromCloudClicked();

		public void saveToCloudClicked();

		public void saveLocalClicked();

		public void shareClicked();

		public void tryAgainClicked();
	}

	public void setListener(Listener listener);

	public void setOnline(String email, String logoutURL);

	public void setNotLoggedIn(String loginURL);

	public void setOffline();

}