package mindnotes.client.ui.text;

import mindnotes.client.ui.PopupContainer;

import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.LinkElement;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.SimplePanel;

public class TextEditor extends Composite implements TextToolbar.Listener {

	private TextToolbar _toolbar;
	private RichTextArea _textArea;

	/**
	 * The widget that will host the toolbar popup
	 */
	private PopupContainer _toolbarHost;

	public TextEditor() {

		_toolbar = new TextToolbar();
		_toolbar.setListener(this);

		_textArea = new RichTextArea();
		_textArea.getElement().setPropertyString("border", "none");

		_textArea.addMouseUpHandler(new MouseUpHandler() {

			@Override
			public void onMouseUp(MouseUpEvent event) {
				updateButtonState();
			}
		});
		_textArea.addKeyDownHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(KeyDownEvent event) {
				updateButtonState();
			}
		});
		SimplePanel container = new SimplePanel();
		container.add(_textArea);

		initWidget(container);

		setStylePrimaryName("mindmap");
		addStyleDependentName("rte");

	}

	public void hideToolbar() {
		_toolbar.setVisible(false);
	}

	public void showToolbar() {
		if (_toolbarHost != null) {
			_toolbarHost.showPopup(this, -30, 0, _toolbar);
		}
	}

	@Override
	public void toggleUnderline() {
		_textArea.getFormatter().toggleUnderline();

	}

	@Override
	public void toggleItalic() {
		_textArea.getFormatter().toggleItalic();

	}

	@Override
	public void toggleBold() {
		_textArea.getFormatter().toggleBold();
	}

	@Override
	public void insertImage() {
		// TODO Auto-generated method stub

	}

	@Override
	public void insertLink() {
		// TODO Auto-generated method stub

	}

	public String getHTML() {

		return _textArea.getHTML();
	}

	public void setHTML(String html) {
		_textArea.setHTML(html);
		// append style link
		// CAVEAT: this works great, but is implementation-specific, and no
		// checks for NPE's are made.
		// wild debugging sessions - start here
		IFrameElement ife = IFrameElement.as(_textArea.getElement());
		LinkElement le = ife.getContentDocument().createLinkElement();
		le.setAttribute("rel", "stylesheet");
		le.setAttribute("type", "text/css");
		le.setAttribute("href", "rte.css");
		ife.getContentDocument().getElementsByTagName("head").getItem(0)
				.appendChild(le);

		updateButtonState();

	}

	protected void updateButtonState() {
		_toolbar.setBoldDown(_textArea.getFormatter().isBold());
		_toolbar.setItalicDown(_textArea.getFormatter().isItalic());
		_toolbar.setUnderlineDown(_textArea.getFormatter().isUnderlined());
	}

	public void setToolbarHost(PopupContainer toolbarHost) {
		_toolbarHost = toolbarHost;
	}

	public PopupContainer getToolbarHost() {
		return _toolbarHost;
	}

}
