package mindnotes.client.ui.embedded;

import mindnotes.client.presentation.EmbeddedObjectView;
import mindnotes.client.ui.LayoutTreeElement;
import mindnotes.client.ui.embedded.widgets.EmbeddedObjectWidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class EmbeddedObjectContainer extends Composite implements
		EmbeddedObjectView, EmbeddedObjectWidget.Listener {

	private static EmbeddedObjectContainerUiBinder uiBinder = GWT
			.create(EmbeddedObjectContainerUiBinder.class);

	interface EmbeddedObjectContainerUiBinder extends
			UiBinder<Widget, EmbeddedObjectContainer> {
	}

	@UiField
	ImageResource imgCollapsed;

	@UiField
	ImageResource imgExpanded;

	@UiField
	SimplePanel embeddedWidget;

	@UiField
	Label titleLabel;

	@UiField
	Image expansionIcon;

	private Listener _listener;

	private boolean _expanded = true;

	private LayoutTreeElement _layoutHost;

	public EmbeddedObjectContainer(EmbeddedObjectWidget widget, String data) {

		initWidget(uiBinder.createAndBindUi(this));

		if (widget != null) {
			widget.setListener(this);
			widget.setData(data);
			titleLabel.setText(widget.getObjectTitle());
			embeddedWidget.setWidget(widget.getObjectWidget());
		}

	}

	@Override
	public void setListener(Listener l) {
		_listener = l;
	}

	@UiHandler("titleLabel")
	public void onTitleClicked(ClickEvent e) {
		toggleExpansion();
	}

	/**
	 * 
	 */
	private void toggleExpansion() {
		_expanded = !_expanded;
		embeddedWidget.setVisible(_expanded);
		expansionIcon.setResource(_expanded ? imgExpanded : imgCollapsed);
		invalidateLayout();

	}

	/**
	 * 
	 */
	private void invalidateLayout() {
		DeferredCommand.addCommand(new Command() {

			@Override
			public void execute() {
				if (_layoutHost != null)
					_layoutHost.setLayoutValid(false);
			}
		});
	}

	@UiHandler("expansionIcon")
	public void onExpansionIconClicked(ClickEvent e) {
		toggleExpansion();
	}

	@UiHandler("removeLabel")
	public void onRemoveClicked(ClickEvent e) {
		if (_listener != null) {
			_listener.onEmbeddedObjectViewRemove();
		}
	}

	public void setLayoutHost(LayoutTreeElement layoutHost) {
		_layoutHost = layoutHost;
	}

	public LayoutTreeElement getLayoutHost() {
		return _layoutHost;
	}

	@Override
	public void dataChanged(String newData) {
		if (_listener != null) {
			_listener.onDataChanged(newData);
		}
	}

	@Override
	public void layoutChanged() {
		invalidateLayout();
	}

}
