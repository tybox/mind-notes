package mindnotes.client.presentation;

import java.util.List;

import mindnotes.shared.model.MindMapInfo;

public interface MindMapSelectionView {

	public interface Listener {
		public void mindMapChosen(MindMapInfo map);
	}

	public void setListener(Listener listener);

	public void setMindMaps(List<MindMapInfo> mindmaps);

	public void askForCloudDocumentSelection();

}