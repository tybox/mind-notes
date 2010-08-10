package mindnotes.client.ui;

import mindnotes.client.presentation.ShareOptionsView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class ShareOptionsDialog extends PopupPanel implements ShareOptionsView {

	private static ShareOptionsDialogUiBinder uiBinder = GWT
			.create(ShareOptionsDialogUiBinder.class);

	interface ShareOptionsDialogUiBinder extends
			UiBinder<Widget, ShareOptionsDialog> {
	}

	private Listener _listener;

	@UiField
	CheckBox publicCheckBox;

	@UiField
	Label statusLabel;

	@UiField
	Anchor shareLink;

	public ShareOptionsDialog() {
		setStylePrimaryName("share-opts");
		setWidget(uiBinder.createAndBindUi(this));
		setAutoHideEnabled(true);

	}

	@Override
	public void setMapNotUploaded() {
		statusLabel.setText("To share a map, it must be saved to cloud first.");
		publicCheckBox.setEnabled(false);
	}

	@Override
	public void setMapPublished(boolean result) {
		publicCheckBox.setValue(result, false);
		publicCheckBox.setEnabled(true);
		updateStatus();
	}

	private void updateStatus() {
		statusLabel
				.setText(publicCheckBox.getValue() ? "This map is public and can be accessed by anyone by visiting this link:"
						: "A public mind map would be accessible by anyone who visits a specified URL.");
		shareLink.setVisible(publicCheckBox.getValue());

	}

	@UiHandler("publicCheckBox")
	public void valueChanged(ValueChangeEvent<Boolean> e) {
		updateStatus();
		if (_listener != null) {
			if (e.getValue()) {
				_listener.makePublicGesture();
			} else {
				_listener.makeNotPublicGesture();
			}
		}
	}

	@Override
	public void setListener(Listener listener) {
		_listener = listener;
	}

	public void setLink(String url) {
		String vurl = url;
		if (vurl.length() > 36) {
			vurl = vurl.substring(0, 20) + " ... "
					+ vurl.substring(vurl.length() - 12);
		}
		shareLink.setText(vurl);
		shareLink.setHref(url);
	}

}
