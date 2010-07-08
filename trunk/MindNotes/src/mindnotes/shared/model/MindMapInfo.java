package mindnotes.shared.model;

import com.google.gwt.user.client.rpc.IsSerializable;

public class MindMapInfo implements IsSerializable {
	private String _title;
	private String _key;

	public MindMapInfo() {

	}

	public MindMapInfo(String key, String title) {
		_key = key;
		_title = title;
	}

	public void setKey(String key) {
		_key = key;
	}

	public String getKey() {
		return _key;
	}

	public void setTitle(String title) {
		_title = title;
	}

	public String getTitle() {
		return _title;
	}

}
