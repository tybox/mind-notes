package mindnotes.client.ui.text;

import com.google.gwt.core.client.JavaScriptObject;

public class TinyConfig {

	public interface SetupCallback {
		public void onSetup(JavaScriptObject editor);
	}

	private JavaScriptObject _nativeConfig;
	private SetupCallback _setupCallback;

	public TinyConfig(SetupCallback callback) {
		_setupCallback = callback;
		createDefaultConfig();
	}

	private native void createDefaultConfig() /*-{
		var cfg = this;
		this.@mindnotes.client.ui.text.TinyConfig::_nativeConfig =
		{
		mode : "none",
		theme : "advanced",
		theme_advanced_toolbar_align : "left",
		theme_advanced_buttons1: "bold,italic,underline,strikethrough,separator,justifyleft,justifycenter,justifyright,justifyfull, separator,link,unlink,image,html,separator,undo,cut,copy,paste",
		theme_advanced_buttons2: "fontselect,fontsizeselect,forecolor,backcolor,separator,indent,outdent,bullist,numlist",
		theme_advanced_buttons3: "",
		setup: function(ed) {
		cfg.@mindnotes.client.ui.text.TinyConfig::onSetup(Lcom/google/gwt/core/client/JavaScriptObject;)(ed);
		}
		};
	}-*/;

	public void setNativeConfig(JavaScriptObject nativeConfig) {
		_nativeConfig = nativeConfig;
	}

	public JavaScriptObject getNativeConfig() {
		return _nativeConfig;
	}

	public void onSetup(JavaScriptObject ed) {
		if (_setupCallback != null) {
			_setupCallback.onSetup(ed);
		}
	}
}
