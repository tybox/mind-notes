package mindnotes.client.ui.text;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;

public class TinyEditor {
	private JavaScriptObject _nativeEditor;
	private Element _div = null;
	private Listener _listener;
	private JavaScriptObject _nativeConfig;

	public interface Listener {
		public void onEditorExitGesture();

		public void onYTVideoInserted(String id);
	}

	public TinyEditor() {
		createDefaultConfig();
	}

	/* @formatter:off */
	private native void createDefaultConfig() /*-{
		var te = this;
		this.@mindnotes.client.ui.text.TinyEditor::_nativeConfig =
		{
			mode : "none",
			theme : "advanced",
			plugins : "paste",
			paste_auto_cleanup_on_paste : true,
			theme_advanced_toolbar_align : "left",
			theme_advanced_buttons1: "bold,italic,underline,strikethrough,separator,justifyleft,justifycenter,justifyright,justifyfull, separator,link,unlink,image,html,separator,undo,cut,copy,paste",
			theme_advanced_buttons2: "fontselect,fontsizeselect,forecolor,backcolor,separator,indent,outdent,bullist,numlist",
			theme_advanced_buttons3: "",
			paste_preprocess : function(pl, o) {

			    var matches = /http:\/\/(?:www\.)?youtube\.\w\w\w?\/watch\?v=([a-zA-Z0-9\-]*)/.exec(o.content);
			    if (matches != null) {

			    	// look for the backref match
			    	var id = matches[1];
					o.content =""; // paste nothing
					te.@mindnotes.client.ui.text.TinyEditor::onYTVideoInserted(Ljava/lang/String;)(id);
			    }

		    },

			setup: function(ed) {
				te.@mindnotes.client.ui.text.TinyEditor::onSetup(Lcom/google/gwt/core/client/JavaScriptObject;)(ed);
			}

		};
	}-*/;
	/* @formatter:on */

	public void attach(Element div) {
		if (_div != null) {
			detach();
		}
		setDiv(div);
		attachEditor(_div.getId(), _nativeConfig);
	}

	private void setDiv(Element div) {
		_div = div;
		String id = _div.getId();
		if (id == null || id.equals(""))
			_div.setId(DOM.createUniqueId());

	}

	public void detach() {
		if (_div == null) {
			return;
		}
		detatchEditor(_div.getId());
		_div = null;
	}

	private native void detatchEditor(String id) /*-{
		var e = this.@mindnotes.client.ui.text.TinyEditor::_nativeEditor;
		e.save();
		$wnd.tinymce.execCommand("mceRemoveControl", false, id);
	}-*/;

	private native void attachEditor(String id, JavaScriptObject config) /*-{
		$wnd.tinymce.init(config);
		var editor = new $wnd.tinymce.Editor(id, config);
		this.@mindnotes.client.ui.text.TinyEditor::_nativeEditor = editor;

		editor.render();
	}-*/;

	public void setFocus(boolean focused) {
		if (focused && _div != null) {
			focusTiny(_div.getId());
		}
	}

	private native void focusTiny(String id) /*-{
		$wnd.tinymce.execCommand('mceFocus', false, id);
	}-*/;

	public void exitEditor() {
		if (_listener != null) {
			_listener.onEditorExitGesture();
		}
	}

	// @formatter:off
	public native void onSetup(JavaScriptObject ed) /*-{
		var tinyEditor = this;
		ed.onKeyDown.add(function(ed, evt) {
			if (evt.keyCode == 27) { //escape
				tinyEditor.@mindnotes.client.ui.text.TinyEditor::exitEditor()();
			}
		});
	}-*/;
	// @formatter:on

	public void setListener(Listener listener) {
		_listener = listener;
	}

	private void onYTVideoInserted(String id) {
		if (_listener != null) {
			_listener.onYTVideoInserted(id);
		}
	}

}
