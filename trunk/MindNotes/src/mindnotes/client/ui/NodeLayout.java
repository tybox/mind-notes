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

	public static class LayoutPosition {
		public LayoutTreeElement parent;
		public int index;
		public NodeLocation location;

		public LayoutPosition() {

		}

		public LayoutPosition(LayoutTreeElement parent, int index,
				NodeLocation location) {
			this.parent = parent;
			this.index = index;
			this.location = location;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + index;
			result = prime * result
					+ ((location == null) ? 0 : location.hashCode());
			result = prime * result
					+ ((parent == null) ? 0 : parent.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			LayoutPosition other = (LayoutPosition) obj;
			if (index != other.index)
				return false;
			if (location != other.location)
				return false;
			if (parent == null) {
				if (other.parent != null)
					return false;
			} else if (!parent.equals(other.parent))
				return false;
			return true;
		}

	}

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
	public static void doLayout(LayoutTreeElement element) {
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

	public static LayoutPosition findClosestInsertPosition(
			LayoutTreeElement root, int px, int py) {
		// inside this element?
		Box b = root.getElementBounds();
		// b.x-H_M |b.x______px____b.x+b.w| b.x+b.w+H_M
		boolean insideRoot = b.x - HORIZONTAL_MARGIN / 2 < px
				&& px <= b.x + b.w + HORIZONTAL_MARGIN / 2;
		// if not, it's one of the children - which side?
		NodeLocation direction;

		if (root.getLocation() == NodeLocation.ROOT) {

			if (px > b.x + b.w / 2) { // |__root__| ---> px
				direction = NodeLocation.RIGHT;
			} else { // px <---- |__root__|
				direction = NodeLocation.LEFT;
			}
		} else {
			direction = root.getLocation();
		}

		int i = 0;
		for (LayoutTreeElement child : root.getLayoutChildren()) {
			Box c = child.getBranchBounds();
			if (c.y - VERTICAL_MARGIN / 2 < py
					&& py <= c.y + c.h + VERTICAL_MARGIN / 2) {
				// hit
				if (insideRoot) {
					return new LayoutPosition(root, i, direction);
				} else if (child.getLocation() == direction) {
					return findClosestInsertPosition(child,
							px + child.getOffsetX(), py + child.getOffsetY());
				}
			}
			i++;
		}

		// no children fit - then assume root as the best
		return new LayoutPosition(root, i, direction);

	}

}
