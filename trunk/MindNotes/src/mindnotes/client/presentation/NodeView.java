package mindnotes.client.presentation;

import mindnotes.client.ui.NodeWidget;
import mindnotes.shared.model.NodeLocation;

public interface NodeView {

	public interface Listener {
		public void nodeClickedGesture(NodeView sender);

		public void nodeTextEditedGesture(NodeView view, String oldText,
				String newText);

		public void nodeEditFinishedGesture(NodeWidget nodeWidget);
	}

	public void setListener(Listener listener);

	public NodeView createChild();

	public void removeChild(NodeView display);

	public void removeAll();

	public void setText(String text);

	public void setLocation(NodeLocation location);

	public void setSelected(boolean isSelected);

	public void setExpanded(boolean isExpanded);

	public void delete();

}
