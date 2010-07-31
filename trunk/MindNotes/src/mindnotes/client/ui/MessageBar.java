package mindnotes.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;

public class MessageBar {

	private static MessageBarUiBinder uiBinder = GWT
			.create(MessageBarUiBinder.class);

	interface MessageBarUiBinder extends UiBinder<PopupPanel, MessageBar> {
	}

	@UiField
	PopupPanel popup;

	@UiField
	HTML messageBox;

	@UiField
	Hyperlink closeLink;

	public MessageBar() {
		uiBinder.createAndBindUi(this);
	}

	public void setMessage(String string) {
		messageBox.setText(string);
	}

	@UiHandler("closeLink")
	public void onCloseClicked(ClickEvent e) {
		popup.hide();
	}

	public void showMessageBar() {
		popup.setPopupPositionAndShow(new PositionCallback() {

			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				int w = Window.getClientWidth();
				popup.setPopupPosition((w - offsetWidth) / 2, 0);
			}
		});
	}

	public void showMessage(String text) {
		setMessage(text);
		showMessageBar();
	}
}
