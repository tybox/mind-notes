package mindnotes.client.presentation;

import mindnotes.client.ui.NodeWidget;
import mindnotes.shared.model.NodeLocation;

public interface NodeView {

	public interface Listener {
		public void nodeClickedGesture(NodeView sender);

		public void nodeDoubleClickedGesture(NodeView sender);

		public void nodeTextEditedGesture(NodeView view, String oldText,
				String newText);

		public void nodeEditFinishedGesture(NodeWidget nodeWidget);

		public void nodeMouseDownGesture(NodeWidget nodeWidget);

		public void onBranchDropped();

		public void onBranchDragged(int index, NodeLocation location);
	}

	public void setListener(Listener listener);

	public NodeView createChild();

	public NodeView createChildBefore(NodeView view);

	public NodeView createChildAfter(NodeView view);

	public void removeChild(NodeView display);

	public void removeAll();

	public void setText(String text);

	public void setLocation(NodeLocation location);

	/**
	 * @see Selection for difference between selected node and current node.
	 * @param isSelected
	 */
	public void setSelectionState(SelectionState state);

	public void setExpanded(boolean isExpanded);

	public void delete();

	public EmbeddedObjectView createEmbeddedObject(String type, String data);

	public void removeEmbeddedObject(EmbeddedObjectView view);

	public void moveChild(NodeView childView, NodeView nodeView, int index,
			NodeLocation location);

}
