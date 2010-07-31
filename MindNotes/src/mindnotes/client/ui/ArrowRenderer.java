package mindnotes.client.ui;

import gwt.canvas.client.Canvas;

public interface ArrowRenderer {
	public void setCanvas(Canvas canvas);

	public void renderArrow(int ox, int oy, Arrow arrow);

}
