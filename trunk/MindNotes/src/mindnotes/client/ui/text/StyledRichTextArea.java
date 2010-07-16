package mindnotes.client.ui.text;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.LinkElement;
import com.google.gwt.event.logical.shared.InitializeEvent;
import com.google.gwt.event.logical.shared.InitializeHandler;
import com.google.gwt.user.client.ui.RichTextArea;


public class StyledRichTextArea extends RichTextArea implements InitializeHandler {
	
	private boolean _styleInjected = false;
	private String _stylesheetFile;
	private boolean _initialized;
	
	public StyledRichTextArea() {

		
		addInitializeHandler(this);
	}
	
	public StyledRichTextArea(String stylesheetFile) {
		this();
		_stylesheetFile = stylesheetFile;
		
	}
	
	public String getStyleName() {
		return _stylesheetFile;
	}

	public void setStyleName(String styleName) {
		_stylesheetFile = styleName;
	}
	
	@Override
	protected void onUnload() {
		super.onUnload();
		_initialized = false;
	}
	
	private void injectStyle() {
		
		// append style link
		// CAVEAT: this works great, but is implementation-specific, and no
		// checks for NPE's are made.
		// wild debugging sessions - start here
		Element e = getElement();
		IFrameElement ife = IFrameElement.as(e);
		LinkElement le = ife.getContentDocument().createLinkElement();
		le.setAttribute("rel", "stylesheet");
		le.setAttribute("type", "text/css");
		le.setAttribute("href", _stylesheetFile);
		ife.getContentDocument().getElementsByTagName("head").getItem(0)
				.appendChild(le);
		
		_styleInjected = true;
		
	}

	@Override
	public void setHTML(String html) {
		_styleInjected = false;
		super.setHTML(html);
		if (_initialized) {
			injectStyle();
		}
	}

	@Override
	public void onInitialize(InitializeEvent event) {
		_initialized = true;
		
		if (!_styleInjected) injectStyle();
	}
	
}
