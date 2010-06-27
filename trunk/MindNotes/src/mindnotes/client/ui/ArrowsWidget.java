package mindnotes.client.ui;

import gwt.canvas.client.Canvas;

import java.util.HashSet;
import java.util.Set;

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
public class ArrowsWidget extends Composite implements ArrowMaker {

	public static class Arrow {

		public NodeWidget from;
		public NodeWidget to;

		public Arrow(NodeWidget from, NodeWidget to) {
			this.from = from;
			this.to = to;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Arrow))
				return false;
			Arrow a = (Arrow) obj;
			return (a.from == this.from) && (a.to == this.to);
		}

		@Override
		public int hashCode() {
			// xor two hashes together
			return (from == null ? 0 : from.hashCode())
					^ (to == null ? 0 : to.hashCode());
		}

	}

	private Canvas _canvas;
	private MindMapWidget _mindMapWidget;

	private Set<Arrow> _arrowSet;

	public ArrowsWidget(MindMapWidget mindMapWidget) {
		_mindMapWidget = mindMapWidget;
		_arrowSet = new HashSet<Arrow>();
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

		_canvas.clear();

		double ox, oy, fx, fy, tx, ty, sinAlpha, cosAlpha, h1x, h1y, h2x, h2y;
		ox = _canvas.getAbsoluteLeft();
		oy = _canvas.getAbsoluteTop();

		if (isAttached()) {

			for (Arrow arrow : _arrowSet) {

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
		}
	}

	public void addArrow(NodeWidget from, NodeWidget to) {
		_arrowSet.add(new Arrow(from, to));
		render();
	}

	public void removeArrow(NodeWidget from, NodeWidget to) {

		// we rely on the Set removing an object if it .equals() the passed
		// parameter
		_arrowSet.remove(new Arrow(from, to));
		render();
	}

	public void setCanvasSize(int newWidth, int newHeight) {
		this.setPixelSize(newWidth, newHeight);
		_canvas.setWidth(newWidth);
		_canvas.setHeight(newHeight);
	}

}
