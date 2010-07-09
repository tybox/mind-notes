package mindnotes.client;

import mindnotes.client.presentation.MindMapEditor;
import mindnotes.client.ui.MindNotesUI;
import mindnotes.shared.model.MindMap;
import mindnotes.shared.model.Node;
import mindnotes.shared.model.NodeLocation;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class MindNotes implements EntryPoint {

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		MindNotesUI mindNotesUI = new MindNotesUI();

		final MindMapEditor presenter = new MindMapEditor(
				mindNotesUI.getMindMapView());

		RootLayoutPanel.get().add(mindNotesUI);

		DOM.getElementById("loader").removeFromParent();

		final MindMap mm = new MindMap();
		Node n = new Node();
		n.setText("<b>Hello</b>");
		Node n1 = new Node();
		n1.setText("<b>World 1-1</b>");
		n1.setNodeLocation(NodeLocation.LEFT);
		Node n2 = new Node();
		n2.setText("<b>World 1-2</b>");
		n2.setNodeLocation(NodeLocation.RIGHT);
		n.addChildNode(n1);
		n.addChildNode(n2);
		mm.setRootNode(n);

		// set the mindmap as a deferred command to let all the UI to set up
		// properly;
		DeferredCommand.addCommand(new Command() {

			@Override
			public void execute() {
				presenter.setMindMap(mm);
			}
		});

	}
}
