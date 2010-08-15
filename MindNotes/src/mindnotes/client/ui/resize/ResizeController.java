package mindnotes.client.ui.resize;

import com.allen_sauer.gwt.dnd.client.AbstractDragController;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.widgetideas.client.GlassPanel;

public class ResizeController extends AbstractDragController {

	private static final int MIN_WIDGET_SIZE = 20;

	private ResizeHandle _handle = null;

	private GlassPanel _glassPanel;

	public ResizeController(AbsolutePanel boundaryPanel) {
		super(boundaryPanel);
	}

	public void dragMove() {
		int w, h;

		Widget resizable = _handle.getResizable();
		w = resizable.getOffsetWidth();
		h = resizable.getOffsetHeight();

		int deltaX = -context.draggable.getAbsoluteLeft()
				+ context.desiredDraggableX;
		if (deltaX != 0) {
			w = Math.max(w + deltaX, MIN_WIDGET_SIZE);
		}

		int deltaY = -context.draggable.getAbsoluteTop()
				+ context.desiredDraggableY;
		if (deltaY != 0) {
			h = Math.max(h + deltaY, MIN_WIDGET_SIZE);
		}

		resizable.setPixelSize(w, h);
		_handle.fireResized();
	}

	@Override
	public void dragEnd() {
		super.dragEnd();
		getBoundaryPanel().remove(_glassPanel);
	}

	@Override
	public void dragStart() {
		super.dragStart();

		_handle = (ResizeHandle) context.draggable;

		// add GlassPanel to prevent iframes capturing mouseover events
		// see http://code.google.com/p/gwt-dnd/issues/detail?id=29
		if (_glassPanel == null) {
			_glassPanel = new GlassPanel(false);
			_glassPanel.setStyleName(_handle.getResources().style()
					.glassPanel());
		}
		getBoundaryPanel().add(_glassPanel, 0, 0);

	}
}
