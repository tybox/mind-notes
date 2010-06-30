package mindnotes.client.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mindnotes.client.model.NodeLocation;
import mindnotes.client.presentation.NodeView;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;

public class NodeWidget extends DeckPanel implements NodeView,
		LayoutTreeElement {

	// external dependencies
	private Set<Arrow> _arrows;
	private NodeContainer _container;
	private Listener _listener;

	// node contents
	private HTML _label;
	private TextEditor _textEditor;

	// node tree relatives
	private List<NodeWidget> _children;
	private NodeWidget _parent;

	// layout data
	private int _offsetX, _offsetY;
	private NodeLocation _nodeLocation;
	private Box _branchBounds;
	private boolean _layoutValid;
	private boolean _expanded = true;
	private boolean _isSelected;
	private FocusPanel _focusPanel;

	public NodeWidget() {

		ClickHandler handler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				_listener.nodeClicked(NodeWidget.this);

			}
		};

		_arrows = new HashSet<Arrow>();

		_textEditor = new TextEditor();

		_label = new HTML();
		_label.setStylePrimaryName("node");
		_label.addStyleDependentName("text");
		_label.addClickHandler(handler);

		add(_label);
		_focusPanel = new FocusPanel(_textEditor);
		_focusPanel.addFocusHandler(new FocusHandler() {

			@Override
			public void onFocus(FocusEvent event) {
				addStyleDependentName("node-focused");
			}
		});
		_focusPanel.addBlurHandler(new BlurHandler() {

			@Override
			public void onBlur(BlurEvent event) {
				// woo-hoo (when I feel heavy metal)
				removeStyleDependentName("node-focused");

			}
		});
		add(_focusPanel);
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
		_label.setHTML(text);
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
	public void setSelected(final boolean isSelected) {
		if (isSelected == _isSelected)
			return;
		_isSelected = isSelected;
		if (isSelected) {
			addStyleDependentName("node-selected");

			showWidget(1); // show text box;
			// setHTML after making rich text editor visible
			// to avoid weird behavior of using the formatter when the widget is
			// not visible
			_textEditor.setHTML(_label.getHTML());

			_focusPanel.setFocus(true);
			if (_container != null)
				_container.onNodeLayoutInvalidated(this);

		} else {
			removeStyleDependentName("node-selected");
			showWidget(0); // show label;

			if (_listener != null) {
				if (!_label.getHTML().equals(_textEditor.getHTML())) {
					_listener.nodeTextEdited(this, _label.getHTML(),
							_textEditor.getHTML());
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

	public boolean isExpanded() {
		return _expanded;
	}

	public void setExpanded(boolean expanded) {
		_expanded = expanded;
		for (NodeWidget child : _children) {
			child.setBranchVisible(_expanded);
		}
		if (_container != null)
			_container.onNodeLayoutInvalidated(this);
	}

	private void setBranchVisible(boolean visible) {
		setVisible(visible);
		for (NodeWidget child : _children) {
			child.setBranchVisible(visible);
		}
	}

	public Set<Arrow> getArrows() {
		return _arrows;
	}

}
