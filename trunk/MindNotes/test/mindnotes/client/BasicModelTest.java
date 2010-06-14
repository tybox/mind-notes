package mindnotes.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import mindnotes.client.model.MindMap;
import mindnotes.client.model.Node;

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
		for(Node child: rootNode.getChildren()) {
			assertEquals(rootNode, child.getParent());
			checkNodeTreeIntegrity(child);
		}
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
