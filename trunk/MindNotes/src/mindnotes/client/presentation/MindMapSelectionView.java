package mindnotes.client.presentation;

import java.util.List;

import mindnotes.shared.model.MindMapInfo;

public interface MindMapSelectionView {

	public interface Listener {
		public void mindMapChosen(MindMapInfo map, boolean local);

		public void mindMapRemove(MindMapInfo document, boolean local);
	}

	public void setListener(Listener listener);

	public void setMindMaps(List<MindMapInfo> mindmaps);

	public void setLocalMindMaps(List<MindMapInfo> mindmaps);

	public void askForCloudDocumentSelection();

}