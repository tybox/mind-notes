package mindnotes.client.ui;

import mindnotes.client.presentation.MindMapView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

public class MindNotesUI extends Composite implements RequiresResize,
		ProvidesResize {

	private static MindNotesUIUiBinder uiBinder = GWT
			.create(MindNotesUIUiBinder.class);

	interface MindNotesUIUiBinder extends UiBinder<Widget, MindNotesUI> {
	}

	@UiField(provided = true)
	MindMapWidget mindMapWidget;

	@UiField
	Panel cloudBarPanel;

	@UiField
	Label titleLabel;

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

	public MindNotesUI() {

		mindMapWidget = new MindMapWidget(false);
		initWidget(uiBinder.createAndBindUi(this));
		mindMapWidget.setEditorWindow(this);

	}

	@UiHandler("titleLabel")
	public void onTitleClicked(ClickEvent event) {
		mindMapWidget.askForDocumentTitle(titleLabel.getText());
	}

	@UiHandler("newButton")
	public void onNewClicked(ClickEvent event) {
		mindMapWidget.newClicked();
	}

	@UiHandler("loadButton")
	public void onLoadClicked(ClickEvent event) {
		mindMapWidget.loadFromCloudClicked();
	}

	@UiHandler("saveButton")
	public void onSaveClicked(ClickEvent event) {
		mindMapWidget.saveToCloudClicked();
	}

	@UiHandler("saveLocalButton")
	public void onSaveLocalClicked(ClickEvent event) {
		mindMapWidget.saveLocalClicked();
	}

	@UiHandler("shareButton")
	public void onShareClicked(ClickEvent event) {
		mindMapWidget.shareClicked();
	}

	public MindMapView getMindMapView() {
		return mindMapWidget;
	}

	public void setLogoutLink(String logoutURL) {
		logoutLink.setHref(logoutURL);
	}

	public void setUserEmail(String email) {
		greetLabel.setText(email);
	}

	public void setCloudBarVisible(boolean visible) {
		logoutLink.setVisible(visible);
		greetLabel.setVisible(visible);
		saveButton.setVisible(visible);
		shareButton.setVisible(visible);
	}

	public void setMindMapTitle(String title) {
		titleLabel.setText(title);
	}

	@Override
	public void onResize() {
		mindMapWidget.onResize();

	}

}
