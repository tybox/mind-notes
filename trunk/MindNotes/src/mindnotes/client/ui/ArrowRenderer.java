package mindnotes.client.ui;

import gwt.canvas.client.Canvas;

public interface ArrowRenderer {
	public void setCanvas(Canvas canvas);

	public void renderArrow(int ox, int oy, Arrow arrow);

	public void renderArrow(int fox, int foy, int tox, int toy, Box f, Box t);

}
