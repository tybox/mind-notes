package mindnotes.client.ui;

import mindnotes.client.model.NodeLocation;
import mindnotes.client.presentation.NodeView;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class NodeWidget extends Composite implements NodeView {

	private DeckPanel _bubble;
	private Label _label;
	private TextBox _textBox;
	private FlowPanel _childPanel;
	private HorizontalPanel _container;
	private ArrowMaker _arrowMaker;
	private Listener _listener;

	public NodeWidget() {

		_bubble = new DeckPanel();
		_textBox = new TextBox();
		_label = new Label();
		_bubble.add(_label);
		_bubble.add(_textBox);
		_bubble.showWidget(0);
		_container = new HorizontalPanel();
		_childPanel = new FlowPanel();
		
		_container.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		_container.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		_container.add(_bubble);
		_container.add(_childPanel);

		//DOM.setStyleAttribute(_childPanel.getElement(), "marginLeft", "200px");
		
		_bubble.setStylePrimaryName("mindmap");
		_bubble.addStyleDependentName("node");
		initWidget(_container);
	}
	
	public void setArrowMaker(ArrowMaker arrowMaker) {
		_arrowMaker = arrowMaker;
	}
	
	@Override
	public NodeView createChild() {
		NodeWidget child = new NodeWidget();
		child.setArrowMaker(_arrowMaker);
		_childPanel.add(child);
		if (_arrowMaker != null) {
			_arrowMaker.addArrow(this, child);
		}
		return child;
	}

	@Override
	public void removeAll() {
		if (_arrowMaker != null) {
			for(int i=0; i < _childPanel.getWidgetCount(); i++) {
				_arrowMaker.removeArrow(this, ((NodeWidget)_childPanel.getWidget(i)));
			}
		}
		_childPanel.clear();
	}

	@Override
	public void removeChild(NodeView view) {
		if (view instanceof NodeWidget) {
			_arrowMaker.removeArrow(this, (NodeWidget)view);
			_childPanel.remove((NodeWidget)view);
		}
	}

	@Override
	public void setLocation(NodeLocation location) {
		
	}

	@Override
	public void setText(String text) {
		_label.setText(text);
		
	}
	
	public int getBubbleWidth() {
		return _bubble.getElement().getClientWidth();
	}
	
	public int getBubbleHeight() {
		return _bubble.getElement().getClientHeight();
	}

	@Override
	public void setListener(Listener listener) {
		// TODO current impl does not remove listeners if we change the listener
		_listener = listener;
		if (_listener != null) {
			_label.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					_listener.nodeClicked(NodeWidget.this);
				}
			});
		}
		
	}

	@Override
	public void setSelected(boolean isSelected) {
		if (isSelected) {
			_bubble.addStyleDependentName("node-selected");
			int w = _label.getOffsetWidth();
			//int h = _label.getOffsetHeight();
			_textBox.setText(_label.getText());
			_textBox.setWidth(w+"px");
			_bubble.showWidget(1); // show text box;
			if (_listener != null) _listener.nodeResize(this);
			
		} else {
			_bubble.removeStyleDependentName("node-selected");
			_bubble.showWidget(0); // show label;
			
			if (_listener != null) {
				if (!_label.getText().equals(_textBox.getText())) {
					_listener.nodeTextEdited(this, _label.getText(), _textBox.getText());
				}
				_listener.nodeResize(this);
			}
		}
	}

	public int getBubbleTop() {
		return _bubble.getAbsoluteTop();
	}
	
	public int getBubbleLeft() {
		return _bubble.getAbsoluteLeft();
	}

	@Override
	public void delete() {
		
		// look on widget chain for parenting widget
		Widget w = this;
		do {
			w = w.getParent();
		} while (! (w instanceof NodeWidget));
		if (w != null) {
			((NodeWidget)w).removeChild(this);
		}
	}
}
