package mindnotes.client;

import static junit.framework.Assert.assertEquals;
import mindnotes.client.model.NodeLocation;
import mindnotes.client.ui.NodeLayout;

import org.junit.Test;

public class LayoutTest {

	@Test
	public void testLayout1() {
		MockLayoutTreeElement root = new MockLayoutTreeElement(0, 0, 200, 100,
				null, null);

		MockLayoutTreeElement child = new MockLayoutTreeElement(0, 0, 200, 100,
				root, NodeLocation.LEFT);
		root.addChild(child);

		NodeLayout layout = new NodeLayout();
		layout.doLayout(root);

		assertEquals(448, root.getBranchBounds().w);
		assertEquals(100, root.getBranchBounds().h);
		assertEquals(-248, child.getOffsetX());
		assertEquals(0, child.getOffsetY());

	}

	@Test
	public void testLayout2() {
		MockLayoutTreeElement root = new MockLayoutTreeElement(0, 0, 200, 100,
				null, null);

		MockLayoutTreeElement child = new MockLayoutTreeElement(0, 0, 200, 100,
				root, NodeLocation.RIGHT);
		root.addChild(child);

		NodeLayout layout = new NodeLayout();
		layout.doLayout(root);

		assertEquals(448, root.getBranchBounds().w);
		assertEquals(100, root.getBranchBounds().h);
		assertEquals(248, child.getOffsetX());
		assertEquals(0, child.getOffsetY());

	}

	@Test
	public void testLayout3() {
		MockLayoutTreeElement root = new MockLayoutTreeElement(0, 0, 200, 100,
				null, null);

		MockLayoutTreeElement child1 = new MockLayoutTreeElement(0, 0, 200,
				100, root, NodeLocation.LEFT);
		MockLayoutTreeElement child2 = new MockLayoutTreeElement(0, 0, 200,
				100, child1, NodeLocation.LEFT);
		root.addChild(child1);
		child1.addChild(child2);

		NodeLayout layout = new NodeLayout();
		layout.doLayout(root);

		assertEquals(696, root.getBranchBounds().w);
		assertEquals(100, root.getBranchBounds().h);

		assertEquals(-248, child1.getOffsetX());
		assertEquals(0, child1.getOffsetY());
		assertEquals(-248, child2.getOffsetX());
		assertEquals(0, child2.getOffsetY());

	}

}
