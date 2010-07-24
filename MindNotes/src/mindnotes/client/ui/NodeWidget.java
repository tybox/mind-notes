package mindnotes.client.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mindnotes.client.presentation.NodeView;
import mindnotes.client.presentation.SelectionState;
import mindnotes.client.ui.text.TinyEditor;
import mindnotes.shared.model.NodeLocation;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;

public class NodeWidget extends Composite implements NodeView,
		LayoutTreeElement {

	// external dependencies
	private Set<Arrow> _arrows;
	private NodeContainer _container;
	private Listener _listener;

	// node contents
	private HTML _label;
	private TinyEditor _textEditor;

	// node tree relatives
	private List<NodeWidget> _children;
	private NodeWidget _parent;

	// layout data
	private int _offsetX, _offsetY;
	private NodeLocation _nodeLocation;
	private Box _branchBounds;
	private boolean _layoutValid;
	private boolean _expanded = true;

	// node state
	private SelectionState _state;

	public NodeWidget() {

		ClickHandler handler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (!isEditing()) {
					_listener.nodeClickedGesture(NodeWidget.this);
				}

			}
		};

		_arrows = new HashSet<Arrow>();

		_label = new HTML() {
			{
				addDomHandler(new DoubleClickHandler() {

					@Override
					public void onDoubleClick(DoubleClickEvent event) {
						if (_listener != null) {
							_listener.nodeDoubleClickedGesture(NodeWidget.this);
						}
					}
				}, DoubleClickEvent.getType());
			}
		};
		_label.setStylePrimaryName("node");
		_label.addStyleDependentName("text");
		_label.addClickHandler(handler);

		_children = new ArrayList<NodeWidget>();

		SimplePanel panel = new SimplePanel();
		panel.add(_label);
		initWidget(panel);

		setStylePrimaryName("mindmap");
		addStyleDependentName("node");

		addDomHandler(handler, ClickEvent.getType());
	}

	protected boolean isEditing() {
		return _state == SelectionState.TEXT_EDITING;
	}

	public void setTextEditor(TinyEditor textEditor) {
		_textEditor = textEditor;
	}

	public void setContainer(NodeContainer container) {
		_container = container;
	}

	@Override
	public NodeView createChild() {
		NodeWidget child = new NodeWidget();
		child.setContainer(_container);
		child.setTextEditor(_textEditor);
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

	public SelectionState getSelectionState() {
		return _state;
	}

	@Override
	public void setSelectionState(SelectionState state) {
		if (state == _state)
			return;
		_state = state;
		switch (state) {
		case DESELECTED:
			setDeselected();
			break;
		case SELECTED:
			setSelected();
			break;
		case CURRENT:
			setCurrent();
			break;
		case PARENT_SELECTED:
			setParentSelected();
			break;
		case TEXT_EDITING:
			setCurrent();
			enterTextEditing();
			break;
		}

		if (state != SelectionState.TEXT_EDITING) {
			exitTextEditing();
		}

		if (_container != null)
			_container.onNodeLayoutInvalidated(this);

	}

	private void setParentSelected() {
		addStyleDependentName("node-parent-selected");
		removeStyleDependentName("node-selected");
		removeStyleDependentName("node-current");
		for (NodeWidget child : _children) {
			child.setSelectionState(SelectionState.PARENT_SELECTED);
		}
	}

	private void setCurrent() {
		addStyleDependentName("node-current");
		removeStyleDependentName("node-selected");
		removeStyleDependentName("node-parent-selected");
		for (NodeWidget child : _children) {
			child.setSelectionState(SelectionState.PARENT_SELECTED);
		}
	}

	private void setSelected() {
		addStyleDependentName("node-selected");
		removeStyleDependentName("node-current");
		removeStyleDependentName("node-parent-selected");
		for (NodeWidget child : _children) {
			child.setSelectionState(SelectionState.PARENT_SELECTED);
		}
	}

	private void setDeselected() {
		removeStyleDependentName("node-selected");
		removeStyleDependentName("node-current");
		removeStyleDependentName("node-parent-selected");
		for (NodeWidget child : _children) {
			if (child.getSelectionState() == SelectionState.PARENT_SELECTED) {
				child.setSelectionState(SelectionState.DESELECTED);
			}
		}
	}

	private void enterTextEditing() {

		// setHTML after making rich text editor visible
		// to avoid weird behavior of using the formatter when the widget is
		// not visible

		_textEditor.attach(_label.getElement());

		// DeferredCommand.addCommand(new Command() {
		//
		// @Override
		// public void execute() {
		// _textEditor.setFocus(true);
		// }
		// });

		if (_container != null) {
			_container.onNodeLayoutInvalidated(this);
		}

	}

	private void exitTextEditing() {

		_textEditor.detach();

		if (_listener != null) {
			// TODO use isDirty;
			_listener.nodeTextEditedGesture(this, _label.getHTML(),
					_label.getHTML());

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
