package mindnotes.client.ui;

import mindnotes.client.ui.NodeLayout.LayoutPosition;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.AbstractDropController;

public class MindMapDropController extends AbstractDropController {

	LayoutPosition _position = null;

	private GhostNode _ghostNode;

	private final MindMapWidget _mindMapWidget;

	public MindMapDropController(MindMapWidget mindMapWidget) {
		super(mindMapWidget.getViewportPanel());
		_mindMapWidget = mindMapWidget;

	}

	@Override
	public void onEnter(DragContext context) {

		super.onEnter(context);
		_ghostNode = new GhostNode((LayoutTreeElement) context.draggable);
		_position = null;
	}

	@Override
	public void onLeave(DragContext context) {

		super.onLeave(context);
		_ghostNode = null;
		((NodeWidget) _position.parent).removeTemporaryLayoutChild();

	}

	@Override
	public void onMove(DragContext context) {
		super.onMove(context);

		LayoutPosition position = findBestDropPosition(context);

		if (_position == null || !_position.equals(position)) {
			_mindMapWidget.holdLayout();
			// remove ghost from previous node
			if (_position != null) {
				((NodeWidget) _position.parent).removeTemporaryLayoutChild();
			}

			_position = position;
			_ghostNode.setParent(_position.parent);
			((NodeWidget) _position.parent).addTemporaryLayoutChild(
					_position.index, _ghostNode);
			_ghostNode.setNodeLocation(_position.location);
			_mindMapWidget.resumeLayout();
		}
	}

	private LayoutPosition findBestDropPosition(DragContext context) {

		int panelLeft = _mindMapWidget.getViewportPanel().getAbsoluteLeft();
		int panelTop = _mindMapWidget.getViewportPanel().getAbsoluteTop();

		NodeWidget root = _mindMapWidget.getRootNodeWidget();

		int px = -_mindMapWidget.getLayoutOffsetX() + root.getOffsetX()
				+ context.mouseX - panelLeft;
		int py = -_mindMapWidget.getLayoutOffsetY() + root.getOffsetY()
				+ context.mouseY - panelTop;

		LayoutPosition position = NodeLayout.findClosestInsertPosition(root,
				px, py);
		while (!(position.parent instanceof NodeWidget)
				&& position.parent != null) {
			LayoutTreeElement newParent = position.parent.getLayoutParent();
			position.index = newParent.getLayoutChildren().indexOf(
					position.parent);
			position.parent = newParent;
		}
		return position;

	}

	public GhostNode getGhostNode() {
		return _ghostNode;
	}

	@Override
	public void onPreviewDrop(DragContext context) throws VetoDragException {
		// throw new VetoDragException();
	}

	@Override
	public void onDrop(DragContext context) {
		super.onDrop(context);
		if (_position.parent instanceof NodeWidget) {
			// XXX this is hacky
			((NodeWidget) context.draggable).onBranchDragged(_position.index,
					_position.location);
			((NodeWidget) _position.parent).onBranchDropped();
		}
	}
}
