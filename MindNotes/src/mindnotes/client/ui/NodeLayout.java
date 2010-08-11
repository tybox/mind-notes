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
						elementBounds.x - HORIZONTAL_MARGIN - b.x - b.w, hLeft
								- child.getBranchBounds().y);
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

		Box b = root.getElementBounds();

		boolean right = px >= (b.x + b.w / 2);

		// mouse location
		NodeLocation ml = right ? NodeLocation.RIGHT : NodeLocation.LEFT;
		boolean up = py < (b.y + b.h / 2);

		boolean isRoot = root.getLocation() == NodeLocation.ROOT;

		if (root.getLayoutChildren().isEmpty()) {
			if (isRoot) {

				return new LayoutPosition(root, 0, ml);
			} else {

				if (ml == root.getLocation()) {
					// we are deeper into the leaves - propose adding a child
					return new LayoutPosition(root, 0, ml);
				} else {
					// we are facing the parent - propose adding a sibling
					int index = root.getLayoutParent().getLayoutChildren()
							.indexOf(root);
					return new LayoutPosition(root.getLayoutParent(), index
							+ (up ? 0 : 1), root.getLocation());
				}
			}
		} else {
			if (ml == root.getLocation() || isRoot) {

				// we are facing the parent - ask child to decide
				LayoutTreeElement lastChild = null;

				for (LayoutTreeElement child : root.getLayoutChildren()) {
					if (isRoot && ml != child.getLocation())
						continue;
					Box cb = child.getBranchBounds();

					if (py - child.getOffsetY() < cb.y + cb.h) {
						return findClosestInsertPosition(child,
								px - child.getOffsetX(),
								py - child.getOffsetY());
					}
					lastChild = child;
				}

				if (lastChild != null) {

					return findClosestInsertPosition(lastChild,
							px - lastChild.getOffsetX(),
							py - lastChild.getOffsetY());
				} else {

					// we are on childless side of root node; suggest adding
					return new LayoutPosition(root, 0, ml);
				}

			} else {

				// we are facing the parent - propose adding a sibling
				int index = root.getLayoutParent().getLayoutChildren()
						.indexOf(root);
				return new LayoutPosition(root.getLayoutParent(), index
						+ (up ? 0 : 1), root.getLocation());
			}
		}
	}
}
