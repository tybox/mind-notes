package mindnotes.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Iterator;

import mindnotes.shared.model.MindMap;
import mindnotes.shared.model.Node;

import org.junit.Test;

public class BasicModelTest {

	@Test
	public void testAddingNodes() {
		MindMap m = new MindMap();
		assertNotNull(m.getRootNode());
		m.getRootNode().addChildNode(new Node());
		m.getRootNode().addChildNode(new Node());
		Node n = new Node();
		m.getRootNode().addChildNode(n);
		m.getRootNode().addChildNode(n);

		assertEquals(3, m.getRootNode().getChildCount());
		checkNodeTreeIntegrity(m.getRootNode());
	}

	public void checkNodeTreeIntegrity(Node rootNode) {
		for (Node child : rootNode.getChildren()) {
			assertEquals(rootNode, child.getParent());
			checkNodeTreeIntegrity(child);
		}
	}

	@Test
	public void testInsertAfter() {
		MindMap m = new MindMap();
		assertNotNull(m.getRootNode());
		m.getRootNode().addChildNode(new Node());
		Node n = new Node();
		m.getRootNode().addChildNode(n);
		m.getRootNode().addChildNode(new Node());

		Node x = new Node();
		m.getRootNode().insertAfter(x, n);

		Iterator<Node> iterator = m.getRootNode().getChildren().iterator();
		for (int i = 0; iterator.hasNext(); i++) {
			Node next = iterator.next();
			if (next == n) {
				assertEquals(1, i);
				assertEquals(x, iterator.next());
			}

		}
		checkNodeTreeIntegrity(m.getRootNode());
	}

	@Test
	public void testInsertBefore() {
		MindMap m = new MindMap();
		assertNotNull(m.getRootNode());
		m.getRootNode().addChildNode(new Node());
		Node n = new Node();
		m.getRootNode().addChildNode(n);
		m.getRootNode().addChildNode(new Node());

		Node x = new Node();
		m.getRootNode().insertBefore(x, n);

		Iterator<Node> iterator = m.getRootNode().getChildren().iterator();
		for (int i = 0; iterator.hasNext(); i++) {
			Node next = iterator.next();
			if (next == x) {
				assertEquals(1, i);
				assertEquals(n, iterator.next());
			}

		}
		checkNodeTreeIntegrity(m.getRootNode());
	}

	@Test
	public void testRemovingNodes() {
		MindMap m = new MindMap();
		assertNotNull(m.getRootNode());
		m.getRootNode().addChildNode(new Node());
		m.getRootNode().addChildNode(new Node());
		Node n = new Node();
		m.getRootNode().addChildNode(n);
		m.getRootNode().addChildNode(n);

		m.getRootNode().removeChildNode(n);

		assertEquals(2, m.getRootNode().getChildCount());
		assertFalse(m.getRootNode().hasChildNode(n));

	}
}
