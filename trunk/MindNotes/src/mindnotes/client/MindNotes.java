package mindnotes.client;

import mindnotes.client.model.MindMap;
import mindnotes.client.model.Node;
import mindnotes.client.presentation.MindMapPresenter;
import mindnotes.client.ui.MindNotesUI;

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
		
		final MindMapPresenter presenter = new MindMapPresenter(mindNotesUI.getMindMapView());
		
		RootLayoutPanel.get().add(mindNotesUI);
		
		DOM.getElementById("loader").removeFromParent();

		final MindMap mm = new MindMap();
		Node n = new Node();
		n.setText("Hello");
		Node n1 = new Node();
		n1.setText("World 1-1");
		Node n2 = new Node();
		n2.setText("World 1-2");
		n.addChildNode(n1);
		n.addChildNode(n2);
		mm.setRootNode(n);
		
		// set the mindmap as a deferred command to let all the UI to set up properly;
		DeferredCommand.addCommand(new Command() {
			
			@Override
			public void execute() {
				presenter.setMindMap(mm);
			}
		});
		
		
		

	}
}
