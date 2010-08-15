package mindnotes.client.ui.embedded.widgets;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

public class YouTubePlayer extends Widget implements EmbeddedObjectWidget {

	private String _videoIdToAttach;
	private Listener _listener;

	public YouTubePlayer() {
		setElement(Document.get().createDivElement());
		getElement().setId(DOM.createUniqueId());
	}

	/* @formatter:off */
	
	private native void showPlayer(String videoId, String divId, String playerId)/*-{
		var ytp = this;
		var params = { allowScriptAccess: "always" };
		var atts = { id: playerId};
		$wnd.swfobject.embedSWF("http://www.youtube.com/v/"+videoId+"?enablejsapi=1&playerapiid="+playerId, 
		               divId, "212", "178", "8", null, null, params, atts);
		$wnd.onYouTubePlayerReady = function(playerid) {
			ytp.@mindnotes.client.ui.embedded.widgets.YouTubePlayer::onPlayerReady();
		}
	}-*/;
	/* @formatter:on */

	public void showPlayer(String videoId) {
		if (isAttached()) {
			showPlayer(videoId, getElement().getId(), DOM.createUniqueId());
		} else {
			_videoIdToAttach = videoId;
		}
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		if (_videoIdToAttach != null) {
			showPlayer(_videoIdToAttach, getElement().getId(),
					DOM.createUniqueId());
			_videoIdToAttach = null;
		}
	}

	public void onPlayerReady() {
		if (_listener != null) {
			_listener.layoutChanged();
		}
	}

	@Override
	public void setData(String data) {
		showPlayer(data);
	}

	@Override
	public void setListener(Listener l) {
		_listener = l;

	}

	@Override
	public Widget getObjectWidget() {
		return this;
	}

	@Override
	public String getObjectTitle() {
		return "Video";
	}

}
