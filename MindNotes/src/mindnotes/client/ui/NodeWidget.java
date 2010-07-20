package mindnotes.client.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mindnotes.client.presentation.NodeView;
import mindnotes.client.presentation.SelectionState;
import mindnotes.client.ui.text.TextEditor;
import mindnotes.shared.model.NodeLocation;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;

public class NodeWidget extends DeckPanel implements NodeView,
		LayoutTreeElement, TextEditor.Listener {

	// external dependencies
	private Set<Arrow> _arrows;
	private NodeContainer _container;
	private Listener _listener;

	// node contents
	private HTML _label;
	private TextEditor _textEditor;
	private FocusPanel _focusPanel;

	// node tree relatives
	private List<NodeWidget> _children;
	private NodeWidget _parent;

	// layout data
	private int _offsetX, _offsetY;
	private NodeLocation _nodeLocation;
	private Box _branchBounds;
	private boolean _layoutValid;
	private boolean _expanded = true;
	private SelectionState _state;

	public NodeWidget() {

		ClickHandler handler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				_listener.nodeClickedGesture(NodeWidget.this);

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

	public void setTextEditor(TextEditor textEditor) {
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
		if (_textEditor.getParent() != _focusPanel) {
			_focusPanel.add(_textEditor);
			_textEditor.setListener(this);
		}

		showWidget(1); // show text box;
		// setHTML after making rich text editor visible
		// to avoid weird behavior of using the formatter when the widget is
		// not visible
		_textEditor.setHTML(_label.getHTML());

		_focusPanel.setFocus(true);

		// must be called before showToolbar(), so that the toolbar knows where
		// to appear
		if (_container != null) {
			_container.onNodeLayoutInvalidated(this);
		}
		_textEditor.showToolbar();
	}

	private void exitTextEditing() {
		if (getVisibleWidget() == 0)
			return; // we're not in text editing!

		_textEditor.hideToolbar();
		showWidget(0); // show label;

		if (_listener != null) {
			if (!_label.getHTML().equals(_textEditor.getHTML())) {
				_listener.nodeTextEditedGesture(this, _label.getHTML(),
						_textEditor.getHTML());
			}
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

	@Override
	public void onTextEditorExit() {
		if (_listener != null) {
			_listener.nodeEditFinishedGesture(this);
		}

	}

}
