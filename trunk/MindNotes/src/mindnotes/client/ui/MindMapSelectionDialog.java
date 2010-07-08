package mindnotes.client.ui;

import java.util.List;

import mindnotes.client.presentation.MindMapSelectionView;
import mindnotes.shared.model.MindMapInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;

public class MindMapSelectionDialog extends Composite implements
		MindMapSelectionView {

	protected Widget _selectedWidget;

	protected MindMapInfo _selectedDocument;

	private class ListSelectionHandler implements ClickHandler {

		private MindMapInfo _document;
		private Widget _widget;

		public ListSelectionHandler(MindMapInfo mmi, Widget w) {
			_document = mmi;
			_widget = w;
		}

		@Override
		public void onClick(ClickEvent event) {
			_selectedDocument = _document;
			if (_selectedWidget != null) {
				_selectedWidget.removeStyleDependentName("selected");
			}
			_selectedWidget = _widget;
			_selectedWidget.addStyleDependentName("selected");
			okButton.setEnabled(true);
		}

	}

	private static MindMapSelectionDialogUiBinder uiBinder = GWT
			.create(MindMapSelectionDialogUiBinder.class);

	interface MindMapSelectionDialogUiBinder extends
			UiBinder<Widget, MindMapSelectionDialog> {
	}

	public MindMapSelectionDialog() {
		initWidget(uiBinder.createAndBindUi(this));
		okButton.setEnabled(false);
		okButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (_listener != null)
					_listener.mindMapChosen(_selectedDocument);
				MindMapSelectionDialog.this.setVisible(false);
			}
		});

		cancelButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				MindMapSelectionDialog.this.setVisible(false);
			}
		});

	}

	@UiField
	protected Button okButton;

	@UiField
	protected Button cancelButton;

	@UiField
	protected FlexTable availableDocumentsTable;

	private Listener _listener;

	@Override
	public void setListener(Listener listener) {
		_listener = listener;
	}

	@Override
	public void setMindMaps(List<MindMapInfo> mindmaps) {

		availableDocumentsTable.clear();

		// populate the table with values for mindmaps
		int i = 0;
		for (MindMapInfo doc : mindmaps) {
			Anchor link = new Anchor(doc.getTitle());
			link.setStylePrimaryName("mindmap-list-link");
			link.addClickHandler(new ListSelectionHandler(doc, link));
			availableDocumentsTable.setWidget(i, 1, link);
			i++;
		}

	}
}
