package mindnotes.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

public class ActionsPanel extends Composite {

	public interface Listener {
		
		public void addClicked();
		public void addRightClicked();
		public void addLeftClicked();
		public void deleteClicked();

	}

	private Button _deleteButton;

	private FlowPanel _container;
	private MindMapWidget _mapWidget;
	private Button _addLeftButton;
	private Button _addRightButton;
	private Button _addButton;
	private Listener _listener;

	public ActionsPanel(MindMapWidget mapWidget) {
		_mapWidget = mapWidget;

		_addLeftButton = new Button("Add Left");
		_addRightButton = new Button("Add Right");
		_addButton = new Button("Add");
		_deleteButton = new Button("Delete");

		// events
		{
			_addButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					if (_listener != null)
						_listener.addClicked();
				}
			});
			_addLeftButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					if (_listener != null)
						_listener.addLeftClicked();
				}
			});
			_addRightButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					if (_listener != null)
						_listener.addRightClicked();
				}
			});
			_deleteButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					if (_listener != null)
						_listener.deleteClicked();
				}
			});
		}

		_container = new FlowPanel();
		_container.add(_addLeftButton);
		_container.add(_addRightButton);
		_container.add(_addButton);
		_container.add(_deleteButton);
		_container.setStylePrimaryName("actionspanel");

		initWidget(_container);
	}

	public void showNextTo(NodeWidget widget) {
		int x, y;
		x = _mapWidget.getNodeRelativeLeft(widget);
		y = _mapWidget.getNodeRelativeTop(widget) + widget.getBubbleHeight();

		_mapWidget.getViewportPanel().setWidgetPosition(this, x, y);

		boolean isroot = widget.getParentNodeWidget() == null;
		_addLeftButton.setVisible(isroot);
		_addRightButton.setVisible(isroot);
		_addButton.setVisible(!isroot);

		setVisible(true);
	}

	public void setListener(Listener listener) {
		_listener = listener;
	}
}
