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
		renderArrowsForNodeWidget(_mindMapWidget.getRootNodeWidget());
	}

	public void renderArrowsForNodeWidget(NodeWidget node) {
		double ox, oy, fx, fy, tx, ty, sinAlpha, cosAlpha, h1x, h1y, h2x, h2y;
		ox = _canvas.getAbsoluteLeft();
		oy = _canvas.getAbsoluteTop();
		for (Arrow arrow : node.getArrows()) {

			// ignore arrows to/from hidden nodes
			if (!(arrow.from.isVisible() && arrow.to.isVisible()))
				continue;
			// find coords of top-left corners of two nodes
			fx = arrow.from.getBubbleLeft() - ox;
			fy = arrow.from.getBubbleTop() - oy;
			tx = arrow.to.getBubbleLeft() - ox;
			ty = arrow.to.getBubbleTop() - oy;

			// shift the coords to point at the nearest two corners
			if (fx < tx) {
				fx += arrow.from.getBubbleWidth();
			} else {
				tx += arrow.to.getBubbleWidth();
			}

			// shift from corners to sides
			// CAVEAT: current impl works for horizontal layout only
			ty += arrow.to.getBubbleHeight() / 2;
			fy += arrow.from.getBubbleHeight() / 2;

			// sine of arrow line slope
			sinAlpha = (ty - fy) / Math.hypot((tx - fx), (ty - fy));
			cosAlpha = (tx - fx) / Math.hypot((tx - fx), (ty - fy));

			// cut down several px from each side of the arrow
			fy += 6 * sinAlpha / cosAlpha * Math.signum(cosAlpha);
			fx += 6 * Math.signum(cosAlpha);
			ty -= 6 * sinAlpha / cosAlpha * Math.signum(cosAlpha);
			tx -= 6 * Math.signum(cosAlpha);

			// draw the arrow
			_canvas.setStrokeStyle("#111");
			_canvas.beginPath();
			_canvas.moveTo(fx, fy);
			_canvas.lineTo(tx, ty);
			_canvas.stroke();

			// draw arrow head
			h1x = tx - 15 * cosAlpha + 5 * sinAlpha;
			h1y = ty - 15 * sinAlpha - 5 * cosAlpha;
			h2x = tx - 15 * cosAlpha - 5 * sinAlpha;
			h2y = ty - 15 * sinAlpha + 5 * cosAlpha;

			_canvas.setFillStyle("#111");
			_canvas.beginPath();
			_canvas.moveTo(tx, ty);
			_canvas.lineTo(h1x, h1y);
			_canvas.lineTo(h2x, h2y);
			_canvas.closePath();
			_canvas.stroke();
			_canvas.fill();

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
