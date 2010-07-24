package mindnotes.client.ui.text;

import com.google.gwt.core.client.JavaScriptObject;

public class TinyConfig {
	private JavaScriptObject _nativeConfig;

	public TinyConfig() {
		createDefaultConfig();
	}

	private native void createDefaultConfig() /*-{
		this.@mindnotes.client.ui.text.TinyConfig::_nativeConfig =
		{
		mode : "none",
		theme : "advanced",
		theme_advanced_toolbar_align : "left",
		theme_advanced_buttons1: "bold,italic,underline,strikethrough,separator,justifyleft,justifycenter,justifyright,justifyfull, separator,link,unlink,image,html,separator,undo,cut,copy,paste",
		theme_advanced_buttons2: "fontselect,fontsizeselect,forecolor,backcolor,separator,indent,outdent,bullist,numlist",
		theme_advanced_buttons3: ""
		};
	}-*/;

	public void setNativeConfig(JavaScriptObject nativeConfig) {
		_nativeConfig = nativeConfig;
	}

	public JavaScriptObject getNativeConfig() {
		return _nativeConfig;
	}

}
