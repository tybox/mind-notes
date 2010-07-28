package mindnotes.client.ui;

import mindnotes.client.presentation.MindMapView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
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

	@UiField
	MindMapWidget mindMapWidget;
	@UiField
	DockLayoutPanel dockLayoutPanel;
	@UiField
	Anchor saveButton;
	@UiField
	Anchor loadButton;
	@UiField
	Anchor logoutLink;
	@UiField
	Label greetLabel;
	@UiField
	Anchor saveLocalButton;
	@UiField
	Panel cloudBarPanel;
	@UiField
	Label titleLabel;
	@UiField
	Anchor newButton;

	public MindNotesUI() {

		initWidget(uiBinder.createAndBindUi(this));
		mindMapWidget.setEditorWindow(this);

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

	@UiHandler("newButton")
	public void onNewClicked(ClickEvent event) {
		mindMapWidget.newClicked();
	}

	@UiHandler("titleLabel")
	public void onTitleClicked(ClickEvent event) {
		mindMapWidget.askForDocumentTitle(titleLabel.getText());
	}

	public MindMapView getMindMapView() {
		return mindMapWidget;
	}

	@Override
	public void onResize() {
		dockLayoutPanel.onResize();
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
	}

	public void setMindMapTitle(String title) {
		titleLabel.setText(title);
	}

}
