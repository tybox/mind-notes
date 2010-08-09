package mindnotes.client.ui.maps;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;

public class MapViewer extends Composite {

	public interface Listener {

		void onLocationUpdated(String newLocation);

	}

	private Listener _listener;

	public MapViewer(String data) {
		HTML html = new HTML();
		initWidget(html);
		html.setPixelSize(500, 400);
		embedMap(html.getElement(), data);
	}

	/* @formatter:off */
	private native void embedMap(Element element, String data)/*-{
		var maps = $wnd.google.maps;
		var viewer = this;
		var mapsLoaded = function() {
			var maps = $wnd.google.maps;
			var map;
			if (maps.BrowserIsCompatible()) {
				var mapOptions = {
					googleBarOptions : {
						style : "new",
					}
				}
				map = new maps.Map2(element, mapOptions);
				if (data) {
					var dataChunks = data.split(";", 2);
					var sw = maps.LatLng.fromUrlValue(dataChunks[0]);
					var ne = maps.LatLng.fromUrlValue(dataChunks[1]);
					$wnd.alert('"'+sw+'" "'+ne+'"');
					var bounds = new maps.LatLngBounds(sw, ne);
					map.setCenter(bounds.getCenter());
					map.setZoom(map.getBoundsZoomLevel(bounds));
				} else {
					map.setCenter(new maps.LatLng(33.956461,-118.396225), 13);
				}
				map.setUIToDefault();
				map.enableGoogleBar();
				var handler = function() {

					var bounds = map.getBounds();
					var strBounds = bounds.getSouthWest().toUrlValue() + ";"+ bounds.getNorthEast().toUrlValue()

					viewer.@mindnotes.client.ui.maps.MapViewer::onLocationUpdated(Ljava/lang/String;)(strBounds);
				};
				maps.Event.addListener(map, "moveend", handler);

			}
		}

		$wnd.google.load("maps", "2", {"callback" : mapsLoaded});
	}-*/;
	/* @formatter:on */

	private void onLocationUpdated(String newLocation) {
		if (_listener != null) {
			_listener.onLocationUpdated(newLocation);
		}
	}

	public void setListener(Listener listener) {
		_listener = listener;
	}
}
