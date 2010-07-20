package mindnotes.client.ui.text;

import mindnotes.client.ui.DialogCallback;
import mindnotes.client.ui.PopupContainer;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;

public class TextEditor extends Composite implements TextToolbar.Listener {

	private TextToolbar _toolbar;
	private StyledRichTextArea _textArea;

	/**
	 * The widget that will host the toolbar popup
	 */
	private PopupContainer _toolbarHost;
	private ImageInsertDialog _imageInsertDialog;
	private LinkInsertDialog _linkInsertDialog;

	public interface Listener {
		public void onTextEditorExit();
	}

	private Listener _listener;

	public TextEditor() {

		_toolbar = new TextToolbar();
		_toolbar.setListener(this);

		_textArea = new StyledRichTextArea("rte.css");
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
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					if (_listener != null) {
						_listener.onTextEditorExit();
					}
				}
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
		if (_imageInsertDialog == null)
			_imageInsertDialog = new ImageInsertDialog();
		_imageInsertDialog.showDialog(new DialogCallback<String>() {

			@Override
			public void dialogSuccessful(String imageURL) {
				insertImage(imageURL);
			}

			@Override
			public void dialogCancelled() {
				// no action needed on cancel
			}
		});
	}

	protected void insertImage(String imageURL) {
		_textArea.getFormatter().insertImage(imageURL);
	}

	@Override
	public void insertLink() {
		if (_linkInsertDialog == null) {
			_linkInsertDialog = new LinkInsertDialog();
		}
		_linkInsertDialog.showDialog(new DialogCallback<String[]>() {

			@Override
			public void dialogCancelled() {
			}

			@Override
			public void dialogSuccessful(String[] dr) {
				insertLink(dr[0], dr[1]);
			}
		});
	}

	protected void insertLink(String url, String text) {
		if (text != null && !text.isEmpty()) {
			// TODO potential script injection vulnerability
			_textArea.getFormatter().insertHTML(
					"<a href=\"" + URL.encode(url) + "\">" + text + "</a>");

		} else {
			_textArea.getFormatter().createLink(url);
		}
	}

	protected void setFont() {
		_textArea.getFormatter().setFontName("");
	}

	public String getHTML() {

		return _textArea.getHTML();
	}

	public void setHTML(String html) {
		_textArea.setHTML(html);

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

	public void setListener(Listener listener) {
		_listener = listener;
	}

	public Listener getListener() {
		return _listener;
	}

}
