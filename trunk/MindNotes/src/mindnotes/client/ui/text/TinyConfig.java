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
		theme_advanced_toolbar_location: "external"
		};
	}-*/;

	public void setNativeConfig(JavaScriptObject nativeConfig) {
		_nativeConfig = nativeConfig;
	}

	public JavaScriptObject getNativeConfig() {
		return _nativeConfig;
	}

}
