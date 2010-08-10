package mindnotes.server;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import mindnotes.shared.model.MindMap;

import com.google.appengine.api.datastore.Key;

/**
 * A DataStore representation of a mindmap.
 * 
 * @author dominik
 * 
 */
@PersistenceCapable
public class DSMindMap {

	/**
	 * the serializable version of the mindmap, which will be saved in the
	 * datastore as a blob
	 */
	@Persistent(serialized = "true")
	private MindMap map;

	@Persistent
	private String title;

	@Persistent
	private Boolean isPublic;

	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@PrimaryKey
	private Key key;

	/**
	 * the id of user; following recommendation of
	 * http://code.google.com/appengine
	 * /docs/java/users/overview.html#Users_and_the_Datastore, storing user id
	 * is preferred over storing the User object
	 */
	@Persistent
	private String userID;

	public DSMindMap() {

	}

	public DSMindMap(MindMap mm) {
		setMap(mm);
		setTitle(mm.getTitle());
	}

	public MindMap getMap() {
		return map;
	}

	public void setMap(MindMap map) {
		this.map = map;
	}

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getUserID() {
		return userID;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public boolean getPublic() {
		if (isPublic == null)
			return false;
		return isPublic;
	}
}
