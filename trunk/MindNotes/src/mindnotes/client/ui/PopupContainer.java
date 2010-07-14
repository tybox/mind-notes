package mindnotes.client.ui;

import com.google.gwt.user.client.ui.Widget;

/**
 * A PopupContainer is a behaviour created as a workaround to the following
 * problem:
 * 
 * Imagine you have a widget inside a container with absolute positioning
 * (AbsolutePanel). The container is inside a scrollable panel. You want to
 * create a popup with a toolbox next to this widget. The obvious choice would
 * be to use PopupPanel - but PopupPanel attaches itself to the browser's client
 * area. If the scroll panel would be scrolled when the popup is visible, it's
 * position won't change.
 * 
 * As a workaround, the popup behaviour is recreated by adding the popup widget
 * at a specified position relative to the anchor with a specified offset (top,
 * left). Most probably a PopupContainer is an AbsolutePanel inside a
 * ScrollPanel that places the popup _inside_ the scroll area, preserving scroll
 * behaviour.
 * 
 * @author dominik
 * 
 */
public interface PopupContainer {

	public void showPopup(Widget anchor, int top, int left, Widget popup);

}
