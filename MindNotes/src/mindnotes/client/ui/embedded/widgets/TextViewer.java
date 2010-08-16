package mindnotes.client.ui.embedded.widgets;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class TextViewer implements EmbeddedObjectWidget {

	private HTML _html;

	public TextViewer() {
		_html = new HTML();
	}

	@Override
	public void setData(String data) {
		_html.setText(data);
	}

	@Override
	public void setListener(Listener l) {

	}

	@Override
	public Widget getObjectWidget() {
		return _html;
	}

	@Override
	public String getObjectTitle() {
		return "Text";
	}

}
