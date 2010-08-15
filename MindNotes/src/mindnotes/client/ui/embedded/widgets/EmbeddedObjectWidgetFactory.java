package mindnotes.client.ui.embedded.widgets;

import java.util.HashMap;

import mindnotes.client.ui.resize.ResizeController;

public class EmbeddedObjectWidgetFactory {

	private interface FactoryAction {
		public EmbeddedObjectWidget create();
	}

	private HashMap<String, FactoryAction> _types;
	private ResizeController _controller;

	public EmbeddedObjectWidgetFactory(ResizeController resizeController) {
		_controller = resizeController;
		_types = new HashMap<String, EmbeddedObjectWidgetFactory.FactoryAction>();

		_types.put("image", new FactoryAction() {

			@Override
			public EmbeddedObjectWidget create() {
				return new ImageContainer();
			}
		});
		_types.put("map", new FactoryAction() {

			@Override
			public EmbeddedObjectWidget create() {
				return new MapViewer();
			}
		});
		_types.put("youtube", new FactoryAction() {

			@Override
			public EmbeddedObjectWidget create() {
				return new YouTubePlayer();
			}
		});
		_types.put("richtext", new FactoryAction() {

			@Override
			public EmbeddedObjectWidget create() {
				return new TextEditor(_controller);
			}
		});
	}

	public EmbeddedObjectWidget createObjectWidget(String type) {
		FactoryAction action = _types.get(type);
		if (action != null) {
			return action.create();
		} else {
			return null;
		}

	}

}
