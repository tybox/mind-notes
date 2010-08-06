package mindnotes.client.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mindnotes.client.presentation.EmbeddedObjectView;
import mindnotes.client.presentation.NodeView;
import mindnotes.client.presentation.SelectionState;
import mindnotes.client.ui.embedded.EmbeddedObjectContainer;
import mindnotes.client.ui.text.TinyEditor;
import mindnotes.shared.model.NodeLocation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class NodeWidget extends Composite implements NodeView,
		LayoutTreeElement {

	interface Resources extends ClientBundle {
		@Source("NodeWidget.css")
		NodeStyle style();
	}

	interface NodeStyle extends CssResource {
		String node();

		String nodeText();

		String nodeSelected();

		String nodeParentSelected();

		String nodeCurrent();

		String nodeFocused();
	}

	// ClientBundle resources
	Resources _resources = GWT.create(Resources.class);

	// external dependencies (to be DI'd)
	private Set<Arrow> _arrows;
	private NodeContainer _container;
	private Listener _listener;
	private TinyEditor _textEditor;
	private NodeContextMenu _contextMenu;

	// node contents
	private HTML _content;

	// node tree relatives
	private TemporaryInsertList<LayoutTreeElement> _layoutChildren;

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
	private Box _elementBounds;

	private FlowPanel _objectContainer;

	public NodeWidget() {

		_resources.style().ensureInjected();

		MouseDownHandler mouseDownHandler = new MouseDownHandler() {

			@Override
			public void onMouseDown(MouseDownEvent event) {
				if (!isEditing()) {
					_listener.nodeMouseDownGesture(NodeWidget.this);
				}
			}
		};

		final DoubleClickHandler doubleClickHandler = new DoubleClickHandler() {

			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				if (_listener != null) {
					_listener.nodeDoubleClickedGesture(NodeWidget.this);
				}
			}
		};
		final ContextMenuHandler contextMenuHandler = new ContextMenuHandler() {

			@Override
			public void onContextMenu(ContextMenuEvent event) {
				if (isEditing())
					return;
				event.stopPropagation();
				event.preventDefault();
				_listener.nodeClickedGesture(NodeWidget.this);

				if (_contextMenu != null) {
					_contextMenu.showContextMenu(event.getNativeEvent()
							.getClientX(), event.getNativeEvent().getClientY());
				}
			}
		};

		_arrows = new HashSet<Arrow>();

		_content = new HTML() {
			{
				addDomHandler(doubleClickHandler, DoubleClickEvent.getType());
				addDomHandler(contextMenuHandler, ContextMenuEvent.getType());
			}
		};
		_content.setStyleName(_resources.style().nodeText());
		_content.addMouseDownHandler(mouseDownHandler);

		_children = new ArrayList<NodeWidget>();
		_layoutChildren = new TemporaryInsertList<LayoutTreeElement>(_children);

		_objectContainer = new FlowPanel();
		_objectContainer.add(_content);

		initWidget(_objectContainer);

		setStyleName(_resources.style().node());

		addDomHandler(mouseDownHandler, MouseDownEvent.getType());
		addDomHandler(doubleClickHandler, DoubleClickEvent.getType());
		addDomHandler(contextMenuHandler, ContextMenuEvent.getType());
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
		return createChildAtIndex(_children.size());
	}

	@Override
	public NodeView createChildBefore(NodeView view) {
		int index = _children.indexOf(view);
		return createChildAtIndex(index >= 0 ? index : _children.size());
	}

	@Override
	public NodeView createChildAfter(NodeView view) {
		int index = _children.indexOf(view);
		return createChildAtIndex(index >= 0 ? index + 1 : _children.size());
	}

	protected void addChildAtIndex(NodeWidget child, int index) {
		child.setLayoutParent(this);
		child.setContainer(_container);
		child.setTextEditor(_textEditor);
		child.setContextMenu(_contextMenu);

		_arrows.add(new Arrow(this, child));

		if (index > _children.size())
			index = _children.size();
		_children.add(index, child);
		if (_container != null)
			_container.addNode(child);
		setLayoutValid(false);
	}

	private NodeWidget createChildAtIndex(int index) {
		NodeWidget child = new NodeWidget();
		addChildAtIndex(child, index);
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
		setLayoutValid(false);
	}

	@Override
	public void removeChild(NodeView view) {
		if (view instanceof NodeWidget) {
			NodeWidget child = (NodeWidget) view;
			removeArrowTo(child);
			if (_container != null)
				_container.removeNode(child);
			_children.remove(child);
			setLayoutValid(false);
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
	}

	@Override
	public void setText(String text) {
		_content.setHTML(text);
		setLayoutValid(false);
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
		setStyleName(_resources.style().node());
		switch (state) {
		case DESELECTED:
			break;
		case SELECTED:
			addStyleName(_resources.style().nodeSelected());
			break;
		case CURRENT:
			addStyleName(_resources.style().nodeCurrent());
			break;
		case PARENT_SELECTED:
			addStyleName(_resources.style().nodeParentSelected());
			break;
		case TEXT_EDITING:
			addStyleName(_resources.style().nodeCurrent());
			enterTextEditing();
			break;
		}
		if (state == SelectionState.DESELECTED) {
			for (NodeWidget child : _children) {
				if (child.getSelectionState() == SelectionState.PARENT_SELECTED) {
					child.setSelectionState(SelectionState.DESELECTED);
				}
			}
		} else {
			for (NodeWidget child : _children) {
				child.setSelectionState(SelectionState.PARENT_SELECTED);
			}
		}

		if (state != SelectionState.TEXT_EDITING) {
			exitTextEditing();
		}

	}

	private void enterTextEditing() {

		// setHTML after making rich text editor visible
		// to avoid weird behavior of using the formatter when the widget is
		// not visible
		_textEditor.setLayoutHost(this);
		_textEditor.attach(_content.getElement());

		setLayoutValid(false);

	}

	private void exitTextEditing() {

		_textEditor.detach();
		setLayoutValid(false);
		if (_listener != null) {
			// TODO use isDirty;
			_listener.nodeTextEditedGesture(this, _content.getHTML(),
					_content.getHTML());
		}
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
		return _layoutChildren;
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
		return _elementBounds == null ? getNativeElementBounds()
				: _elementBounds;
	}

	public Box getNativeElementBounds() {
		_elementBounds = new Box(0, 0, getElement().getClientWidth(),
				getElement().getClientHeight() + 1);
		return _elementBounds;

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

		if (valid == false) {
			_elementBounds = null;
			LayoutTreeElement layoutParent = getLayoutParent();

			if (layoutParent != null) {
				layoutParent.setLayoutValid(false);
			} else if (_container != null) { // if this node has no layout
												// parent,
												// notify container
				_container.onNodeLayoutInvalidated(this);
			}

		}
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
		setLayoutValid(false);
	}

	private void setBranchVisible(boolean visible) {
		setVisible(visible);
		for (NodeWidget child : _children) {
			child.setBranchVisible(visible);
		}
		setLayoutValid(false);
	}

	public Set<Arrow> getArrows() {
		return _arrows;
	}

	public void setContextMenu(NodeContextMenu contextMenu) {
		_contextMenu = contextMenu;
	}

	public NodeContextMenu getContextMenu() {
		return _contextMenu;
	}

	@Override
	public EmbeddedObjectView createEmbeddedObject(String type, String data) {
		EmbeddedObjectContainer eoc = new EmbeddedObjectContainer(type, data);
		eoc.setLayoutHost(this);
		_objectContainer.add(eoc);
		setLayoutValid(false);
		return eoc;
	}

	@Override
	public void removeEmbeddedObject(EmbeddedObjectView view) {
		_objectContainer.remove((Widget) view);
		setLayoutValid(false);
	}

	public Widget getContentWidget() {
		return _content;
	}

	public int indexOfChild(NodeWidget widget) {
		return _children.indexOf(widget);
	}

	public void addTemporaryLayoutChild(int index, LayoutTreeElement child) {
		_layoutChildren.setTemporaryInsert(index, child);
		setLayoutValid(false);
	}

	public void removeTemporaryLayoutChild() {
		_layoutChildren.clearInsert();
		setLayoutValid(false);
	}

	public void onBranchDragged(int index, NodeLocation location) {
		if (_listener != null) {
			_listener.onBranchDragged(index, location);
		}
	}

	public void onBranchDropped() {
		if (_listener != null) {
			_listener.onBranchDropped();
		}
	}

	@Override
	public void moveChild(NodeView childView, NodeView newParentView,
			int index, NodeLocation location) {
		NodeWidget newParentWidget = (NodeWidget) newParentView;

		NodeWidget childWidget = (NodeWidget) childView;
		removeChild(childWidget);
		childWidget.setLocation(location, true);
		newParentWidget.addChildAtIndex(childWidget, index);

	}

	private void setLocation(NodeLocation location, boolean propagate) {
		setLocation(location);
		if (propagate)
			for (NodeWidget child : _children) {
				child.setLocation(location, true);
			}
	}
}
