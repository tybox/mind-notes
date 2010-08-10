package mindnotes.client.storage;

import java.util.List;

import mindnotes.shared.model.MindMap;
import mindnotes.shared.model.MindMapInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Storage {

	public void getStoredMaps(AsyncCallback<List<MindMapInfo>> callback);

	public void loadMindMap(MindMapInfo map, AsyncCallback<MindMap> callback);

	public void remove(MindMapInfo map, AsyncCallback<Void> asyncCallback);

	public void saveMindMap(MindMap map, AsyncCallback<MindMapInfo> callback);
}
