package mindnotes.client.ui;

import mindnotes.client.ui.imagesearch.ImageSearchWidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class SearchPopup extends PopupPanel {

	private static SearchPopupUiBinder uiBinder = GWT
			.create(SearchPopupUiBinder.class);

	interface SearchPopupUiBinder extends UiBinder<Widget, SearchPopup> {
	}

	interface Listener extends ImageSearchWidget.Listener {
		public void mapInsertGesture();

		public void textInsertGesture();
	}

	private Listener _listener;

	@UiField
	ImageSearchWidget imageSearch;
	@UiField
	Anchor insertMap;
	@UiField
	Anchor insertText;

	public SearchPopup() {
		setWidget(uiBinder.createAndBindUi(this));
		setAutoHideEnabled(true);
		setStylePrimaryName("search-dialog");
	}

	@UiHandler("insertMap")
	public void onInsertMapClicked(ClickEvent e) {
		if (_listener != null) {
			_listener.mapInsertGesture();
		}
	}

	@UiHandler("insertText")
	public void onInsertTextClicked(ClickEvent e) {
		if (_listener != null) {
			_listener.textInsertGesture();
		}
	}

	public void setListener(Listener listener) {
		_listener = listener;
		imageSearch.setListener(listener);
	}

	public void performSearches(String searchText) {
		imageSearch.performSearch(searchText);
	}

	public void showAt(final int x, final int y) {
		setPopupPositionAndShow(new PositionCallback() {

			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				int nx, ny;
				if (x + offsetWidth > Window.getClientWidth()) {
					nx = x - (offsetWidth + 20);
				} else {
					nx = x;
				}
				if (y + offsetHeight > Window.getClientHeight()) {
					ny = y - (offsetHeight + 20);
				} else {
					ny = y;
				}
				setPopupPosition(nx, ny);
			}
		});
	}
}
