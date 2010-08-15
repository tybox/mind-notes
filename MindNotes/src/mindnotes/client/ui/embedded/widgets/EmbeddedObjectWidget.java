package mindnotes.client.ui.embedded.widgets;

import com.google.gwt.user.client.ui.Widget;

public interface EmbeddedObjectWidget {
	public interface Listener {
		public void dataChanged(String newData);

		public void layoutChanged();
	}

	public void setData(String data);

	public void setListener(Listener l);

	public Widget getObjectWidget();

	public String getObjectTitle();
}
