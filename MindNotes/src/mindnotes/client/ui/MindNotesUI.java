package mindnotes.client.ui;

import mindnotes.client.presentation.MindMapView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
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

	@UiField(provided = true)
	MindMapWidget mindMapWidget;

	@UiField
	CloudBar cloudBarPanel;

	@UiField
	Label titleLabel;

	public MindNotesUI() {

		mindMapWidget = new MindMapWidget(false);
		initWidget(uiBinder.createAndBindUi(this));
		mindMapWidget.setEditorWindow(this);

	}

	@UiHandler("titleLabel")
	public void onTitleClicked(ClickEvent event) {
		mindMapWidget.askForDocumentTitle(titleLabel.getText());
	}

	public MindMapView getMindMapView() {
		return mindMapWidget;
	}

	public void setMindMapTitle(String title) {
		titleLabel.setText(title);
	}

	@Override
	public void onResize() {
		mindMapWidget.onResize();

	}

}
