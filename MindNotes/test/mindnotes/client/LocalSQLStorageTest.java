package mindnotes.client;

import mindnotes.client.storage.LocalSQLStorage;
import mindnotes.shared.model.MindMap;
import mindnotes.shared.model.Node;
import mindnotes.shared.model.NodeLocation;

import com.google.gwt.junit.client.GWTTestCase;

public class LocalSQLStorageTest extends GWTTestCase {

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

		node(m.getRootNode(), "sadas", true, NodeLocation.LEFT);
		node(m.getRootNode(), null, true, NodeLocation.LEFT);
		node(m.getRootNode(), "dsdsd", true, null);
		node(n2, "dsdsd", true, null);
		LocalSQLStorage storage = new LocalSQLStorage();
		storage.saveMindMapSync(m);
	}

	@Override
	public String getModuleName() {
		return "mindnotes.MindNotes";
	}

}
