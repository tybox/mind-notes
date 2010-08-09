package mindnotes.client.presentation;

public interface EmbeddedObjectView {

	interface Listener {
		public void onEmbeddedObjectViewRemove();

		public void onDataChanged(String newData);
	}

	public void setListener(Listener l);
}
