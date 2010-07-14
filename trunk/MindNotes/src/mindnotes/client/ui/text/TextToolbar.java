package mindnotes.client.ui.text;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public class TextToolbar extends Composite {

	private static TextFormattingPanelUiBinder uiBinder = GWT
			.create(TextFormattingPanelUiBinder.class);

	interface TextFormattingPanelUiBinder extends UiBinder<Widget, TextToolbar> {
	}

	interface Listener {

		public void toggleBold();

		public void toggleItalic();

		public void toggleUnderline();

		public void insertImage();

		public void insertLink();

	}

	private Listener _listener;

	@UiField
	ToggleButton boldButton;

	@UiField
	ToggleButton italicButton;

	@UiField
	ToggleButton underlineButton;

	public TextToolbar() {

		initWidget(uiBinder.createAndBindUi(this));

	}

	@UiHandler("boldButton")
	public void boldClicked(ClickEvent ce) {
		if (_listener == null)
			return;
		_listener.toggleBold();
	}

	@UiHandler("italicButton")
	public void italicClicked(ClickEvent ce) {
		if (_listener == null)
			return;
		_listener.toggleItalic();
	}

	@UiHandler("underlineButton")
	public void underlineClicked(ClickEvent ce) {
		if (_listener == null)
			return;
		_listener.toggleUnderline();
	}

	@UiHandler("imageButton")
	public void insertImageClicked(ClickEvent ce) {
		if (_listener == null)
			return;
		_listener.insertImage();
	}

	@UiHandler("linkButton")
	public void insertLinkClicked(ClickEvent ce) {
		if (_listener == null)
			return;
		_listener.insertLink();
	}

	public void setBoldDown(boolean bold) {
		boldButton.setDown(bold);
	}

	public void setItalicDown(boolean italic) {
		italicButton.setDown(italic);
	}

	public void setUnderlineDown(boolean underlined) {
		underlineButton.setDown(underlined);
	}

	public void setListener(Listener listener) {
		_listener = listener;
	}

	public Listener getListener() {
		return _listener;
	}

}
