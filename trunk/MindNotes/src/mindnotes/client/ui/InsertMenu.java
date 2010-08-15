package mindnotes.client.ui;

import mindnotes.client.ui.imagesearch.ImageSearchWidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class InsertMenu extends PopupPanel {

	private static final int POPUP_TIMEOUT = 600;

	private static InsertMenuUiBinder uiBinder = GWT
			.create(InsertMenuUiBinder.class);

	interface InsertMenuUiBinder extends UiBinder<Widget, InsertMenu> {
	}

	interface Listener {
		public void mapInsertGesture();

		public void textInsertGesture();

		public void imageChosenGesture(String url);
	}

	private Listener _listener;

	@UiField
	Label insertMap;
	@UiField
	Label insertText;
	@UiField
	Label showImageSearch;
	@UiField(provided = true)
	PopupPanel imageSearchPopup;
	@UiField
	ImageSearchWidget imageSearch;

	private Timer _imagePopupClose;

	public InsertMenu() {

		imageSearchPopup = new PopupPanel() {
			{
				addDomHandler(new MouseOutHandler() {

					@Override
					public void onMouseOut(MouseOutEvent event) {
						_imagePopupClose.schedule(POPUP_TIMEOUT);

					}
				}, MouseOutEvent.getType());
				addDomHandler(new MouseOverHandler() {

					@Override
					public void onMouseOver(MouseOverEvent event) {
						_imagePopupClose.cancel();

					}
				}, MouseOverEvent.getType());

			}
		};

		setWidget(uiBinder.createAndBindUi(this));
		setAutoHideEnabled(true);
		setStylePrimaryName("search-dialog");
		_imagePopupClose = new Timer() {

			@Override
			public void run() {
				imageSearchPopup.hide();
			}
		};
		imageSearchPopup.removeFromParent();
		imageSearch.setListener(new ImageSearchWidget.Listener() {

			@Override
			public void onResize(int offsetWidth, int offsetHeight) {
			}

			@Override
			public void imageChosenGesture(String url) {
				if (_listener != null)
					_listener.imageChosenGesture(url);
			}

			@Override
			public void onCancelHide() {
				_imagePopupClose.cancel();
			}

			@Override
			public void onMaybeHide() {
				_imagePopupClose.schedule(POPUP_TIMEOUT);
			}
		});
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

	@UiHandler("showImageSearch")
	public void onImageSearchMouseOver(MouseOverEvent e) {
		_imagePopupClose.cancel();
		if (!imageSearchPopup.isShowing()) {
			showImageSearchPopup();
		}
	}

	@UiHandler("showImageSearch")
	public void onImageSearchMouseOut(MouseOutEvent e) {
		_imagePopupClose.schedule(POPUP_TIMEOUT);

	}

	private void showImageSearchPopup() {
		imageSearchPopup.setPopupPositionAndShow(new PositionCallback() {

			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				int x = showImageSearch.getAbsoluteLeft()
						+ showImageSearch.getOffsetWidth();
				int y = showImageSearch.getAbsoluteTop();

				if (x + offsetWidth > Window.getClientWidth()) {
					x -= showImageSearch.getOffsetWidth() + offsetWidth;
				}
				if (y + offsetHeight > Window.getClientHeight()) {
					y -= offsetHeight;
				}

				imageSearchPopup.setPopupPosition(x, y);

			}
		});
	}

	public void setListener(Listener listener) {
		_listener = listener;

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
