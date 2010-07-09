package mindnotes.server;

import java.util.LinkedList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import mindnotes.shared.model.MindMap;
import mindnotes.shared.model.MindMapInfo;
import mindnotes.shared.services.MindmapStorageService;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class MindmapStorageServiceImpl extends RemoteServiceServlet implements
		MindmapStorageService {

	@Override
	public void saveMindmap(MindMap map) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			DSMindMap dsMindMap = new DSMindMap(map);
			dsMindMap.setUserID(getCurrentUserID());
			pm.makePersistent(dsMindMap);
			System.out.println("saved" + map + " to: "
					+ dsMindMap.getKey().getId());

		} finally {
			pm.close();
		}
	}

	@Override
	public MindMap loadMindmap(String key) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm.newQuery(DSMindMap.class);
		query.setFilter("key == keyParam");
		query.declareParameters("com.google.appengine.api.datastore.Key keyParam");
		List<DSMindMap> result = null;
		try {
			result = (List<DSMindMap>) query.execute(KeyFactory
					.stringToKey(key));
			if (!result.isEmpty()) {
				return result.get(0).getMap();
			}
		} finally {
			pm.close();
		}

		return null;
	}

	@Override
	public List<MindMapInfo> getAvailableMindmaps() {
		String userID = getCurrentUserID();

		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm
				.newQuery("select key, title from mindnotes.server.DSMindMap where userID == userIDParam parameters String userIDParam");

		try {
			List<Object[]> result = (List<Object[]>) query.execute(userID);
			List<MindMapInfo> mminfos = new LinkedList<MindMapInfo>();
			for (Object[] resultRow : result) {
				mminfos.add(new MindMapInfo(KeyFactory
						.keyToString((Key) resultRow[0]), (String) resultRow[1]));
			}
			return mminfos;
		} finally {
			pm.close();
		}

	}

	/**
	 * @return
	 */
	private String getCurrentUserID() {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if (user == null)
			throw new RuntimeException("user not logged in"); // panic

		return user.getUserId();
	}
}
