package mindnotes.client.ui;

import mindnotes.client.model.Node;
import mindnotes.client.presentation.MindMapView;
import mindnotes.client.presentation.NodeView;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ScrollPanel;

public class MindMapWidget extends Composite implements MindMapView, RequiresResize {
	
	private AbsolutePanel _viewportPanel;
	private ScrollPanel _scrollPanel;
	private NodeWidget _rootNodeView;
	private ArrowsWidget _arrowsWidget;
	private ActionsPanel _actionsPanel;
	private Listener _listener;
	private NodeWidget _actionsPanelView;
	
	public MindMapWidget() {
		
		_actionsPanel = new ActionsPanel(this);
		_actionsPanel.setVisible(false);
		
		_actionsPanel.setListener(new ActionsPanel.Listener() {
			
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
		});
		
		_arrowsWidget = new ArrowsWidget(this);
		_arrowsWidget.setPixelSize(1000, 1000);
				
		_rootNodeView = new NodeWidget();
		_rootNodeView.setArrowMaker(_arrowsWidget);
		
		_viewportPanel= new AbsolutePanel();
		_viewportPanel.add(_arrowsWidget, 0, 0);
		_viewportPanel.add(_rootNodeView, 500, 500);
		_viewportPanel.add(_actionsPanel, 0, 0);
		
		_scrollPanel = new ScrollPanel(_viewportPanel);
		// _scrollPanel.setPixelSize(600, 500);
		_scrollPanel.setScrollPosition(250);
		_scrollPanel.setHorizontalScrollPosition(250);
		_scrollPanel.setAlwaysShowScrollBars(true);
		_viewportPanel.setPixelSize(1000, 1000);
		_viewportPanel.addStyleName("checkers-bg");
		initWidget(_scrollPanel);

	}

	@Override
	public NodeView getRootNodeView() {
		return _rootNodeView;
	}
	
	/**
	 * Push a redraw request to the underlying canvas widgets.
	 */
	public void redraw() {
		_arrowsWidget.render();
	}

	@Override
	public void nodeLayoutChanged() {
		resizeViewport();
		redraw();
		DeferredCommand.addCommand(new Command() {
			
			@Override
			public void execute() {
				if (_actionsPanelView != null) {
					_actionsPanel.showNextTo(_actionsPanelView);
				}
	
			}
		});
		
	}

	public int getNodeRelativeTop(NodeWidget node) {
		return node.getBubbleTop() - _viewportPanel.getAbsoluteTop();
	}
	
	public int getNodeRelativeLeft(NodeWidget node) {
		return node.getBubbleLeft() - _viewportPanel.getAbsoluteLeft();
	}
	
	public AbsolutePanel getViewportPanel() {
		return _viewportPanel;
	}

	@Override
	public void showActionsPanel(NodeView view, Node node) {
		// TODO maybe move this to ActionsPanel
		_actionsPanelView = (NodeWidget)view;
		_actionsPanel.showNextTo((NodeWidget)view);
	}

	@Override
	public void setListener(Listener l) {
		_listener = l;
	}

	@Override
	public void hideActionsPanel() {
		_actionsPanelView = null;
		_actionsPanel.setVisible(false);
	}
	
	/**
	 * Recalculate the size of the viewport and the location of elements to simulate
	 * growing of the viewport when more elements are added.
	 */
	public void resizeViewport() {
		int elementsWidth = _rootNodeView.getOffsetWidth();
		int elementsHeight = _rootNodeView.getOffsetHeight();
		
		/* how big the viewport should be? */ 
		int newWidth = Math.max(_scrollPanel.getElement().getClientWidth(), elementsWidth + 300);
		int newHeight = Math.max(_scrollPanel.getElement().getClientHeight(), elementsHeight + 300);
		
		/* where should we put the elements? (in the middle of the viewport to get growth in all directions)*/
		// but first, remember where we were before
		int prevElementsLeft = _rootNodeView.getAbsoluteLeft() - _viewportPanel.getAbsoluteLeft();
		int prevElementsTop = _rootNodeView.getAbsoluteTop() - _viewportPanel.getAbsoluteTop();
		
		// okay, back to finding out the new elements location!
		int newElementsLeft = (newWidth - elementsWidth) / 2;
		int newElementsTop = (newHeight - elementsHeight) / 2;
		// that was easy... newElementsLeft, newElementsTop, elementsWidth and 
		// elementsHeight define a box that should be in the center of the viewport.
		
		// let's change stuff, then!
		_viewportPanel.setPixelSize(newWidth, newHeight);
		_arrowsWidget.setCanvasSize(newWidth, newHeight);
		_viewportPanel.setWidgetPosition(_rootNodeView, newElementsLeft, newElementsTop);
		
		// okay, what about prevElementsLeft and prevElementsTop?
		// we need them for this:
		_scrollPanel.setScrollPosition(_scrollPanel.getScrollPosition() + (newElementsTop - prevElementsTop));
		_scrollPanel.setHorizontalScrollPosition(_scrollPanel.getHorizontalScrollPosition() + (newElementsLeft - prevElementsLeft));
		
		// hopefully, we're done!
		
		// note: if you're writing alone and you prefer pair programming,
		// speak to yourself in the code :)
	}

	@Override
	public void onResize() {
		
		// XXX: potential caveat: resizeViewport destroys scroll position? (got to check)
		nodeLayoutChanged();
	}

	/**
	 * Also called by arrowmaker
	 */
	public void onClick() {
		// let the presenter decide what to do
		if(_listener != null) _listener.clickGesture();
	}
}
