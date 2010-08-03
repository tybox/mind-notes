package mindnotes.client.ui.embedded;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

public class YouTubePlayer extends Widget {

	public interface PlayerReadyCallback {
		public void onPlayerReady();
	}

	private PlayerReadyCallback _callback;
	private String _videoIdToAttach;

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
			ytp.@mindnotes.client.ui.embedded.YouTubePlayer::onPlayerReady();
		}
	}-*/;
	/* @formatter:on */

	public void showPlayer(String videoId, PlayerReadyCallback callback) {
		_callback = callback;
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
		if (_callback != null) {
			_callback.onPlayerReady();
		}
	}

}
