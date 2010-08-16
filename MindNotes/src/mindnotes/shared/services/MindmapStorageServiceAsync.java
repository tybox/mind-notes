package mindnotes.shared.services;

import java.util.List;

import mindnotes.shared.model.MindMap;
import mindnotes.shared.model.MindMapInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface MindmapStorageServiceAsync {

	void getAvailableMindmaps(AsyncCallback<List<MindMapInfo>> callback);

	void loadMindmap(String key, AsyncCallback<MindMap> callback);

	void removeMindmap(String key, AsyncCallback<Void> callback);

	void setMapPublic(String key, boolean isPublic, AsyncCallback<Void> callback);

	void getMapPublic(String key, AsyncCallback<Boolean> callback);

	void loadMindmapPublic(String key, AsyncCallback<MindMap> callback);

	void saveMindmap(String key, MindMap map,
			AsyncCallback<MindMapInfo> callback);

}
