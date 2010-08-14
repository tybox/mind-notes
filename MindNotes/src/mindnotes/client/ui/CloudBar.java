package mindnotes.client.ui;

import mindnotes.client.presentation.CloudActionsView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * The Cloud Bar is the collection of links on the top-right site of the client
 * that allows access to cloud features.
 * 
 * @author dominik
 * 
 */
public class CloudBar extends Composite implements CloudActionsView {

	private static CloudBarUiBinder uiBinder = GWT
			.create(CloudBarUiBinder.class);

	interface CloudBarUiBinder extends UiBinder<Widget, CloudBar> {
	}

	private Listener _listener;

	@UiField
	Label greetLabel;
	@UiField
	Anchor newButton;
	@UiField
	Anchor loadButton;
	@UiField
	Anchor saveButton;
	@UiField
	Anchor saveLocalButton;
	@UiField
	Anchor shareButton;
	@UiField
	Anchor logoutLink;
	@UiField
	Anchor loginLink;
	@UiField
	Anchor tryAgainLink;
	@UiField
	Label offlineLabel;
	@UiField
	Label notLoggedInLabel;

	public CloudBar() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("newButton")
	public void onNewClicked(ClickEvent event) {
		if (_listener != null) {
			_listener.newClicked();
		}
	}

	@UiHandler("loadButton")
	public void onLoadClicked(ClickEvent event) {
		if (_listener != null) {
			_listener.loadFromCloudClicked();
		}
	}

	@UiHandler("saveButton")
	public void onSaveClicked(ClickEvent event) {
		if (_listener != null) {
			_listener.saveToCloudClicked();
		}
	}

	@UiHandler("saveLocalButton")
	public void onSaveLocalClicked(ClickEvent event) {
		if (_listener != null) {
			_listener.saveLocalClicked();
		}
	}

	@UiHandler("shareButton")
	public void onShareClicked(ClickEvent event) {
		if (_listener != null) {
			_listener.shareClicked();
		}
	}

	@UiHandler("tryAgainLink")
	public void onTryAgainClicked(ClickEvent event) {
		if (_listener != null) {
			_listener.tryAgainClicked();
		}
	}

	/* (non-Javadoc)
	 * @see mindnotes.client.ui.CloudActionsView#setListener(mindnotes.client.ui.CloudBar.Listener)
	 */
	@Override
	public void setListener(Listener listener) {
		_listener = listener;
	}

	private void setOnlineWidgetsVisible(boolean visible) {
		logoutLink.setVisible(visible);
		greetLabel.setVisible(visible);
		saveButton.setVisible(visible);
		shareButton.setVisible(visible);
	}

	private void setOfflineWidgetsVisible(boolean visible) {
		offlineLabel.setVisible(visible);
		tryAgainLink.setVisible(visible);
	}

	private void setNotLoggedInWidgetsVisible(boolean visible) {
		notLoggedInLabel.setVisible(visible);
		loginLink.setVisible(visible);
	}

	/* (non-Javadoc)
	 * @see mindnotes.client.ui.CloudActionsView#setOnline(java.lang.String, java.lang.String)
	 */
	@Override
	public void setOnline(String email, String logoutURL) {
		setOnlineWidgetsVisible(true);
		setOfflineWidgetsVisible(false);
		setNotLoggedInWidgetsVisible(false);

		greetLabel.setText(email);
		logoutLink.setHref(logoutURL);
		setVisible(true);
	}

	/* (non-Javadoc)
	 * @see mindnotes.client.ui.CloudActionsView#setNotLoggedIn(java.lang.String)
	 */
	@Override
	public void setNotLoggedIn(String loginURL) {
		setOnlineWidgetsVisible(false);
		setOfflineWidgetsVisible(false);
		setNotLoggedInWidgetsVisible(true);
		loginLink.setHref(loginURL);
		setVisible(true);
	}

	/* (non-Javadoc)
	 * @see mindnotes.client.ui.CloudActionsView#setOffline(java.lang.String)
	 */
	@Override
	public void setOffline() {
		setOnlineWidgetsVisible(false);
		setOfflineWidgetsVisible(true);
		setNotLoggedInWidgetsVisible(false);
		setVisible(true);
	}

}
