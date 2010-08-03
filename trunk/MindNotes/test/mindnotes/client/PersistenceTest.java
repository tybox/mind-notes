package mindnotes.client;

import java.util.Iterator;

import mindnotes.client.storage.JSONMindMapBuilder;
import mindnotes.shared.model.EmbeddedObject;
import mindnotes.shared.model.MindMap;
import mindnotes.shared.model.Node;
import mindnotes.shared.model.NodeLocation;

import com.google.gwt.junit.client.GWTTestCase;

public class PersistenceTest extends GWTTestCase {

	public void testBasicMap() {
		MindMap m = new MindMap();
		m.getRootNode().addChildNode(new Node());
		m.getRootNode().addChildNode(new Node());
		Node n = new Node();
		m.getRootNode().addChildNode(n);
		m.getRootNode().addChildNode(n);
		roundtrip(m);
	}

	public Node node(Node parent, String title, boolean expanded,
			NodeLocation nl) {
		Node n = new Node();
		n.setExpanded(expanded);
		n.setText(title);
		n.setNodeLocation(nl);
		parent.addChildNode(n);
		return n;
	}

	public void testMap1() {
		MindMap m = new MindMap();
		m.setTitle("a title");
		Node n1 = node(m.getRootNode(), "n1", true, NodeLocation.LEFT);
		Node n2 = node(n1, "n2", false, NodeLocation.RIGHT);
		node(n1, "n3", false, NodeLocation.RIGHT);
		node(n2, "n3", false, NodeLocation.RIGHT);
		n2.addObject(new EmbeddedObject("yt", "dfcsdf"));
		node(m.getRootNode(), "sadas", true, NodeLocation.LEFT);
		node(m.getRootNode(), null, true, NodeLocation.LEFT);
		node(m.getRootNode(), "dsdsd", true, null);
		node(n2, "dsdsd", true, null);
		roundtrip(m);
	}

	public void roundtrip(MindMap m) {
		MindMap m1 = m;
		JSONMindMapBuilder jmmb = new JSONMindMapBuilder();
		m1.copyTo(jmmb);
		MindMap m2 = new MindMap();
		jmmb.copyTo(m2);
		compareMaps(m1, m2);
	}

	private void compareMaps(MindMap m1, MindMap m2) {
		if (m1 == m2)
			return;
		assertEquals(m1.getTitle(), m2.getTitle());
		compareNode(m1.getRootNode(), m2.getRootNode());
	}

	private void compareNode(Node n1, Node n2) {
		if (n1 == n2)
			return;
		assertEquals(n1.getText(), n2.getText());
		assertEquals(n1.getChildCount(), n2.getChildCount());
		Iterator<Node> i1 = n1.getChildren().iterator();
		Iterator<Node> i2 = n2.getChildren().iterator();
		while (i1.hasNext()) {
			compareNode(i1.next(), i2.next());
		}

		assertEquals(n1.getObjects().size(), n2.getObjects().size());
		Iterator<EmbeddedObject> ie1 = n1.getObjects().iterator();
		Iterator<EmbeddedObject> ie2 = n2.getObjects().iterator();
		while (ie1.hasNext()) {
			assertEquals(ie1.next(), ie2.next());
		}
	}

	@Override
	public String getModuleName() {
		return "mindnotes.MindNotes";
	}

}
