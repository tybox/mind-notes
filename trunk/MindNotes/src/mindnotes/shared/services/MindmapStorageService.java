package mindnotes.shared.services;

import java.util.List;

import mindnotes.shared.model.MindMap;
import mindnotes.shared.model.MindMapInfo;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath(value = "network/storage")
public interface MindmapStorageService extends RemoteService {

	public MindMapInfo saveMindmap(String key, MindMap map);

	public MindMap loadMindmap(String key);

	public MindMap loadMindmapPublic(String key);

	public List<MindMapInfo> getAvailableMindmaps();

	public void removeMindmap(String key);

	public void setMapPublic(String key, boolean isPublic);

	public boolean getMapPublic(String key);
}
