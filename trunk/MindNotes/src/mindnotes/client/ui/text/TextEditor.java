package mindnotes.client.ui.text;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.SimplePanel;

public class TextEditor extends Composite implements Focusable {

	private TinyEditor _rtArea;

	public interface Listener {
		public void onTextEditorExit();
	}

	private Listener _listener;

	public TextEditor() {

		SimplePanel container = new SimplePanel();
		_rtArea = new TinyEditor();
		container.add(_rtArea);

		initWidget(container);

		setStylePrimaryName("mindmap");
		addStyleDependentName("rte");

	}

	public String getHTML() {

		return _rtArea.getHTML();
	}

	public void setHTML(String html) {
		_rtArea.setHTML(html);

	}

	public void setListener(Listener listener) {
		_listener = listener;
	}

	public Listener getListener() {
		return _listener;
	}

	@Override
	public int getTabIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setAccessKey(char key) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFocus(boolean focused) {
		_rtArea.setFocus(focused);
	}

	@Override
	public void setTabIndex(int index) {
		// TODO Auto-generated method stub

	}

}
