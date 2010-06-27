package mindnotes.client.ui;

import java.util.ArrayList;
import java.util.List;

import mindnotes.client.model.NodeLocation;
import mindnotes.client.presentation.NodeView;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

public class NodeWidget extends Composite implements NodeView,
		LayoutTreeElement {

	// external dependencies
	private ArrowMaker _arrowMaker;
	private NodeContainer _container;
	private Listener _listener;

	// bubble contents
	private DeckPanel _bubble;
	private Label _label;
	private TextBox _textBox;

	// node tree relatives
	private List<NodeWidget> _children;
	private NodeWidget _parent;

	// layout data
	private int _offsetX, _offsetY;
	private NodeLocation _nodeLocation;
	private Box _branchBounds;
	private boolean _layoutValid;

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
		_children = new ArrayList<NodeWidget>();

		_bubble.setStylePrimaryName("mindmap");
		_bubble.addStyleDependentName("node");
		initWidget(_bubble);
	}

	public void setContainer(NodeContainer container) {
		_container = container;
	}

	public void setArrowMaker(ArrowMaker arrowMaker) {
		_arrowMaker = arrowMaker;
	}

	@Override
	public NodeView createChild() {
		NodeWidget child = new NodeWidget();
		child.setArrowMaker(_arrowMaker);
		child.setContainer(_container);
		child.setLayoutParent(this);
		if (_arrowMaker != null) {
			_arrowMaker.addArrow(this, child);
		}
		_children.add(child);
		if (_container != null)
			_container.addNode(child);
		if (_container != null)
			_container.onNodeLayoutInvalidated(this);
		return child;
	}

	public void setLayoutParent(NodeWidget nodeWidget) {
		_parent = nodeWidget;
	}

	@Override
	public void removeAll() {
		removeArrowsInBranch();
		if (_container != null) {
			for (NodeWidget child : _children) {
				_container.removeNode(child);
			}
		}
		_children.clear();
		if (_container != null)
			_container.onNodeLayoutInvalidated(this);
	}

	@Override
	public void removeChild(NodeView view) {
		if (view instanceof NodeWidget) {
			NodeWidget child = (NodeWidget) view;
			if (_arrowMaker != null)
				_arrowMaker.removeArrow(this, child);
			child.removeArrowsInBranch();
			if (_container != null)
				_container.removeNode(child);
			_children.remove(child);
			if (_container != null)
				_container.onNodeLayoutInvalidated(this);
		}

	}

	protected void removeArrowsInBranch() {
		if (_arrowMaker != null) {
			for (NodeWidget child : _children) {
				_arrowMaker.removeArrow(this, child);
				child.removeArrowsInBranch();
			}
		}
	}

	@Override
	public void setLocation(NodeLocation location) {
		_nodeLocation = location;
		setLayoutValid(false);
		if (_container != null)
			_container.onNodeLayoutInvalidated(this);
	}

	@Override
	public void setText(String text) {
		_label.setText(text);
	}

	public int getBubbleWidth() {

		// / XXX dirty hack; 4 is for the border
		return _bubble.getElement().getClientWidth() + 4;
	}

	public int getBubbleHeight() {
		return _bubble.getElement().getClientHeight() + 4;
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
			_textBox.setWidth(w + "px");
			_bubble.showWidget(1); // show text box;
			_textBox.setFocus(true);
			_textBox.selectAll();
			if (_container != null)
				_container.onNodeLayoutInvalidated(this);

		} else {
			_bubble.removeStyleDependentName("node-selected");
			_bubble.showWidget(0); // show label;

			if (_listener != null) {
				if (!_label.getText().equals(_textBox.getText())) {
					_listener.nodeTextEdited(this, _label.getText(),
							_textBox.getText());
				}
			}
			if (_container != null)
				_container.onNodeLayoutInvalidated(this);
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
		return _parent;
	}

	@Override
	public List<? extends LayoutTreeElement> getLayoutChildren() {
		return _children;
	}

	public List<NodeWidget> getChildren() {
		return _children;
	}

	@Override
	public Box getBranchBounds() {
		return _branchBounds;
	}

	@Override
	public void setBranchBounds(Box box) {
		_branchBounds = box;
	}

	@Override
	public Box getElementBounds() {
		return new Box(0, 0, _bubble.getElement().getClientWidth(), _bubble
				.getElement().getClientHeight());
	}

	@Override
	public void setOffset(int x, int y) {
		_offsetX = x;
		_offsetY = y;
	}

	@Override
	public int getOffsetX() {
		return _offsetX;
	}

	@Override
	public int getOffsetY() {
		return _offsetY;
	}

	@Override
	public NodeLocation getLocation() {
		return _nodeLocation;
	}

	@Override
	public void setLayoutValid(boolean valid) {
		_layoutValid = valid;
	}

	@Override
	public boolean isLayoutValid() {
		return _layoutValid;
	}

	@Override
	public LayoutTreeElement getLayoutParent() {
		return _parent;
	}

}
