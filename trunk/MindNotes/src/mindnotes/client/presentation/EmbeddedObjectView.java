package mindnotes.client.presentation;

public interface EmbeddedObjectView {

	interface Listener {
		public void onEmbeddedObjectViewRemove();
	}

	public void setListener(Listener l);
}
