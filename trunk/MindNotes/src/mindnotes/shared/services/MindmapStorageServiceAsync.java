package mindnotes.shared.services;

import java.util.List;

import mindnotes.shared.model.MindMap;
import mindnotes.shared.model.MindMapInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface MindmapStorageServiceAsync {

	void getAvailableMindmaps(AsyncCallback<List<MindMapInfo>> callback);

	void loadMindmap(String key, AsyncCallback<MindMap> callback);

	void saveMindmap(MindMap map, AsyncCallback<Void> callback);

}
