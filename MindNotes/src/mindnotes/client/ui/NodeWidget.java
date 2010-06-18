package mindnotes.client.ui;

import mindnotes.client.model.NodeLocation;
import mindnotes.client.presentation.NodeView;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;

public class NodeWidget extends Composite implements NodeView {

	private DeckPanel _bubble;
	private Label _label;
	private TextBox _textBox;
	private FlowPanel _childPanelRight;
	private HorizontalPanel _container;
	private ArrowMaker _arrowMaker;
	private Listener _listener;
	private FlowPanel _childPanelLeft;

	public NodeWidget() {

		_bubble = new DeckPanel();
		_textBox = new TextBox();
		_textBox.setStylePrimaryName("node");
		_textBox.addStyleDependentName("textBox");
		
		_textBox.addKeyPressHandler(new KeyPressHandler() {
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getCharCode() == KeyCodes.KEY_ENTER) {
					if (_listener != null) {
						_listener.nodeEditFinished(NodeWidget.this);
					}
				}
			}
		});
		
		_label = new Label();
		_label.setStylePrimaryName("node");
		_label.addStyleDependentName("text");
		
		_bubble.add(_label);
		_bubble.add(_textBox);
		_bubble.showWidget(0);
		_container = new HorizontalPanel();
		_childPanelRight = new FlowPanel();
		_childPanelLeft = new FlowPanel();
		
		_container.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		_container.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		
		_container.add(_childPanelLeft);
		_container.add(_bubble);
		_container.add(_childPanelRight);

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
		_childPanelRight.add(child);
		if (_arrowMaker != null) {
			_arrowMaker.addArrow(this, child);
		}
		maybeHidePanels();
		return child;
	}

	@Override
	public void removeAll() {
		if (_arrowMaker != null) {
			for(int i=0; i < _childPanelRight.getWidgetCount(); i++) {
				_arrowMaker.removeArrow(this, ((NodeWidget)_childPanelRight.getWidget(i)));
			}
		}
		_childPanelRight.clear();
		_childPanelLeft.clear();
		maybeHidePanels();
	}

	@Override
	public void removeChild(NodeView view) {
		if (view instanceof NodeWidget) {
			_arrowMaker.removeArrow(this, (NodeWidget)view);
			if(!_childPanelRight.remove((NodeWidget)view)) {
				_childPanelLeft.remove((NodeWidget)view);
			}
		}
		maybeHidePanels();
	}

	@Override
	public void setLocation(NodeLocation location) {
		NodeWidget parent = getParentNodeWidget();
		_childPanelRight.getElement().
		if (location == NodeLocation.LEFT) {
			_container.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		} else {
			_container.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		}
		if (parent != null) parent.setLocation(this, location);
	}

	public void setLocation(NodeWidget child, NodeLocation location) {

		switch (location) {
		case RIGHT:
			if (_childPanelLeft.remove(child)) _childPanelRight.add(child);
			break;

		case LEFT:
			if (_childPanelRight.remove(child)) _childPanelLeft.add(child);
			break;
		
		default:
			break;
		}
		maybeHidePanels();
		if (_listener != null) _listener.nodeResize(this);
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
			int w = _label.getElement().getClientWidth();
			_textBox.setText(_label.getText());
			_textBox.setWidth(w+"px");
			_bubble.showWidget(1); // show text box;
			_textBox.setFocus(true);
			_textBox.selectAll();
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
		
		NodeWidget w = getParentNodeWidget();
		
		if (w != null) {
			w.removeChild(this);
		}
	}
	
	public NodeWidget getParentNodeWidget() {
		// look on widget chain for parenting widget
		Widget w = this;
		do {
			w = w.getParent();
			if (w == null) break;
		} while (! (w instanceof NodeWidget));
		
		return (NodeWidget) w;
	}
	
	private void maybeHidePanels() {
		_childPanelLeft.setVisible(_childPanelLeft.getWidgetCount() > 0);
		_childPanelRight.setVisible(_childPanelRight.getWidgetCount() > 0);
	}
}
