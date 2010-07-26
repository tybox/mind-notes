package mindnotes.client.ui;

import mindnotes.client.presentation.ActionOptions;
import mindnotes.client.presentation.MindMapSelectionView;
import mindnotes.client.presentation.MindMapView;
import mindnotes.client.presentation.NodeView;
import mindnotes.client.ui.text.TinyEditor;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class MindMapWidget extends Composite implements MindMapView,
		NodeContainer, RequiresResize, PopupContainer, ButtonContainer {

	private ActionButtons _actionButtons;

	private Listener _listener;
	private NodeLayout _layout;
	private NodeWidget _rootNode;

	private MindMapSelectionDialog _mindMapSelectionDialog;
	private AbsolutePanel _viewportPanel;
	private ScrollPanel _scrollPanel;
	private ArrowsWidget _arrowsWidget;
	private MindNotesUI _window;

	private boolean _layoutValid;

	public MindMapWidget() {
		Event.addNativePreviewHandler(new NativePreviewHandler() {

			@Override
			public void onPreviewNativeEvent(NativePreviewEvent event) {
				/**
				 * Chrome does not fire a 'keypress' event with ctr+c, ctrl+v,
				 * ctrl+x (it does for ctrl+z) Firefox does not exhibit this
				 * behavior. Oh well.
				 */
				if (event.getTypeInt() == Event.ONKEYDOWN) {

					NativeEvent nativeEvent = event.getNativeEvent();

					// hack for Macs to make cmd button behave like ctrl
					// on
					// other platforms
					boolean meta = Window.Navigator.getPlatform().equals(
							"MacIntel") ? nativeEvent.getMetaKey()
							: nativeEvent.getCtrlKey();

					boolean shortcut = meta || nativeEvent.getShiftKey()
							|| nativeEvent.getAltKey();

					if (_listener != null && shortcut) {
						_listener.keyboardShortcut(nativeEvent.getKeyCode(),
								meta, nativeEvent.getShiftKey(),
								nativeEvent.getAltKey());
					}
				}

			}
		});

		_layout = new NodeLayout();
		_actionButtons = new ActionButtons();

		_actionButtons.setListener(new ActionButtons.Listener() {

			@Override
			public void deleteClicked() {
				_listener.deleteGesture();
			}

			@Override
			public void addRightClicked() {
				_listener.addRightGesture();
			}

			@Override
			public void addLeftClicked() {
				_listener.addLeftGesture();
			}

			@Override
			public void addClicked() {
				_listener.addGesture();
			}

			@Override
			public void expandClicked() {
				_listener.expandGesture();
			}
		});

		_arrowsWidget = new ArrowsWidget(this);
		_arrowsWidget.setPixelSize(1000, 1000);

		_rootNode = new NodeWidget();
		NodeContextMenu contextMenu = new NodeContextMenu();
		contextMenu.setListener(new NodeContextMenu.Listener() {

			@Override
			public void onMenuPaste() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onMenuDelete() {
				_listener.deleteGesture();

			}

			@Override
			public void onMenuCut() {

			}

			@Override
			public void onMenuCopy() {
				// TODO Auto-generated method stub

			}
		});
		_rootNode.setContextMenu(contextMenu);
		_rootNode.setContainer(this);
		TinyEditor textEditor = new TinyEditor();
		_rootNode.setTextEditor(textEditor);

		_viewportPanel = new AbsolutePanel();
		_viewportPanel.add(_arrowsWidget, 0, 0);
		_viewportPanel.add(_rootNode, 500, 500);

		_scrollPanel = new ScrollPanel(_viewportPanel);
		// _scrollPanel.setPixelSize(600, 500);
		_scrollPanel.setScrollPosition(250);
		_scrollPanel.setHorizontalScrollPosition(250);
		_scrollPanel.setAlwaysShowScrollBars(true);
		_viewportPanel.setPixelSize(1000, 1000);
		// _viewportPanel.addStyleName("checkers-bg");
		initWidget(_scrollPanel);

		_actionButtons.setContainer(this);
		_actionButtons.setNodeContainer(this);

	}

	@Override
	public NodeView getRootNodeView() {
		return _rootNode;
	}

	/**
	 * Push a redraw request to the underlying canvas widgets.
	 */
	public void redraw() {
		_arrowsWidget.render();
	}

	@Override
	public int getNodeRelativeTop(NodeWidget node) {
		return node.getBubbleTop() - _viewportPanel.getAbsoluteTop();
	}

	@Override
	public int getNodeRelativeLeft(NodeWidget node) {
		return node.getBubbleLeft() - _viewportPanel.getAbsoluteLeft();
	}

	public AbsolutePanel getViewportPanel() {
		return _viewportPanel;
	}

	@Override
	public void showActions(NodeView view, ActionOptions options) {
		_actionButtons.showNextTo((NodeWidget) view, options);
	}

	@Override
	public void setListener(Listener l) {
		_listener = l;
	}

	@Override
	public void hideActions() {
		_actionButtons.hideButtons();
	}

	@Override
	public void onResize() {
		_layoutValid = false;
		updateLayout();
	}

	/**
	 * Also called by arrowmaker
	 */
	public void onClick() {
		// let the presenter decide what to do
		if (_listener != null)
			_listener.clickGesture();
	}

	@Override
	public void removeNode(NodeWidget node) {
		_viewportPanel.remove(node);
		for (NodeWidget child : node.getNodeChildren()) {
			removeNode(child);
		}
	}

	@Override
	public void addNode(NodeWidget node) {
		_viewportPanel.add(node);
	}

	public void updateLayout() {
		if (_layoutValid)
			return;
		_layout.doLayout(_rootNode);

		// use top level node's suggested width and height as a suggested size
		// for the whole tree.
		Box bounds = _rootNode.getBranchBounds();

		// suggest a new size for the viewport panel
		int paneWidth = _scrollPanel.getElement().getClientWidth();
		int paneHeight = _scrollPanel.getElement().getClientHeight();

		int viewportWidth = Math.max(paneWidth, bounds.w + 100);
		int viewportHeight = Math.max(paneHeight, bounds.h + 100);

		_viewportPanel.setPixelSize(viewportWidth, viewportHeight);
		_arrowsWidget.setCanvasSize(viewportWidth, viewportHeight);

		// reset widget positions
		setBranchPositions(_rootNode, -bounds.x + (viewportWidth - bounds.w)
				/ 2, -bounds.y + (viewportHeight - bounds.h) / 2);

		redraw();

		_actionButtons.updateButtonLayout();
		_layoutValid = true;

	}

	private void setBranchPositions(NodeWidget node, int x, int y) {
		// TODO optimize by not updating every widget
		_viewportPanel.setWidgetPosition(node, x, y);
		for (LayoutTreeElement child : node.getLayoutChildren()) {
			if (child instanceof NodeWidget) {
				setBranchPositions((NodeWidget) child, x + child.getOffsetX(),
						y + child.getOffsetY());
			}
		}
	}

	@Override
	public void onNodeLayoutInvalidated(NodeWidget node) {
		_layoutValid = false;
		updateLayout();
	}

	public NodeWidget getRootNodeWidget() {
		return _rootNode;
	}

	public void saveToCloudClicked() {
		if (_listener != null) {
			_listener.saveToCloudGesture();
		}

	}

	public void loadFromCloudClicked() {
		if (_listener != null) {
			_listener.loadFromCloudGesture();
		}

	}

	@Override
	public MindMapSelectionView getMindMapSelectionView() {
		if (_mindMapSelectionDialog == null) {
			_mindMapSelectionDialog = new MindMapSelectionDialog();
		}
		return _mindMapSelectionDialog;
	}

	@Override
	public String askForDocumentTitle() {
		// TODO make this a neat DialogBox

		return Window.prompt("Enter the title of your mind map:",
				"Untitled Mind Map");

	}

	public void setEditorWindow(MindNotesUI window) {
		_window = window;
	}

	@Override
	public void setUserInfo(String email, String logoutURL) {
		_window.setUserEmail(email);
		_window.setLogoutLink(logoutURL);
		_window.setCloudBarVisible(email != null);
	}

	@Override
	public void showPopup(Widget anchor, int top, int left, Widget popup) {
		int x = anchor.getAbsoluteLeft() - _viewportPanel.getAbsoluteLeft()
				+ left;
		int y = anchor.getAbsoluteTop() - _viewportPanel.getAbsoluteTop() + top;
		_viewportPanel.add(popup, x, y);
		popup.setVisible(true);

	}

	@Override
	public void addButton(Widget widget) {
		_viewportPanel.add(widget);

	}

	@Override
	public void setButtonPosition(Widget button, int x, int y) {
		_viewportPanel.setWidgetPosition(button, x, y);

	}
}
