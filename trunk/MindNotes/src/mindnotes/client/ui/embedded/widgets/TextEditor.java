package mindnotes.client.ui.embedded.widgets;

import mindnotes.client.ui.LayoutTreeElement;
import mindnotes.client.ui.resize.ResizeController;
import mindnotes.client.ui.resize.ResizeHandle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class TextEditor implements EmbeddedObjectWidget {

	interface Resources extends ClientBundle {
		@Source("TextEditor.css")
		public Style style();
	}

	interface Style extends CssResource {
		String editor();
	}

	private HTML _html;
	private Listener _listener;
	private JavaScriptObject _nativeEditor;
	private Element _div = null;
	private JavaScriptObject _nativeConfig;
	private LayoutTreeElement _layoutHost;
	private FlowPanel _flowPanel;
	private FocusPanel _dragHandle;
	private Resources _resources = GWT.create(Resources.class);

	public TextEditor(ResizeController controller) {
		_resources.style().ensureInjected();
		_flowPanel = new FlowPanel();

		_html = new HTML() {
			{
				addDomHandler(new DoubleClickHandler() {

					@Override
					public void onDoubleClick(DoubleClickEvent event) {
						attach(_html.getElement());

					}
				}, DoubleClickEvent.getType());

			}

			protected void onLoad() {
				attach(_html.getElement());
			};
		};

		_flowPanel.add(_html);
		ResizeHandle handle = new ResizeHandle(controller, _flowPanel);
		handle.setListener(new ResizeHandle.Listener() {

			@Override
			public void onResized() {
				fireLayoutChanged();
			}
		});
		_flowPanel.add(handle);
		_flowPanel.addStyleName(_resources.style().editor());

		createDefaultConfig();

	}

	@Override
	public void setData(String data) {
		_html.setHTML(data);
	}

	@Override
	public void setListener(Listener l) {
		_listener = l;
	}

	@Override
	public Widget getObjectWidget() {
		return _flowPanel;
	}

	@Override
	public String getObjectTitle() {
		return "Text";
	}

	/* @formatter:off */
	private native void createDefaultConfig() /*-{
		var te = this;
		this.@mindnotes.client.ui.embedded.widgets.TextEditor::_nativeConfig =
		{
			mode : "none",
			theme : "advanced",
			plugins : "paste",
			width: "100%",
			height: "100%",
			paste_auto_cleanup_on_paste : true,
			theme_advanced_toolbar_align : "left",
			theme_advanced_toolbar_location : "top",
			theme_advanced_buttons2: "bold,italic,underline,strikethrough,separator,justifyleft,justifycenter,justifyright,justifyfull, separator,link,unlink,image",
			theme_advanced_buttons1: "fontselect,fontsizeselect,forecolor,backcolor,separator,bullist,numlist",
			theme_advanced_buttons3: "",
			paste_preprocess : function(pl, o) {

			    var matches = /http:\/\/(?:www\.)?youtube\.\w\w\w?\/watch\?v=([a-zA-Z0-9_\-]*)/.exec(o.content);
			    if (matches != null) {

			    	// look for the backref match
			    	var id = matches[1];
					//o.content =""; // paste nothing

					//te.@mindnotes.client.ui.embedded.widgets.TextEditor::onYTVideoInserted(Ljava/lang/String;)(id);
			    }

		    },

			setup: function(ed) {
				te.@mindnotes.client.ui.embedded.widgets.TextEditor::onSetup(Lcom/google/gwt/core/client/JavaScriptObject;)(ed);

			}

		};
	}-*/;
	/* @formatter:on */

	// @formatter:off
	public native void onSetup(JavaScriptObject ed) /*-{
		var tinyEditor = this;
		ed.onInit.add(function(e){
			tinyEditor.@mindnotes.client.ui.embedded.widgets.TextEditor::onInitialized()();
		})
		ed.onKeyDown.add(function(e, evt) {
			if (evt.keyCode == 27) { //escape
				tinyEditor.@mindnotes.client.ui.embedded.widgets.TextEditor::exitEditor()();
			}
		});
		ed.onDeactivate.add(function(evt) {
				tinyEditor.@mindnotes.client.ui.embedded.widgets.TextEditor::exitEditor()();
			}
		);
	}-*/;
	// @formatter:on

	private void onInitialized() {
		fireLayoutChanged();
	}

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
		fireLayoutChanged();
		if (_listener != null) {
			_listener.dataChanged(_html.getHTML());
		}

	}

	private void fireLayoutChanged() {
		if (_listener != null) {
			_listener.layoutChanged();
		}
	}

	private native void detatchEditor(String id) /*-{
		var e = this.@mindnotes.client.ui.embedded.widgets.TextEditor::_nativeEditor;
		e.save();
		$wnd.tinymce.execCommand("mceRemoveControl", false, id);
	}-*/;

	private native void attachEditor(String id, JavaScriptObject config) /*-{
		$wnd.tinymce.init(config);
		var editor = new $wnd.tinymce.Editor(id, config);
		this.@mindnotes.client.ui.embedded.widgets.TextEditor::_nativeEditor = editor;

		editor.render();
	}-*/;

	private native void focusTiny(String id) /*-{
		$wnd.tinymce.execCommand('mceFocus', false, id);
	}-*/;

	public void exitEditor() {
		detach();

	}

}
