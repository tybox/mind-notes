package mindnotes.client.ui;

import java.util.List;

import mindnotes.client.model.NodeLocation;

public interface LayoutTreeElement {

	public LayoutTreeElement getLayoutParent();

	public List<? extends LayoutTreeElement> getLayoutChildren();

	/**
	 * 
	 * @return a box (x, y, w, h) that bounds the branch that starts with this
	 *         element. If this is the same a leaf element, it's the same as
	 *         getElementBounds(); Coordinates are relative to the element's
	 *         center (element's center is at (0,0) )
	 */
	public Box getBranchBounds();

	public void setBranchBounds(Box box);

	/**
	 * 
	 * @return a box (x, y, w, h) that bounds the content of this element only,
	 *         not including its children.
	 */
	public Box getElementBounds();

	/**
	 * Sets the offset from parent's center to this element's center.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public void setOffset(int x, int y);

	public int getOffsetX();

	public int getOffsetY();

	public NodeLocation getLocation();

	public void setLayoutValid(boolean valid);

	public boolean isLayoutValid();

}
