package mindnotes.client;

import mindnotes.client.presentation.MindMapEditor;
import mindnotes.client.presentation.MindMapView;
import mindnotes.shared.model.MindMap;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

public class MindMapEditorTest {

	private MindMapView mockView;
	private MindMapEditor editor;

	@Before
	public void setUp() {
		mockView = EasyMock.createMock(MindMapView.class);
		editor = new MindMapEditor(mockView);
	}

	@Test
	public void selectOnceTest() {
		EasyMock.replay(mockView);

		MindMap m = new MindMap();
		editor.setMindMap(m);
		editor.setCurrentNode(m.getRootNode());
	}
}
