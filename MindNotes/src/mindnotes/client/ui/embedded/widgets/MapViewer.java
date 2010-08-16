package mindnotes.client.ui.embedded.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class MapViewer extends Composite implements EmbeddedObjectWidget {

	interface Resources extends ClientBundle {
		@Source("MapViewer.css")
		Style style();
	}

	interface Style extends CssResource {
		String offline();
	}

	private Listener _listener;
	private HTML _html;
	private Timer _offlineTimer;
	private Resources _resources = GWT.create(Resources.class);

	public MapViewer() {
		_resources.style().ensureInjected();
		_html = new HTML();

		initWidget(_html);
		_offlineTimer = new Timer() {

			@Override
			public void run() {
				reportOffline();
			}
		};
		reportLoading();
	}

	private void reportLoading() {
		_html.setHTML("<div class=\"" + _resources.style().offline()
				+ "\">loading...</div>");
		if (_listener != null) {
			_listener.layoutChanged();
		}
	}

	private void reportOffline() {
		_html.setHTML("<div class=\""
				+ _resources.style().offline()
				+ "\">Google Maps is taking longer to load than usual. Are you online?</div>");
		if (_listener != null) {
			_listener.layoutChanged();
		}
	}

	/* @formatter:off */
	private native void embedMap(Element element, String data)/*-{
		var viewer = this;
		var mapsLoaded = function() {
			var timer = viewer.@mindnotes.client.ui.embedded.widgets.MapViewer::_offlineTimer;
			timer.@com.google.gwt.user.client.Timer::cancel()();
			var html = viewer.@mindnotes.client.ui.embedded.widgets.MapViewer::_html;
			html.@com.google.gwt.user.client.ui.HTML::setPixelSize(II)(500, 400);
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
				var l = viewer.@mindnotes.client.ui.embedded.widgets.MapViewer::_listener;
				if (l) {
					l.@mindnotes.client.ui.embedded.widgets.EmbeddedObjectWidget.Listener::layoutChanged()();
				}
			}
		}
		if ($wnd.google.load) {
			$wnd.google.load("maps", "2", {"callback" : mapsLoaded});
		}
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
		_offlineTimer.schedule(5000);
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
