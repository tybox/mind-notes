package mindnotes.client.ui;

public class Arrow {

	public NodeWidget from;
	public NodeWidget to;

	public Arrow(NodeWidget from, NodeWidget to) {
		this.from = from;
		this.to = to;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Arrow))
			return false;
		Arrow a = (Arrow) obj;
		return (a.from == this.from) && (a.to == this.to);
	}

	@Override
	public int hashCode() {
		// xor two hashes together
		return (from == null ? 0 : from.hashCode())
				^ (to == null ? 0 : to.hashCode());
	}

}