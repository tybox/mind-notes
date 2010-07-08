package mindnotes.shared.model;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Defines the position of the node in the diagram relative to its parent node.
 * 
 * @author dominik
 * 
 */
public enum NodeLocation implements IsSerializable {
	LEFT, RIGHT, ROOT
}
