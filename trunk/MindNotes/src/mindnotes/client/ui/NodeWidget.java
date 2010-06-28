package mindnotes.client.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mindnotes.client.model.NodeLocation;
import mindnotes.client.presentation.NodeView;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

public class NodeWidget extends DeckPanel implements NodeView,
		LayoutTreeElement {

	// external dependencies
	private Set<Arrow> _arrows;
	private NodeContainer _container;
	private Listener _listener;

	// node contents
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
	private boolean _expanded;

	public NodeWidget() {

		ClickHandler handler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				_listener.nodeClicked(NodeWidget.this);

			}
		};

		_arrows = new HashSet<Arrow>();

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
		_label.addClickHandler(handler);

		add(_label);
		add(_textBox);
		showWidget(0);
		_children = new ArrayList<NodeWidget>();

		setStylePrimaryName("mindmap");
		addStyleDependentName("node");

		addDomHandler(handler, ClickEvent.getType());
	}

	public void setContainer(NodeContainer container) {
		_container = container;
	}

	@Override
	public NodeView createChild() {
		NodeWidget child = new NodeWidget();
		child.setContainer(_container);
		child.setLayoutParent(this);
		_arrows.add(new Arrow(this, child));
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
		_arrows.clear();
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
			removeArrowTo(child);
			if (_container != null)
				_container.removeNode(child);
			_children.remove(child);
			if (_container != null)
				_container.onNodeLayoutInvalidated(this);
		}

	}

	private void removeArrowTo(NodeWidget child) {
		// we rely on the behavior that Set.remove works on .equals() not on ==
		// TODO does this upset GC? in javascript?
		_arrows.remove(new Arrow(this, child));
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
		return getElement().getClientWidth() + 4;
	}

	public int getBubbleHeight() {
		return getElement().getClientHeight() + 4;
	}

	@Override
	public void setListener(Listener listener) {
		_listener = listener;

	}

	@Override
	public void setSelected(boolean isSelected) {
		if (isSelected) {
			addStyleDependentName("node-selected");
			int w = _label.getElement().getClientWidth();
			_textBox.setText(_label.getText());
			_textBox.setWidth(w + "px");
			showWidget(1); // show text box;
			_textBox.setFocus(true);
			_textBox.selectAll();
			if (_container != null)
				_container.onNodeLayoutInvalidated(this);

		} else {
			removeStyleDependentName("node-selected");
			showWidget(0); // show label;

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
		return getAbsoluteTop();
	}

	public int getBubbleLeft() {
		return getAbsoluteLeft();
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

	public List<NodeWidget> getNodeChildren() {
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
		return new Box(0, 0, getElement().getClientWidth(), getElement()
				.getClientHeight());
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

	@Override
	public void toggleExpansion() {
		_expanded = !_expanded;
		if (_expanded)
			showAllChildren();
		else
			hideAllChildren();
	}

	public boolean isExpanded() {
		return _expanded;
	}

	public void setExpanded(boolean expanded) {
		_expanded = expanded;
	}

	private void hideAllChildren() {
		for (NodeWidget child : _children) {
			child.setVisible(false);
			removeArrowTo(child);
		}
	}

	private void showAllChildren() {
		// TODO Auto-generated method stub

	}

	public Set<Arrow> getArrows() {
		return _arrows;
	}

}
