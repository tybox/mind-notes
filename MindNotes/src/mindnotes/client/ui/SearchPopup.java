package mindnotes.client.ui;

import mindnotes.client.ui.imagesearch.ImageSearchWidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class SearchPopup extends Composite {

	private static SearchPopupUiBinder uiBinder = GWT
			.create(SearchPopupUiBinder.class);

	interface SearchPopupUiBinder extends UiBinder<Widget, SearchPopup> {
	}

	interface Listener extends ImageSearchWidget.Listener {
		public void mapCreateGesture();
	}

	private Listener _listener;

	@UiField
	ImageSearchWidget imageSearch;
	@UiField
	Anchor insertMap;

	private HandlerRegistration _handlerRegistration;

	public SearchPopup() {
		initWidget(uiBinder.createAndBindUi(this));

	}

	@UiHandler("insertMap")
	public void onInsertMapClicked(ClickEvent e) {
		if (_listener != null) {
			_listener.mapCreateGesture();
		}
	}

	public void setListener(Listener listener) {
		_listener = listener;
		imageSearch.setListener(listener);
	}

	public void performSearches(String searchText) {
		imageSearch.performSearch(searchText);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			_handlerRegistration = Event
					.addNativePreviewHandler(new NativePreviewHandler() {

						@Override
						public void onPreviewNativeEvent(
								NativePreviewEvent event) {
							previewNativeEvent(event);
						}
					});
		} else {
			if (_handlerRegistration != null) {
				_handlerRegistration.removeHandler();
			}
		}
	}

	private void previewNativeEvent(NativePreviewEvent event) {
		if (event.isCanceled()) {
			return;
		}
		// If the event targets the popup or the partner, consume it
		Event nativeEvent = Event.as(event.getNativeEvent());

		boolean eventTargetsPopup = eventTargetsPopup(nativeEvent);

		if (eventTargetsPopup) {
			event.consume();
		}

		// Switch on the event type
		int type = nativeEvent.getTypeInt();
		switch (type) {

		case Event.ONMOUSEDOWN:
			// Don't eat events if event capture is enabled, as this can
			// interfere with dialog dragging, for example.
			if (DOM.getCaptureElement() != null) {
				event.consume();
				return;
			}

			if (!eventTargetsPopup) {
				setVisible(false);
				return;
			}
			break;
		case Event.ONMOUSEUP:
		case Event.ONMOUSEMOVE:
		case Event.ONCLICK:
		case Event.ONDBLCLICK: {
			// Don't eat events if event capture is enabled, as this can
			// interfere with dialog dragging, for example.
			if (DOM.getCaptureElement() != null) {
				event.consume();
				return;
			}
			break;
		}

		}
	}

	private boolean eventTargetsPopup(NativeEvent event) {
		EventTarget target = event.getEventTarget();
		if (Element.is(target)) {
			return getElement().isOrHasChild(Element.as(target));
		}
		return false;
	}

	/*public void show(final int x, final int y, String searchText) {
		imageSearch.performSearch(searchText);
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
	}*/
}
