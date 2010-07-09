package mindnotes.client.ui;

import mindnotes.client.presentation.MindMapView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Label;
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

	public MindNotesUI() {

		initWidget(uiBinder.createAndBindUi(this));

		saveButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				mindMapWidget.saveToCloudClicked();
			}
		});
		loadButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				mindMapWidget.loadFromCloudClicked();
			}
		});
		mindMapWidget.setEditorWindow(this);
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

}
