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

public class ImageInsertDialog {

	private static ImageInsertDialogUiBinder uiBinder = GWT
			.create(ImageInsertDialogUiBinder.class);

	interface ImageInsertDialogUiBinder extends
			UiBinder<DialogBox, ImageInsertDialog> {
	}

	@UiField
	Button okButton;

	@UiField
	Button cancelButton;
	
	@UiField
	TextBox urlTextBox;

	private DialogBox _dialogBox;

	private DialogCallback<String> _callback;
	

	public ImageInsertDialog() {
		_dialogBox = uiBinder.createAndBindUi(this);
	}
	
	public void showDialog(DialogCallback<String> callback) {
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
			_callback.dialogSuccessful(getImageURL());
		}
	}
	
	public String getImageURL() {
		return urlTextBox.getText();
		
	}

}
