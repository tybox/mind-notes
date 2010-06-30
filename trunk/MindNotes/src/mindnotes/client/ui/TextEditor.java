package mindnotes.client.ui;

import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.LinkElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TextEditor extends Composite {

	private HorizontalPanel _toolbar;
	private RichTextArea _textArea;
	private ToggleButton _boldButton;
	private ToggleButton _italicsButton;
	private ToggleButton _underlineButton;

	public TextEditor() {
		VerticalPanel container;
		container = new VerticalPanel();

		_toolbar = new HorizontalPanel();

		_boldButton = new ToggleButton("B");
		_italicsButton = new ToggleButton("I");
		_underlineButton = new ToggleButton("U");

		_boldButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				toggleBold();
			}

		});
		_italicsButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				toggleItalics();
			}

		});
		_underlineButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				toggleUnderline();
			}

		});

		_toolbar.add(_boldButton);
		_toolbar.add(_italicsButton);
		_toolbar.add(_underlineButton);

		_textArea = new RichTextArea();

		_textArea.addMouseUpHandler(new MouseUpHandler() {

			@Override
			public void onMouseUp(MouseUpEvent event) {
				updateButtonState();
			}
		});
		_textArea.addKeyPressHandler(new KeyPressHandler() {

			@Override
			public void onKeyPress(KeyPressEvent event) {
				updateButtonState();

			}
		});

		container.add(_toolbar);
		container.add(_textArea);
		initWidget(container);
		setStylePrimaryName("mindmap");
		addStyleDependentName("rte");

	}

	protected void toggleUnderline() {
		_textArea.getFormatter().toggleUnderline();

	}

	protected void toggleItalics() {
		_textArea.getFormatter().toggleItalic();

	}

	protected void toggleBold() {
		_textArea.getFormatter().toggleBold();
	}

	public String getHTML() {

		return _textArea.getHTML();
	}

	public void setHTML(String html) {
		_textArea.setHTML(html);
		// append style link
		// CAVEAT: this works great, but is implementation-specific, and no
		// checks for NPE's are made.
		// wild debugging sessions - start here
		IFrameElement ife = IFrameElement.as(_textArea.getElement());
		LinkElement le = ife.getContentDocument().createLinkElement();
		le.setAttribute("rel", "stylesheet");
		le.setAttribute("type", "text/css");
		le.setAttribute("href", "rte.css");
		ife.getContentDocument().getElementsByTagName("head").getItem(0)
				.appendChild(le);

		updateButtonState();

		// _textArea.setHTML("<div class=\"fabee\">" + html + "</div>");
	}

	protected void updateButtonState() {
		_boldButton.setDown(_textArea.getFormatter().isBold());
		_italicsButton.setDown(_textArea.getFormatter().isItalic());
		_underlineButton.setDown(_textArea.getFormatter().isUnderlined());
	}

}
