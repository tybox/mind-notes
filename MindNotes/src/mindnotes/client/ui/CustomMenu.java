package mindnotes.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/*
 * CustomMenu is a simple menu implementation that, contary to GWT's MenuBar, allows
 * arbitrary widgets as menu items. 
 */
public class CustomMenu {
	private FlowPanel _panel;

	public CustomMenu() {
		_panel = new FlowPanel();
	}

	public void fireFrom(Widget widget) {

	}

	public void fireAt(int x, int y) {

	}

	public void addItem(String text, Widget item) {

	}

	public void addItem(String text, final Command command) {
		Label item = new Label(text);
		item.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				command.execute();
			}
		});
		// addItem(item);
	}

}
