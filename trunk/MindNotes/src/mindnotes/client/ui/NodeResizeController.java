package mindnotes.client.ui;

import com.allen_sauer.gwt.dnd.client.AbstractDragController;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

public class NodeResizeController extends AbstractDragController {

	private static final int MIN_NODE_SIZE = 10;

	private NodeWidget _nodeWidget = null;

	public NodeResizeController(AbsolutePanel boundaryPanel) {
		super(boundaryPanel);
	}

	public void dragMove() {
		int w, h;

		w = _nodeWidget.getTextPanelWidth();
		h = _nodeWidget.getTextPanelHeight();

		int deltaX = -context.draggable.getAbsoluteLeft()
				+ context.desiredDraggableX;
		if (deltaX != 0) {
			w = Math.max(w + deltaX, MIN_NODE_SIZE);
		}

		int deltaY = -context.draggable.getAbsoluteTop()
				+ context.desiredDraggableY;
		if (deltaY != 0) {
			h = Math.max(h + deltaY, MIN_NODE_SIZE);
		}

		_nodeWidget.setTextPanelSize(w, h);

	}

	@Override
	public void dragStart() {
		super.dragStart();
		Widget parent = context.draggable.getParent();
		while (!(parent instanceof NodeWidget)) {
			parent = parent.getParent();
		}
		_nodeWidget = (NodeWidget) parent;
	}

}
