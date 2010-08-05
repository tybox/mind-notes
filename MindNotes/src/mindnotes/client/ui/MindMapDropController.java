package mindnotes.client.ui;

import mindnotes.shared.model.NodeLocation;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.AbstractDropController;

public class MindMapDropController extends AbstractDropController {

	DropPosition _position = null;

	private GhostNode _ghostNode;

	private final MindMapWidget _mindMapWidget;

	private class DropPosition {
		NodeWidget parent;
		int index;
		NodeLocation location;

		@Override
		public boolean equals(Object obj) {
			return (obj != null) && (((DropPosition) obj).index == index)
					&& (((DropPosition) obj).parent == parent)
					&& (((DropPosition) obj).location == location);
		}
	}

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
		_position.parent.removeTemporaryLayoutChild();

	}

	@Override
	public void onMove(DragContext context) {
		super.onMove(context);

		DropPosition position = findBestDropPosition(context);
		if (_position == null || !_position.equals(position)) {

			// remove ghost from previous node
			if (_position != null) {
				_position.parent.removeTemporaryLayoutChild();
			}

			_position = position;
			_ghostNode.setParent(_position.parent);
			_position.parent.addTemporaryLayoutChild(_position.index,
					_ghostNode);

		}
	}

	private DropPosition findBestDropPosition(DragContext context) {
		DropPosition dropPosition = new DropPosition();
		dropPosition.parent = _mindMapWidget.getRootNodeWidget();
		dropPosition.index = 0;
		return dropPosition;
	}

	public GhostNode getGhostNode() {
		return _ghostNode;
	}

	@Override
	public void onPreviewDrop(DragContext context) throws VetoDragException {
		throw new VetoDragException();
	}
}
