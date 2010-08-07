package mindnotes.client.ui;

import com.google.gwt.user.client.ui.Widget;

public interface ButtonContainer {
	public void addButton(Widget w);

	public void setButtonPosition(Widget button, int x, int y);

	public int getRelativeTop(Widget button);

	public int getRelativeLeft(Widget button);
}
