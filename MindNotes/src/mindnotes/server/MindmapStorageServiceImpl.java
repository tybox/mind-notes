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
	public MindMapInfo saveMindmap(MindMap map) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			DSMindMap dsMindMap = new DSMindMap(map);
			dsMindMap.setUserID(getCurrentUserID());
			pm.makePersistent(dsMindMap);

			return new MindMapInfo(KeyFactory.keyToString(dsMindMap.getKey()),
					dsMindMap.getTitle());

		} finally {
			pm.close();
		}
	}

	@Override
	public MindMap loadMindmap(String key) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			return loadDSMindMap(pm, key, false).getMap();
		} finally {
			pm.close();
		}
	}

	@Override
	public MindMap loadMindmapPublic(String key) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			return loadDSMindMap(pm, key, true).getMap();
		} finally {
			pm.close();
		}
	}

	/**
	 * @param key
	 * @return
	 */
	private DSMindMap loadDSMindMap(PersistenceManager pm, String key,
			boolean hasToBePublic) {
		Query query = createSingleSelectQuery(pm);

		@SuppressWarnings("unchecked")
		List<DSMindMap> result = (List<DSMindMap>) query.execute(KeyFactory
				.stringToKey(key));

		if (!result.isEmpty()) {

			DSMindMap dsMindMap = result.get(0);
			// check if user is authorized to load this map;
			if (hasToBePublic) {
				if (!dsMindMap.getPublic()) {
					throw new SecurityException("Map is not public");
				}
			} else {
				authorizeUser(dsMindMap.getUserID());
			}
			return dsMindMap;
		} else {
			throw new RuntimeException("No such map");
		}

	}

	private void authorizeUser(String requiredID) {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if (user == null)
			throw new SecurityException("user not logged in");
		if (!user.getUserId().equals(requiredID)) {
			throw new SecurityException("user not authorized");
		}
	}

	@Override
	public void removeMindmap(String key) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query q = createSingleSelectQuery(pm);
		try {
			// loadMindmap also does a security check
			if (loadMindmap(key) != null)
				q.deletePersistentAll(KeyFactory.stringToKey(key));
		} finally {
			pm.close();
		}
	}

	private Query createSingleSelectQuery(PersistenceManager pm) {
		Query query = pm.newQuery(DSMindMap.class);
		query.setFilter("key == keyParam");
		query.declareParameters("com.google.appengine.api.datastore.Key keyParam");
		return query;
	}

	@Override
	public List<MindMapInfo> getAvailableMindmaps() {
		String userID = getCurrentUserID();

		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm
				.newQuery("select key, title from mindnotes.server.DSMindMap where userID == userIDParam parameters String userIDParam");

		try {
			@SuppressWarnings("unchecked")
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

	@Override
	public void setMapPublic(String key, boolean isPublic) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			DSMindMap map = loadDSMindMap(pm, key, false);
			map.setPublic(isPublic);
		} finally {
			pm.close();
		}
	}

	@Override
	public boolean getMapPublic(String key) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			DSMindMap map = loadDSMindMap(pm, key, false);
			return map.getPublic();
		} finally {
			pm.close();
		}
	}

}
