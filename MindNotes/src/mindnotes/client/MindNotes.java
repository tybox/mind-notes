package mindnotes.client;

import mindnotes.client.presentation.MindMapEditor;
import mindnotes.client.ui.MindNotesUI;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.DOM;
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

		presenter.newMindMap();

	}
}
