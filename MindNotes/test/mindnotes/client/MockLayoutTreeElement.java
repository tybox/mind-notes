package mindnotes.client;

import java.util.ArrayList;
import java.util.List;

import mindnotes.client.ui.Box;
import mindnotes.client.ui.LayoutTreeElement;
import mindnotes.shared.model.NodeLocation;

public class MockLayoutTreeElement implements LayoutTreeElement {

	private LayoutTreeElement _parent;
	private List<LayoutTreeElement> _children;
	private Box _branchBounds;
	private Box _elementBounds;
	private int _x;
	private int _y;
	private NodeLocation _location;
	private boolean _valid;

	public MockLayoutTreeElement(int x, int y, int w, int h,
			LayoutTreeElement parent, NodeLocation location) {
		_children = new ArrayList<LayoutTreeElement>();
		_elementBounds = new Box(x, y, w, h);
		_location = location;
	}

	public void addChild(LayoutTreeElement child) {
		_children.add(child);
	}

	public void setParent(LayoutTreeElement parent) {
		_parent = parent;
	}

	@Override
	public LayoutTreeElement getLayoutParent() {
		return _parent;
	}

	@Override
	public List<LayoutTreeElement> getLayoutChildren() {
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
		_x = x;
		_y = y;
	}

	@Override
	public int getOffsetX() {
		return _x;
	}

	@Override
	public int getOffsetY() {
		return _y;
	}

	@Override
	public NodeLocation getLocation() {
		return _location;
	}

	@Override
	public void setLayoutValid(boolean valid) {
		_valid = valid;
	}

	@Override
	public boolean isLayoutValid() {
		return _valid;
	}

}