package mindnotes.client.ui.text;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.SimplePanel;

public class TinyEditor extends Composite implements HasHTML {
	private JavaScriptObject _nativeEditor;
	private TinyConfig _config = new TinyConfig();

	private Element _div;

	public TinyEditor() {
		SimplePanel panel = new SimplePanel();
		_div = DOM.createDiv();
		_div.setId(DOM.createUniqueId());
		panel.getElement().appendChild(_div);
		initWidget(panel);
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		attachEditor(_div.getId(), _config.getNativeConfig());

	}

	@Override
	protected void onDetach() {
		super.onDetach();
		detatchEditor(_div.getId());
	}

	private native void detatchEditor(String id) /*-{
		$wnd.tinymce.execCommand("mceRemoveControl", false, id);
	}-*/;

	private native void attachEditor(String id, JavaScriptObject config) /*-{
		$wnd.tinymce.init(config);
		var editor = new $wnd.tinymce.Editor(id, config);

		this.@mindnotes.client.ui.text.TinyEditor::_nativeEditor = editor;
		editor.render();
	}-*/;

	@Override
	public String getText() {
		return getHTML();
	}

	@Override
	public void setText(String text) {
		setHTML(text);
	}

	@Override
	public String getHTML() {
		if (isAttached())
			return getNativeHTML();
		else
			return _div.getInnerHTML();
	}

	private native String getNativeHTML()/*-{
		var e = this.@mindnotes.client.ui.text.TinyEditor::_nativeEditor;
		var c = e.getContent();
		return c;
	}-*/;

	@Override
	public void setHTML(String html) {
		if (isAttached() && isTinyInited()) {
			setNativeHTML(html);
		}
		_div.setInnerHTML(html);

	}

	private native boolean isTinyInited() /*-{
		var e = this.@mindnotes.client.ui.text.TinyEditor::_nativeEditor;
		return !!(e.dom);
	}-*/;

	private native void setNativeHTML(String html)/*-{
		var e = this.@mindnotes.client.ui.text.TinyEditor::_nativeEditor
		e.setContent(html);
	}-*/;

	public void setFocus(boolean focused) {
		if (focused) {
			focusTiny(_div.getId());
		}
	}

	private native void focusTiny(String id) /*-{
		$wnd.tinymce.execCommand('mceFocus', false, id);
	}-*/;

}
