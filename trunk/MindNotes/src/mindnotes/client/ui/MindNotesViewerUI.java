package mindnotes.client.ui;

import mindnotes.client.presentation.MindMapView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class MindNotesViewerUI extends Composite {

	private static MindMapViewerUIUiBinder uiBinder = GWT
			.create(MindMapViewerUIUiBinder.class);

	interface MindMapViewerUIUiBinder extends
			UiBinder<Widget, MindNotesViewerUI> {
	}

	@UiField(provided = true)
	MindMapWidget mindMapWidget;

	@UiField
	Label titleLabel;

	@UiField
	public DialogBox errorDialog;

	@UiField
	public Label errorDialogLabel;

	public MindNotesViewerUI() {
		mindMapWidget = new MindMapWidget(true);
		initWidget(uiBinder.createAndBindUi(this));
	}

	public MindMapView getMindMapView() {
		return mindMapWidget;
	}

	public void setMapTitle(String string) {
		titleLabel.setText(string);
	}
}
