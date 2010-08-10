package mindnotes.client;

import mindnotes.client.presentation.MindMapViewer;
import mindnotes.client.ui.MindNotesViewerUI;
import mindnotes.shared.model.MindMap;
import mindnotes.shared.services.MindmapStorageService;
import mindnotes.shared.services.MindmapStorageServiceAsync;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class MindNotesViewer implements EntryPoint {

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		String mapKey = Window.Location.getParameter("map");
		if (mapKey == null)
			return;
		MindmapStorageServiceAsync storage = GWT
				.create(MindmapStorageService.class);
		storage.loadMindmapPublic(mapKey, new AsyncCallback<MindMap>() {

			@Override
			public void onSuccess(MindMap result) {
				showMap(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				showError(new MindNotesViewerUI(),
						"I'm sorry, I cannot show you this map. ");
			}
		});

	}

	protected void showError(final MindNotesViewerUI ui, String string) {
		DOM.getElementById("loader").removeFromParent();
		ui.errorDialogLabel.setText(string);

		ui.errorDialog.setPopupPositionAndShow(new PositionCallback() {

			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				ui.errorDialog.setPopupPosition(
						(Window.getClientWidth() - offsetWidth) / 2,
						(Window.getClientHeight() - offsetHeight) / 2);
			}
		});

	}

	/**
	 * @param result
	 * 
	 */
	private void showMap(final MindMap result) {
		MindNotesViewerUI ui = new MindNotesViewerUI();
		final MindMapViewer presenter = new MindMapViewer();
		presenter.setView(ui.getMindMapView());
		ui.setMapTitle(result.getTitle());
		RootLayoutPanel.get().add(ui);

		DOM.getElementById("loader").removeFromParent();

		final MindMap mindMap = new MindMap();
		mindMap.setTitle("Viewer");
		mindMap.getRootNode().setText("Viewer Mindmap");
		DeferredCommand.addCommand(new Command() {

			@Override
			public void execute() {
				presenter.setMindMap(result);
			}

		});
	}
}
