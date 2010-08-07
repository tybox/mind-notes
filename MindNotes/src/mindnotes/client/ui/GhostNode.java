package mindnotes.client.ui;

import java.util.ArrayList;
import java.util.List;

import mindnotes.shared.model.NodeLocation;

public class GhostNode implements LayoutTreeElement {

	private LayoutTreeElement _parent;
	private List<GhostNode> _children;
	private Box _branchBounds;
	private Box _elementBounds;
	private int _offsetX;
	private int _offsetY;
	private NodeLocation _location;
	private boolean _layoutValid;

	public GhostNode(LayoutTreeElement realNode) {

		_children = new ArrayList<GhostNode>();
		_branchBounds = new Box(realNode.getBranchBounds());
		_elementBounds = new Box(realNode.getElementBounds());
		_location = realNode.getLocation();

		for (LayoutTreeElement child : realNode.getLayoutChildren()) {
			addChild(new GhostNode(child));
		}
	}

	private void addChild(GhostNode child) {
		_children.add(child);
		child.setParent(this);
	}

	public void setParent(LayoutTreeElement parent) {
		_parent = parent;

	}

	@Override
	public LayoutTreeElement getLayoutParent() {
		return _parent;
	}

	@Override
	public List<? extends LayoutTreeElement> getLayoutChildren() {
		return _children;
	}

	@Override
	public Box getBranchBounds() {
		return _branchBounds;
	}

	@Override
	public void setBranchBounds(Box box) {
		_branchBounds = box;
	}

	@Override
	public Box getElementBounds() {
		return _elementBounds;
	}

	@Override
	public void setOffset(int x, int y) {
		_offsetX = x;
		_offsetY = y;
	}

	@Override
	public int getOffsetX() {
		return _offsetX;
	}

	@Override
	public int getOffsetY() {
		return _offsetY;
	}

	@Override
	public NodeLocation getLocation() {
		return _location;
	}

	@Override
	public void setLayoutValid(boolean valid) {
		_layoutValid = valid;
		if (_parent != null && valid == false) {
			_parent.setLayoutValid(false);
		}
	}

	@Override
	public boolean isLayoutValid() {
		return _layoutValid;
	}

	public List<GhostNode> getChildren() {
		return _children;
	}

	public void setNodeLocation(NodeLocation location) {
		boolean change = !(location.equals(_location));
		_location = location;
		for (GhostNode child : _children) {
			child.setNodeLocation(location);
		}
		if (change) {
			setLayoutValid(false);
		}

	}

}
