package mindnotes.shared.model;

import java.io.Serializable;

public class EmbeddedObject implements Serializable {

	private static final long serialVersionUID = 2037552914857086834L;

	private String _type;
	private String _data;

	public EmbeddedObject() {
	}

	public EmbeddedObject(String type, String data) {
		_type = type;
		_data = data;

	}

	public void setType(String type) {
		_type = type;
	}

	public String getType() {
		return _type;
	}

	public void setData(String data) {
		_data = data;
	}

	public String getData() {
		return _data;
	}

	public EmbeddedObject makeClone() {
		EmbeddedObject o = new EmbeddedObject();
		o.setData(getData());
		o.setType(getType());
		return o;
	}

	@Override
	public boolean equals(Object arg0) {
		if (arg0 == null || !(arg0 instanceof EmbeddedObject))
			return false;
		EmbeddedObject eo = (EmbeddedObject) arg0;
		return ((_data == null) ? eo._data == null : _data.equals(eo._data))
				&& ((_type == null) ? eo._type == null : _type.equals(eo._type));
	}
}
