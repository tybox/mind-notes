package mindnotes.client.ui.resize;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class ResizeHandle extends FocusPanel {

	private final Widget _resizable;

	interface Resources extends ClientBundle {
		@Source("ResizeHandle.css")
		Style style();

		ImageResource resizeHandleIcon();
	}

	interface Style extends CssResource {
		String resizeHandle();

		String resizable();

		String glassPanel();
	}

	public interface Listener {
		public void onResized();
	}

	private Resources _resources = GWT.create(Resources.class);
	private Listener _listener;

	public ResizeHandle(ResizeController controller, Widget resizable) {
		_resources.style().ensureInjected();
		_resizable = resizable;
		_resizable.addStyleName(_resources.style().resizable());
		setWidget(new Image(_resources.resizeHandleIcon()));
		addDomHandler(new DoubleClickHandler() {

			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				_resizable.setSize("auto", "auto");
				fireResized();
				event.stopPropagation();
			}
		}, DoubleClickEvent.getType());
		controller.makeDraggable(this);
		setStyleName(_resources.style().resizeHandle());
	}

	public void fireResized() {
		if (_listener != null) {
			_listener.onResized();
		}

	}

	public Widget getResizable() {
		return _resizable;
	}

	public void setListener(Listener listener) {
		_listener = listener;
	}

	public Resources getResources() {
		return _resources;
	}
}
