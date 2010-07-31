package mindnotes.client.ui;

import gwt.canvas.client.Canvas;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * This widget renders all arrows and links that illustrate relations between
 * mindmap nodes.
 * 
 * @author dominik
 * 
 */
@SuppressWarnings("deprecation")
// ClickListener is deprecated but I use a 3rd party
// canvas widget written before event model overhaul.
// what you gonna do...
public class ArrowsWidget extends Composite {

	private Canvas _canvas;
	private MindMapWidget _mindMapWidget;
	private ArrowRenderer _renderer;
	private int _ox;
	private int _oy;

	public ArrowsWidget(MindMapWidget mindMapWidget) {

		_mindMapWidget = mindMapWidget;
		_canvas = new Canvas();
		_canvas.setBackgroundColor(Canvas.TRANSPARENT);
		_canvas.setWidth(1000);
		_canvas.setHeight(1000);
		_canvas.addClickListener(new ClickListener() {

			@Override
			public void onClick(Widget sender) {

				sendOnClick();
			}
		});

		_renderer = new ClassicArrowRenderer();
		_renderer.setCanvas(_canvas);

		initWidget(_canvas);
	}

	protected void sendOnClick() {
		if (_mindMapWidget != null)
			_mindMapWidget.onClick();
	}

	public void render() {
		if (!isAttached())
			return;

		_canvas.clear();
		_ox = _canvas.getAbsoluteLeft();
		_oy = _canvas.getAbsoluteTop();
		renderArrowsForNodeWidget(_mindMapWidget.getRootNodeWidget());
	}

	public void renderArrowsForNodeWidget(NodeWidget node) {

		for (Arrow arrow : node.getArrows()) {
			// TODO optimize arrow rendering by caching coordinates instead of
			// querying DOM each time

			_renderer.renderArrow(_ox, _oy, arrow);
		}

		// do the same recursively for children
		for (NodeWidget child : node.getNodeChildren()) {
			renderArrowsForNodeWidget(child);
		}

	}

	public void setCanvasSize(int newWidth, int newHeight) {
		this.setPixelSize(newWidth, newHeight);
		_canvas.setWidth(newWidth);
		_canvas.setHeight(newHeight);
	}

}
