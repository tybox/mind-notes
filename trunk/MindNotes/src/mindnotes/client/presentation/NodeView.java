package mindnotes.client.presentation;

import mindnotes.client.model.NodeLocation;
import mindnotes.client.ui.NodeWidget;

public interface NodeView {
	
	public interface Listener {
		public void nodeClicked(NodeView sender);
		public void nodeResize(NodeView sender);
		public void nodeTextEdited(NodeView view, String oldText, String newText);
		public void nodeEditFinished(NodeWidget nodeWidget);
	}
	
	public void setListener(Listener listener);
	
	public NodeView createChild();
	public void removeChild(NodeView display);
	public void removeAll();
	
	public void setText(String text);
	public void setLocation(NodeLocation location);
	public void setSelected(boolean isSelected);

	public void delete();
}
