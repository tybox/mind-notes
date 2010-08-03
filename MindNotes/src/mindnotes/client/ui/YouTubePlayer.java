package mindnotes.client.ui;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

public class YouTubePlayer extends Widget {

	public YouTubePlayer() {
		setElement(Document.get().createDivElement());
		getElement().setId(DOM.createUniqueId());
	}

	/* @formatter:off */
	
	private native void showPlayer(String videoId, String divId, String playerId)/*-{
		var params = { allowScriptAccess: "always" };
		var atts = { id: playerId};
		$wnd.swfobject.embedSWF("http://www.youtube.com/v/"+videoId+"?enablejsapi=1", 
		               divId, "212", "178", "8", null, null, params, atts);
	}-*/;
	/* @formatter:on */

	public void showPlayer(String videoId) {
		showPlayer(videoId, getElement().getId(), DOM.createUniqueId());
	}
}
