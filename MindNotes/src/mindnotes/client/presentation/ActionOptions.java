package mindnotes.client.presentation;

public interface ActionOptions {

	boolean canExpand();

	boolean canAddLeft();

	boolean canAddRight();

	boolean canHaveSiblings();

	boolean canDelete();

	boolean isExpanded();

}