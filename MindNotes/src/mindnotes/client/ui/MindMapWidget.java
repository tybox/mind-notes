package mindnotes.client.ui;

import static com.google.gwt.dom.client.Style.Unit.PX;
import mindnotes.client.presentation.ActionOptions;
import mindnotes.client.presentation.CloudActionsView;
import mindnotes.client.presentation.MindMapSelectionView;
import mindnotes.client.presentation.MindMapView;
import mindnotes.client.presentation.NodeView;
import mindnotes.client.presentation.ShareOptionsView;
import mindnotes.client.ui.embedded.widgets.EmbeddedObjectWidgetFactory;
import mindnotes.client.ui.resize.ResizeController;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class MindMapWidget extends Composite implements MindMapView,
		NodeContainer, RequiresResize, PopupContainer, ButtonContainer {

	private ActionButtons _actionButtons;

	private Listener _listener;
	private NodeWidget _rootNode;

	private MindMapSelectionDialog _mindMapSelectionDialog;
	private AbsolutePanel _viewportPanel;
	private ScrollPanel _scrollPanel;
	private CanvasRenderWidget _arrowsWidget;
	private MindNotesUI _window;

	private boolean _layoutValid;

	private MessageBar _messageBar;

	private boolean _holdLayoutUpdates;

	private PickupDragController _dragController;
	private MindMapDropController _dropController;

	private int _ox;
	private int _oy;

	private boolean _holdCentering;
	private InsertMenu _insertMenu;

	private boolean _viewer;

	private ShareOptionsDialog _shareOptionsDialog;

	public MindMapWidget(boolean viewer) {
		_viewer = viewer;

		initArrows();
		initViewport();
		initRootNode(viewer);

		if (!_viewer) {
			initKeyboardShortcuts();
			initActionButtons();
			initContextMenu();
			initDragDrop();

		}
		addNode(_rootNode);

		initWidget(_scrollPanel);

		if (!_viewer) {
			initMindMapSelectionDialog();
		}

		initWindowClosingHandler();
	}

	private void initWindowClosingHandler() {
		Window.addWindowClosingHandler(new ClosingHandler() {

			@Override
			public void onWindowClosing(ClosingEvent event) {
				if (_listener != null && _listener.windowClosing()) {
					event.setMessage("There are unsaved changes in your mind map. Are you sure you want to close this window?");
				} else {
					event.setMessage(null);
				}
			}
		});

	}

	/**
	 * @param viewOnly
	 * 
	 */
	private void initRootNode(boolean viewOnly) {
		_rootNode = new NodeWidget();
		_rootNode.setEmbeddedObjectFactory(new EmbeddedObjectWidgetFactory(
				new ResizeController(_viewportPanel), viewOnly));
		_rootNode.setContainer(this);

	}

	/**
	 * 
	 */
	private void initArrows() {
		_arrowsWidget = new CanvasRenderWidget(this);
		_arrowsWidget.setPixelSize(1000, 1000);
	}

	/**
	 * 
	 */
	private void initViewport() {
		_viewportPanel = new AbsolutePanel();
		_viewportPanel.add(_arrowsWidget, 0, 0);

		_scrollPanel = new ScrollPanel(_viewportPanel);
		// _scrollPanel.setPixelSize(600, 500);
		_scrollPanel.setScrollPosition(250);
		_scrollPanel.setHorizontalScrollPosition(250);
		_scrollPanel.setAlwaysShowScrollBars(true);
		_viewportPanel.setPixelSize(1000, 1000);
		// _viewportPanel.addStyleName("checkers-bg");

		// fix scroll panel positioning to let the scroll panel take up the
		// whole parent
		Style style = _scrollPanel.getElement().getStyle();
		style.setPosition(Position.ABSOLUTE);
		style.setLeft(0, PX);
		style.setTop(0, PX);
		style.setRight(0, PX);
		style.setBottom(0, PX);
	}

	/**
	 * 
	 */
	private void initMindMapSelectionDialog() {
		_mindMapSelectionDialog = new MindMapSelectionDialog();
		_mindMapSelectionDialog.setPositionCallback(new PositionCallback() {

			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				if (_window == null)
					return;
				int maxLeft = Window.getClientWidth() - offsetWidth - 16;
				int left = _window.cloudBarPanel.loadButton.getAbsoluteLeft();
				int top = _window.cloudBarPanel.getAbsoluteTop()
						+ _window.cloudBarPanel.getOffsetHeight();
				if (left > maxLeft)
					left = maxLeft;
				_mindMapSelectionDialog.setPopupPosition(left, top);
			}
		});
	}

	/**
	 * 
	 */
	private void initDragDrop() {
		_dragController = new PickupDragController(_viewportPanel, false);
		_dragController.setBehaviorDragStartSensitivity(40);

		_dropController = new MindMapDropController(this);
		_dragController.registerDropController(_dropController);

		_dragController.addDragHandler(new DragHandler() {

			private NodeWidget _dragged;
			private int _index;
			private NodeWidget _parentNodeWidget;

			@Override
			public void onPreviewDragStart(DragStartEvent event)
					throws VetoDragException {

			}

			@Override
			public void onPreviewDragEnd(DragEndEvent event)
					throws VetoDragException {

			}

			@Override
			public void onDragStart(DragStartEvent event) {
				hideActions();
				holdCentering();
				_dragged = (NodeWidget) event.getContext().draggable;
				_parentNodeWidget = _dragged.getParentNodeWidget();
				_index = _parentNodeWidget.indexOfChild(_dragged);
				_dragged.getParentNodeWidget().removeChild(_dragged);
				_viewportPanel.add(_dragged, 0, 0);
			}

			@Override
			public void onDragEnd(DragEndEvent event) {
				if (event.getContext().finalDropController == null) {
					_viewportPanel.remove(_dragged);
					_parentNodeWidget.addChildAtIndex(_dragged, _index);

				}
				resumeCentering();
				showActions();

			}
		});
	}

	/**
	 * 
	 */
	private void initContextMenu() {
		NodeContextMenu contextMenu = new NodeContextMenu();
		contextMenu.setListener(new NodeContextMenu.Listener() {

			@Override
			public void onMenuPaste() {
				if (_listener != null)
					_listener.pasteGesture();

			}

			@Override
			public void onMenuDelete() {
				if (_listener != null)
					_listener.deleteGesture();

			}

			@Override
			public void onMenuCut() {
				if (_listener != null)
					_listener.cutGesture();
			}

			@Override
			public void onMenuCopy() {
				if (_listener != null)
					_listener.copyGesture();

			}
		});
		_rootNode.setContextMenu(contextMenu);
	}

	/**
	 * 
	 */
	private void initKeyboardShortcuts() {
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

					if (_listener != null) {
						_listener.keyboardShortcut(nativeEvent.getKeyCode(),
								meta, nativeEvent.getShiftKey(),
								nativeEvent.getAltKey());
					}
				}

			}
		});
	}

	/**
	 * 
	 */
	private void initActionButtons() {
		_actionButtons = new ActionButtons();

		_actionButtons.setListener(new ActionButtons.Listener() {

			@Override
			public void deleteClicked() {
				if (_listener != null)
					_listener.deleteGesture();
			}

			@Override
			public void addRightClicked() {
				if (_listener != null)
					_listener.addRightGesture();
			}

			@Override
			public void addLeftClicked() {
				if (_listener != null)
					_listener.addLeftGesture();
			}

			@Override
			public void expandClicked() {
				if (_listener != null)
					_listener.expandGesture();
			}

			@Override
			public void addUpClicked() {
				if (_listener != null)
					_listener.addUpGesture();
			}

			@Override
			public void addDownClicked() {
				if (_listener != null)
					_listener.addDownGesture();
			}

			@Override
			public void insertMenuFired(int x, int y) {
				if (_listener != null)
					_listener.insertMenuGesture(x, y);
			}
		});
		_actionButtons.setContainer(this);
		_actionButtons.setNodeContainer(this);
	}

	@Override
	public void showInsertMenu(int x, int y, String text) {
		if (_insertMenu == null) {
			_insertMenu = new InsertMenu();
			_insertMenu.setListener(new InsertMenu.Listener() {

				@Override
				public void imageChosenGesture(String url) {
					if (_listener != null) {
						_listener.imageInsertGesture(url);
						_insertMenu.hide();
					}
				}

				@Override
				public void mapInsertGesture() {
					if (_listener != null) {
						_listener.mapInsertGesture();
						_insertMenu.hide();
					}
				}

				/*@Override
				public void onResize(int offsetWidth, int offsetHeight) {
					int x = _insertMenu.getAbsoluteLeft();
					int y = _insertMenu.getAbsoluteTop();
					int w = _insertMenu.getOffsetWidth();
					int h = _insertMenu.getOffsetHeight();
					if (x + w > Window.getClientWidth()) {
						x = Window.getClientWidth() - w - 30;
					}
					if (y + h > Window.getClientHeight()) {
						y = Window.getClientHeight() - h - 30;
					}
					_insertMenu.setPopupPosition(x, y);
				}*/

				@Override
				public void textInsertGesture() {
					if (_listener != null) {
						_listener.textInsertGesture();
						_insertMenu.hide();
					}
				}
			});
		}
		_insertMenu.performSearches(stripHTML(text));
		// showPopup(null, x, y, _searchMenu);
		_insertMenu.showAt(x, y);

	}

	private native String stripHTML(String text)/*-{
		return text.replace(/<\/?[^>]*>/g, '');
	}-*/;

	@Override
	public NodeView getRootNodeView() {
		return _rootNode;
	}

	@Override
	public int getNodeRelativeTop(NodeWidget node) {
		// TODO maybe those methods could use cached values instead of DOM calls
		return node.getAbsoluteTop() - _viewportPanel.getAbsoluteTop();
	}

	@Override
	public int getNodeRelativeLeft(NodeWidget node) {
		return node.getAbsoluteLeft() - _viewportPanel.getAbsoluteLeft();
	}

	public AbsolutePanel getViewportPanel() {
		return _viewportPanel;
	}

	@Override
	public void showActions(NodeView view, ActionOptions options) {
		_actionButtons.showNextTo((NodeWidget) view, options);
	}

	public void showActions() {
		_actionButtons.show();
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

	public void setTitle(String title) {
		if (_window != null) {
			_window.setMindMapTitle(title);
		}
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

		_viewportPanel.add(node, 0, 0);
		for (NodeWidget child : node.getNodeChildren()) {
			addNode(child);
		}
		if (node.getParentNodeWidget() != null && _dragController != null) {
			_dragController.makeDraggable(node, node.getContentWidget());
		}
	}

	public void updateLayout() {
		if (_layoutValid)
			return;
		NodeLayout.doLayout(_rootNode);
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

		if (!_holdCentering) {
			_ox = -bounds.x + (viewportWidth - bounds.w) / 2;
			_oy = -bounds.y + (viewportHeight - bounds.h) / 2;
		}
		setBranchPositions(_rootNode, _ox, _oy);

		_arrowsWidget.render(_ox, _oy);

		if (_actionButtons != null)
			_actionButtons.updateButtonLayout();
		_layoutValid = true;

	}

	private void holdCentering() {
		_holdCentering = true;
	}

	private void resumeCentering() {
		_holdCentering = false;
	}

	public int getLayoutOffsetX() {
		return _ox;
	}

	public int getLayoutOffsetY() {
		return _oy;
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
		if (!_holdLayoutUpdates) {
			updateLayout();
		}
	}

	public NodeWidget getRootNodeWidget() {
		return _rootNode;
	}

	@Override
	public MindMapSelectionView getMindMapSelectionView() {

		return _mindMapSelectionDialog;
	}

	@Override
	public String askForDocumentTitle() {
		return askForDocumentTitle("Untitled map");

	}

	public String askForDocumentTitle(String oldTitle) {
		// TODO make this a neat DialogBox
		final String title = Window
				.prompt("Enter the title of your mind map:\n(A proper dialog is on the way)",
						oldTitle);

		DeferredCommand.addCommand(new Command() {

			@Override
			public void execute() {
				if (_listener != null) {
					_listener.titleChanged(title);
				}

			}
		});

		return title;
	}

	public void setEditorWindow(MindNotesUI window) {
		_window = window;
		_window.cloudBarPanel.setListener(new CloudActionsView.Listener() {

			@Override
			public void newClicked() {
				if (_listener != null) {
					_listener.newMapGesture();
				}
			}

			@Override
			public void loadFromCloudClicked() {
				if (_listener != null) {
					_listener.loadFromCloudGesture();
				}
			}

			@Override
			public void saveToCloudClicked() {
				if (_listener != null) {
					_listener.saveToCloudGesture();
				}
			}

			@Override
			public void saveLocalClicked() {
				if (_listener != null) {
					_listener.saveLocalGesture();
				}
			}

			@Override
			public void shareClicked() {
				if (_listener != null) {
					_listener.shareClickGesture();
				}
			}

			@Override
			public void tryAgainClicked() {
				if (_listener != null) {
					_listener.reconnectGesture();
				}
			}

		});
	}

	@Override
	public void showPopup(Widget anchor, int left, int top, Widget popup) {
		int anchorOffsetX = anchor == null ? 0 : anchor.getAbsoluteLeft();
		int anchorOffsetY = anchor == null ? 0 : anchor.getAbsoluteTop();
		int x = anchorOffsetX - _viewportPanel.getAbsoluteLeft() + left;
		int y = anchorOffsetY - _viewportPanel.getAbsoluteTop() + top;
		_viewportPanel.add(popup, x, y);
		int dw = x + popup.getOffsetWidth();
		int dh = y + popup.getOffsetHeight();
		if (dw > _viewportPanel.getOffsetWidth()
				|| dh > _viewportPanel.getOffsetHeight()) {
			_viewportPanel.setPixelSize(dw, dh);
		}
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

	@Override
	public void showMessage(String string) {
		if (_messageBar == null) {
			_messageBar = new MessageBar();
		}
		_messageBar.showMessage(string);
	}

	@Override
	public void holdLayout() {
		_holdLayoutUpdates = true;
	}

	@Override
	public void resumeLayout() {
		_holdLayoutUpdates = false;
		if (!_layoutValid) {
			updateLayout();
		}
	}

	public GhostNode getGhostNode() {
		if (_dropController != null)
			return _dropController.getGhostNode();
		else
			return null;

	}

	@Override
	public int getRelativeTop(Widget button) {
		return button.getAbsoluteTop() - _viewportPanel.getAbsoluteTop();
	}

	@Override
	public int getRelativeLeft(Widget button) {
		return button.getAbsoluteLeft() - _viewportPanel.getAbsoluteLeft();
	}

	@Override
	public ShareOptionsView showShareDialog() {
		if (_shareOptionsDialog == null) {
			_shareOptionsDialog = new ShareOptionsDialog();
		}
		DeferredCommand.addCommand(new Command() {

			@Override
			public void execute() {
				_shareOptionsDialog
						.setPopupPositionAndShow(new PositionCallback() {

							@Override
							public void setPosition(int offsetWidth,
									int offsetHeight) {
								int x = _window.cloudBarPanel.shareButton
										.getAbsoluteLeft();
								int y = _window.cloudBarPanel.getAbsoluteTop()
										+ _window.cloudBarPanel
												.getOffsetHeight();
								if (x + offsetWidth > Window.getClientWidth()) {
									x -= offsetWidth
											- _window.cloudBarPanel.shareButton
													.getOffsetWidth();
								}
								_shareOptionsDialog.setPopupPosition(x, y);
							}
						});
			}
		});

		return _shareOptionsDialog;
	}

	@Override
	public CloudActionsView getCloudActionsView() {
		return _window.cloudBarPanel;
	}
}
