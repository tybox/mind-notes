package mindnotes.client.ui;

import mindnotes.shared.model.NodeLocation;

/**
 * NodeLayout is responsible for computing desired positions of nodes in a node
 * tree based on their relations.
 * 
 * 
 * 
 * 
 * 
 * 
 * @author dominik
 * 
 */
public class NodeLayout {

	/**
	 * Some margins for the layout; TODO make this configurable someday
	 */
	private static final int HORIZONTAL_MARGIN = 48;
	private static final int VERTICAL_MARGIN = 32;

	/**
	 * Layout works from bottom to top; layout mechanism for each element works
	 * as a function:
	 * 
	 * F: {Element Bounds of this element, Branch Bounds of every child} ->
	 * {relative position of every child, Branch Bounds of this element}
	 * 
	 * The doLayout() method is called recursively for every child; childless
	 * elements return data immediately.
	 * 
	 * @see LayoutTreeElement for definitions of branch bounds and element
	 *      bounds.
	 * 
	 * @param element
	 */
	public void doLayout(LayoutTreeElement element) {
		if (element.isLayoutValid()) {
			return; // nothing changed, nothing to do;
		}
		// there are two groups of elements: one on the left, one on the right

		// go through the list of children and find out:
		// maximum width on the left and right
		// total height on left and right

		Box elementBounds = element.getElementBounds();

		int wLeft = 0, wRight = 0, hLeft = 0, hRight = 0;
		for (LayoutTreeElement child : element.getLayoutChildren()) {
			// recursive call to let the child layout itself
			doLayout(child);
			Box b = child.getBranchBounds();
			if (child.getLocation() == NodeLocation.LEFT) {
				// right now, the child will be vertically shifted from the
				// desired location, because we don't know the height
				// of all elements
				child.setOffset(
						elementBounds.x - HORIZONTAL_MARGIN - b.x - b.w, hLeft);
				wLeft = Math.max(wLeft, b.w);
				hLeft += b.h + VERTICAL_MARGIN;

			}
			if (child.getLocation() == NodeLocation.RIGHT) {
				// see comment above
				child.setOffset(elementBounds.x + elementBounds.w
						+ HORIZONTAL_MARGIN - child.getBranchBounds().x, hRight
						- child.getBranchBounds().y);
				wRight = Math.max(wRight, b.w);
				hRight += b.h + VERTICAL_MARGIN;
			}
		}
		if (hRight > 0)
			hRight -= VERTICAL_MARGIN;
		if (hLeft > 0)
			hLeft -= VERTICAL_MARGIN;

		// now, while traversing, we'll adjust the vertical position of
		// children.
		// the offset is set so that vertical center of parent element is at the
		// same height as center of all children brought together;
		int shiftLeft = elementBounds.y + (elementBounds.h / 2) - hLeft / 2;
		int shiftRight = elementBounds.y + (elementBounds.h / 2) - hRight / 2;

		for (LayoutTreeElement child : element.getLayoutChildren()) {
			if (child.getLocation() == NodeLocation.LEFT) {
				child.setOffset(child.getOffsetX(), child.getOffsetY()
						+ shiftLeft);
			}
			if (child.getLocation() == NodeLocation.RIGHT) {
				child.setOffset(child.getOffsetX(), child.getOffsetY()
						+ shiftRight);
			}
		}

		// determine this element's branch bounding box

		int eycenter = elementBounds.y + elementBounds.h / 2;
		int top = Math.min(eycenter - Math.max(hLeft, hRight) / 2,
				elementBounds.y);
		int bottom = Math.max(eycenter + Math.max(hLeft, hRight) / 2,
				elementBounds.y + elementBounds.h);
		int left = elementBounds.x
				- (wLeft == 0 ? 0 : HORIZONTAL_MARGIN + wLeft);
		int right = elementBounds.x + elementBounds.w
				+ (wRight == 0 ? 0 : HORIZONTAL_MARGIN + wRight);

		Box bounds = new Box(left, top, right - left, bottom - top);
		element.setBranchBounds(bounds);
		element.setLayoutValid(true);

	}
}
