package mindnotes.client.presentation;

public interface ShareOptionsView {

	public interface Listener {

		void makePublicGesture();

		void makeNotPublicGesture();

	}

	void setMapNotUploaded();

	void setMapPublished(boolean result);

	void setListener(Listener listener);

	void setLink(String url);

}
