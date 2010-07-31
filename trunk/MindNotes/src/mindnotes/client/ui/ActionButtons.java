package mindnotes.client.ui;

import mindnotes.client.presentation.ActionOptions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;

public class ActionButtons {

	public interface Resources extends ClientBundle {
		ImageResource plusIcon();

		ImageResource deleteIcon();

		Styles buttonStyles();

		public interface Styles extends CssResource {
			String button();
		}
	}

	public interface Listener {

		public void addRightClicked();

		public void addLeftClicked();

		public void addUpClicked();

		public void addDownClicked();

		public void deleteClicked();

		public void expandClicked();

	}

	// DI deps
	private ButtonContainer _container;
	private NodeContainer _nodeContainer;
	private Listener _listener;

	// Buttons
	private PushButton _deleteButton;
	private PushButton _addLeftButton;
	private PushButton _addRightButton;
	private PushButton _addUpButton;
	private PushButton _addDownButton;

	// resources
	private Resources _resources = GWT.create(Resources.class);
	private ActionOptions _options;
	private NodeWidget _widget;

	public ActionButtons() {
		_resources.buttonStyles().ensureInjected();
		_addLeftButton = createButton(_resources.plusIcon());
		_addRightButton = createButton(_resources.plusIcon());
		_addUpButton = createButton(_resources.plusIcon());
		_addDownButton = createButton(_resources.plusIcon());

		_deleteButton = createButton(_resources.deleteIcon());
		_deleteButton.addStyleName(_resources.buttonStyles().button());

		// events
		{

			_addLeftButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					if (_listener != null)
						_listener.addLeftClicked();
				}
			});
			_addRightButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					if (_listener != null)
						_listener.addRightClicked();
				}
			});
			_addUpButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					if (_listener != null)
						_listener.addUpClicked();
				}
			});
			_addDownButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					if (_listener != null)
						_listener.addDownClicked();
				}
			});
			_deleteButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					if (_listener != null)
						_listener.deleteClicked();
				}
			});

		}

	}

	/**
	 * @return
	 * 
	 */
	private PushButton createButton(ImageResource icon) {
		PushButton b = new PushButton(new Image(icon));
		b.addStyleName(_resources.buttonStyles().button());
		return b;
	}

	public void showNextTo(NodeWidget widget, ActionOptions options) {

		_widget = widget;
		_options = options;

		updateButtonLayout();

	}

	public void setListener(Listener listener) {
		_listener = listener;
	}

	public void setContainer(ButtonContainer container) {
		_container = container;
		_container.addButton(_addLeftButton);
		_container.addButton(_addRightButton);
		_container.addButton(_addUpButton);
		_container.addButton(_addDownButton);
		_container.addButton(_deleteButton);
	}

	public ButtonContainer getContainer() {
		return _container;
	}

	public void setNodeContainer(NodeContainer nodeContainer) {
		_nodeContainer = nodeContainer;
	}

	public NodeContainer getNodeContainer() {
		return _nodeContainer;
	}

	public void hideButtons() {
		_deleteButton.setVisible(false);
		_addLeftButton.setVisible(false);
		_addRightButton.setVisible(false);
		_addUpButton.setVisible(false);
		_addDownButton.setVisible(false);
	}

	public void updateButtonLayout() {
		if (_widget == null) {
			hideButtons();
			return;
		}
		int x, y, w, h;
		x = _nodeContainer.getNodeRelativeLeft(_widget);
		y = _nodeContainer.getNodeRelativeTop(_widget);
		w = _widget.getElementBounds().w;
		h = _widget.getElementBounds().h;

		_deleteButton.setVisible(_options.canDelete());
		_container.setButtonPosition(_deleteButton, x + w - 22, y + h + 5);
		_addLeftButton.setVisible(_options.canAddLeft());
		_container.setButtonPosition(_addLeftButton, x - 27, y + (h - 20) / 2);
		_addRightButton.setVisible(_options.canAddRight());
		_container.setButtonPosition(_addRightButton, x + w + 5, y + (h - 20)
				/ 2);
		_addUpButton.setVisible(_options.canHaveSiblings());
		_container.setButtonPosition(_addUpButton, x + (w - 20) / 2, y - 24);
		_addDownButton.setVisible(_options.canHaveSiblings());
		_container.setButtonPosition(_addDownButton, x + (w - 20) / 2, y + h
				+ 5);

	}
}
