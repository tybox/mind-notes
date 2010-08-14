package mindnotes.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;

public class NodeContextMenu extends PopupPanel {

	interface Resources extends ClientBundle {
		@Source("NodeContextMenu.css")
		Style style();
	}

	interface Style extends CssResource {
		public String contextMenu();
	}

	Resources _resources = GWT.create(Resources.class);

	public interface Listener {
		public void onMenuDelete();

		public void onMenuCut();

		public void onMenuCopy();

		public void onMenuPaste();
	}

	private Listener _listener;

	public NodeContextMenu() {

		setAutoHideEnabled(true);

		MenuBar menu = new MenuBar(true);
		menu.addItem("Delete", new Command() {

			@Override
			public void execute() {
				hide();
				if (_listener != null)
					_listener.onMenuDelete();
			}

		});
		menu.addItem("Cut", new Command() {

			@Override
			public void execute() {
				hide();
				if (_listener != null)
					_listener.onMenuCut();
			}

		});
		menu.addItem("Copy", new Command() {

			@Override
			public void execute() {
				hide();
				if (_listener != null)
					_listener.onMenuCopy();
			}

		});
		menu.addItem("Paste", new Command() {

			@Override
			public void execute() {
				hide();
				if (_listener != null)
					_listener.onMenuPaste();
			}

		});

		setStyleName(_resources.style().contextMenu());

		add(menu);
	}

	public void showContextMenu(final int clientX, final int clientY) {
		setPopupPositionAndShow(new PositionCallback() {

			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				positionMenu(clientX, clientY, offsetWidth, offsetHeight);
			}
		});
	}

	/**
	 * Position the menu so that it doesn't appear outside the window frame.
	 * 
	 * @param clientX
	 * @param clientY
	 * @param offsetWidth
	 * @param offsetHeight
	 */
	protected void positionMenu(int clientX, int clientY, int offsetWidth,
			int offsetHeight) {

		int rightEdge = Window.getClientWidth();
		int bottomEdge = Window.getClientHeight();

		int popupRightEdge = clientX + offsetWidth;
		int popupBottomEdge = clientY + offsetHeight;

		int left = (popupRightEdge > rightEdge) ? clientX - offsetWidth
				: clientX;
		int top = (popupBottomEdge > bottomEdge) ? clientY - offsetHeight
				: clientY;

		setPopupPosition(left, top);

	}

	public void setListener(Listener listener) {
		_listener = listener;
	}

	public Listener getListener() {
		return _listener;
	}

}
