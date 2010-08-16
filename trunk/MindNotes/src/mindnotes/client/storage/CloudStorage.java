package mindnotes.client.storage;

import java.util.List;

import mindnotes.shared.model.MindMap;
import mindnotes.shared.model.MindMapInfo;
import mindnotes.shared.services.MindmapStorageService;
import mindnotes.shared.services.MindmapStorageServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class CloudStorage implements Storage {

	MindmapStorageServiceAsync _mindmapStorage = GWT
			.create(MindmapStorageService.class);

	@Override
	public void getStoredMaps(AsyncCallback<List<MindMapInfo>> callback) {
		_mindmapStorage.getAvailableMindmaps(callback);
	}

	@Override
	public void loadMindMap(MindMapInfo map, AsyncCallback<MindMap> callback) {
		_mindmapStorage.loadMindmap(map.getKey(), callback);
	}

	@Override
	public void saveMindMap(String key, MindMap map,
			AsyncCallback<MindMapInfo> callback) {
		_mindmapStorage.saveMindmap(key, map, callback);
	}

	@Override
	public void remove(MindMapInfo map, AsyncCallback<Void> callback) {
		_mindmapStorage.removeMindmap(map.getKey(), callback);
	}

	public MindmapStorageServiceAsync getService() {
		return _mindmapStorage;
	}

}
