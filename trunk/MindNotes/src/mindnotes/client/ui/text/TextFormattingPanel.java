package mindnotes.client.ui.text;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class TextFormattingPanel extends Composite {

	private static TextFormattingPanelUiBinder uiBinder = GWT
			.create(TextFormattingPanelUiBinder.class);

	private static Resources resources = GWT.create(Resources.class);

	interface TextFormattingPanelUiBinder extends
			UiBinder<Widget, TextFormattingPanel> {
	}

	public interface Resources extends ClientBundle {
		@Source("b.png")
		ImageResource iconB();
	}

	public TextFormattingPanel() {

		initWidget(uiBinder.createAndBindUi(this));

	}

}
