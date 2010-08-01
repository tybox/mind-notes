package mindnotes.client.ui;

import gwt.canvas.client.Canvas;

public class ClassicArrowRenderer implements ArrowRenderer {

	private Canvas _canvas;

	@Override
	public void setCanvas(Canvas canvas) {
		_canvas = canvas;
	}

	@Override
	public void renderArrow(int ox, int oy, Arrow arrow) {
		double fx, fy, tx, ty, sinAlpha, cosAlpha, h1x, h1y, h2x, h2y;

		// ignore arrows to/from hidden nodes
		if (!(arrow.from.isVisible() && arrow.to.isVisible()))
			return;
		// find coords of top-left corners of two nodes

		Box fromBox = arrow.from.getElementBounds();
		Box toBox = arrow.to.getElementBounds();

		fx = fromBox.x + ox;
		fy = fromBox.y + oy;
		tx = toBox.x + ox + arrow.to.getOffsetX();
		ty = toBox.y + oy + arrow.to.getOffsetY();

		// shift the coords to point at the nearest two corners
		if (fx < tx) {
			fx += fromBox.w;
		} else {
			tx += toBox.w;
		}

		// shift from corners to sides
		// CAVEAT: current impl works for horizontal layout only
		ty += toBox.h / 2;
		fy += fromBox.h / 2;

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

}
