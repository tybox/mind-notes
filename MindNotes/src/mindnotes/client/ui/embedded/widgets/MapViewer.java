package mindnotes.client.ui.embedded.widgets;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class MapViewer extends Composite implements EmbeddedObjectWidget {

	private Listener _listener;
	private HTML _html;

	public MapViewer() {
		_html = new HTML();
		initWidget(_html);
		_html.setPixelSize(500, 400);

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

					viewer.@mindnotes.client.ui.embedded.widgets.MapViewer::onLocationUpdated(Ljava/lang/String;)(strBounds);
				};
				maps.Event.addListener(map, "moveend", handler);

			}
		}

		$wnd.google.load("maps", "2", {"callback" : mapsLoaded});
	}-*/;
	/* @formatter:on */

	private void onLocationUpdated(String newLocation) {
		if (_listener != null) {
			_listener.dataChanged(newLocation);
		}
	}

	@Override
	public void setListener(Listener listener) {
		_listener = listener;
	}

	@Override
	public void setData(String data) {
		embedMap(_html.getElement(), data);
	}

	@Override
	public Widget getObjectWidget() {
		return this;
	}

	@Override
	public String getObjectTitle() {
		return "Geo Map";
	}
}
