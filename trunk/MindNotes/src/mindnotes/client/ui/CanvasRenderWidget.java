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
/**
 * CanvasRenderWidget is responsible for rendering any UI object that instead of being shown via DOM,
 * is rendered on a HTML5 Canvas.
 * 
 * Currently, there are two types of such objects: Arrows and Ghost Nodes (used in D&D)
 */
public class CanvasRenderWidget extends Composite {

	private Canvas _canvas;
	private MindMapWidget _mindMapWidget;
	private ArrowRenderer _arrowRenderer;

	public CanvasRenderWidget(MindMapWidget mindMapWidget) {

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

		// _renderer = new ClassicArrowRenderer();
		_arrowRenderer = new BezierArrowRenderer();
		_arrowRenderer.setCanvas(_canvas);

		initWidget(_canvas);
	}

	protected void sendOnClick() {
		if (_mindMapWidget != null)
			_mindMapWidget.onClick();
	}

	public void render(int ox, int oy) {
		if (!isAttached())
			return;

		_canvas.clear();

		renderArrowsForNodeWidget(ox, oy, _mindMapWidget.getRootNodeWidget());
		GhostNode ghost = _mindMapWidget.getGhostNode();
		if (ghost != null) {
			LayoutTreeElement parent = ghost;
			int gox = ox, goy = oy;
			while (true) {
				parent = parent.getLayoutParent();
				if (parent == null)
					break;
				gox += parent.getOffsetX();
				goy += parent.getOffsetY();
			}
			renderGhostNodeBranch(ghost, gox, goy);
		}
	}

	private void renderArrowsForNodeWidget(int ox, int oy, NodeWidget node) {

		int nox = ox + node.getOffsetX();
		int noy = oy + node.getOffsetY();

		for (Arrow arrow : node.getArrows()) {
			_arrowRenderer.renderArrow(nox, noy, arrow);
		}

		// do the same recursively for children
		for (NodeWidget child : node.getNodeChildren()) {
			renderArrowsForNodeWidget(nox, noy, child);
		}

	}

	private void renderGhostNodeBranch(GhostNode root, int ox, int oy) {

		LayoutTreeElement rootParent = root.getLayoutParent();

		int nox = ox + root.getOffsetX();
		int noy = oy + root.getOffsetY();

		_arrowRenderer.renderArrow(ox, oy, nox, noy,
				rootParent.getElementBounds(), root.getElementBounds());

		renderGhostNode(root, nox, noy);

		for (GhostNode child : root.getChildren()) {

			renderGhostNodeBranch(child, nox, noy);
		}

	}

	private void renderGhostNode(GhostNode root, int nox, int noy) {
		_canvas.setStrokeStyle("#1b56b5");
		Box b = root.getElementBounds();
		_canvas.beginPath();
		_canvas.moveTo(nox + b.x, noy + b.y + b.h);
		_canvas.lineTo(nox + b.x + b.w, noy + b.y + b.h);
		_canvas.stroke();
	}

	public void setCanvasSize(int newWidth, int newHeight) {
		this.setPixelSize(newWidth, newHeight);
		_canvas.setWidth(newWidth);
		_canvas.setHeight(newHeight);
	}

}
