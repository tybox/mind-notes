package mindnotes.client.ui.text;

import mindnotes.client.ui.DialogCallback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;

public class LinkInsertDialog {

	private static LinkInsertDialogUiBinder uiBinder = GWT
			.create(LinkInsertDialogUiBinder.class);

	interface LinkInsertDialogUiBinder extends
			UiBinder<DialogBox, LinkInsertDialog> {
	}

	@UiField
	Button okButton;

	@UiField
	Button cancelButton;
	
	@UiField
	TextBox urlTextBox;
	
	@UiField
	TextBox captionTextBox;

	private DialogBox _dialogBox;

	private DialogCallback<String[]> _callback;
	

	public LinkInsertDialog() {
		_dialogBox = uiBinder.createAndBindUi(this);
	}
	
	public void showDialog(DialogCallback<String[]> callback) {
		_dialogBox.setPopupPositionAndShow(new PositionCallback() {
			
			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				// place the dialog in the center of client area
				int w = (Window.getClientWidth() - offsetWidth)/2;
				int h = (Window.getClientHeight() - offsetHeight)/2;
				_dialogBox.setPopupPosition(w, h);
			}
		});
		
		_callback = callback;
	}
	
	@UiHandler("cancelButton")
	public void onCancelClicked(ClickEvent event) {
		_dialogBox.hide();
		if (_callback != null) {
			_callback.dialogCancelled();
		}
	}
	
	@UiHandler("okButton")
	public void onOkClicked(ClickEvent event) {
		_dialogBox.hide();
		if (_callback != null) {
			_callback.dialogSuccessful(new String[] {getImageURL(), getLinkText()});
		}
	}
	
	public String getImageURL() {
		return urlTextBox.getText();
		
	}
	public String getLinkText() {
		return captionTextBox.getText();
		
	}

}
