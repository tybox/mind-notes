package mindnotes.client.ui;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

public class ActionsPanel extends Composite {
	
	private Button _addButton;
	private Button _deleteButton;

	private FlowPanel _container;
	private MindMapWidget _mapWidget;
	
	public ActionsPanel(MindMapWidget mapWidget) {
		_mapWidget = mapWidget;
		
		_addButton = new Button("Add Child");
		_deleteButton = new Button("Delete");
		
		
		_container = new FlowPanel();
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
		setVisible(true);
	}
	
	public HasClickHandlers getAddButton() {
		return _addButton;
	}
	
	public HasClickHandlers getDeleteButton() {
		return _deleteButton;
	}
}
