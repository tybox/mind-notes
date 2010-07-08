package mindnotes.shared.services;

import java.util.List;

import mindnotes.shared.model.MindMap;
import mindnotes.shared.model.MindMapInfo;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath(value = "storage")
public interface MindmapStorageService extends RemoteService {
	public void saveMindmap(MindMap map);

	public MindMap loadMindmap(String key);

	public List<MindMapInfo> getAvailableMindmaps();
}
