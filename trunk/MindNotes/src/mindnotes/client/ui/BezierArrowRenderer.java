package mindnotes.client.ui;

import gwt.canvas.client.Canvas;

public class BezierArrowRenderer implements ArrowRenderer {

	private final static int CURVENESS = 20;

	private Canvas _canvas;

	@Override
	public void setCanvas(Canvas canvas) {
		_canvas = canvas;

	}

	@Override
	public void renderArrow(int ox, int oy, Arrow arrow) {
		// ignore arrows to/from hidden nodes
		if (!(arrow.from.isVisible() && arrow.to.isVisible()))
			return;

		// get all coords we need
		Box f = arrow.from.getElementBounds();
		Box t = arrow.to.getElementBounds();
		int tox = arrow.to.getOffsetX() + ox;
		int toy = arrow.to.getOffsetY() + oy;

		// decide what kind of line do we have to draw

		// all lines are drawn from the bottom edge of the node,
		// either from bottom left corner or bottom right corner.

		// CAVEAT: current implementation poorly handles nodes that
		// overlap themselves on the x axis.

		int x1, y1, x2, y2;
		if (ox + f.x + f.w < tox + t.x) { // from___to
			x1 = f.x + f.w + ox;
			x2 = t.x + tox;
			y1 = f.y + f.h + oy;
			y2 = t.y + t.h + toy;
		} else { // to___from
			x1 = t.x + t.w + tox;
			x2 = f.x + ox;
			y1 = t.y + t.h + toy;
			y2 = f.y + f.h + oy;
		}

		_canvas.setStrokeStyle("#111");
		_canvas.beginPath();
		_canvas.moveTo(x1, y1);
		_canvas.cubicCurveTo(x1 + CURVENESS, y1, x2 - CURVENESS, y2, x2, y2);
		_canvas.stroke();
	}

}
