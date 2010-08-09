package mindnotes.client.ui.imagesearch;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ImageSearchWidget extends Composite {

	private JavaScriptObject _imageSearch;
	private boolean _waiting;

	private static ImageSearchWidgetUiBinder uiBinder = GWT
			.create(ImageSearchWidgetUiBinder.class);

	interface ImageSearchWidgetUiBinder extends
			UiBinder<Widget, ImageSearchWidget> {
	}

	interface Styles extends CssResource {
		String resultImg();
	}

	public interface Listener {

		public void imageChosenGesture(String url);

		public void onResize(int offsetWidth, int offsetHeight);

	}

	@UiField
	FlowPanel searchForm;
	@UiField
	TextBox searchInput;
	@UiField
	Button searchButton;
	@UiField
	HTML brandingBox;
	@UiField
	FlowPanel imageContainer;
	@UiField
	Image previewThumbnail;
	@UiField
	Anchor previewLink;
	@UiField
	Label previewDimensions;
	@UiField
	ScrollPanel scrollPanel;
	@UiField
	Styles style;
	@UiField(provided = true)
	PopupPanel imagePreview;

	private ImageResult _selectedImage;
	private Listener _listener;

	public ImageSearchWidget() {
		imagePreview = new PopupPanel() {
			{
				addDomHandler(new MouseOutHandler() {

					@Override
					public void onMouseOut(MouseOutEvent event) {
						imagePreview.hide();
					}
				}, MouseOutEvent.getType());
			}
		};
		initWidget(uiBinder.createAndBindUi(this));
		attachBranding(brandingBox.getElement());
		imagePreview.removeFromParent();

	}

	@UiHandler("scrollPanel")
	public void onScroll(ScrollEvent event) {
		if (imageContainer.getOffsetHeight() - scrollPanel.getScrollPosition()
				- scrollPanel.getOffsetHeight() < 100
				&& !_waiting) {
			nextPage();
		}
	}

	@UiHandler("searchInput")
	public void onSearchInputKeyDown(KeyDownEvent event) {
		if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
			executeSearch(searchInput.getText());
			event.stopPropagation();
		}
	}

	@UiHandler("searchButton")
	public void onSearchButtonClicked(ClickEvent event) {
		executeSearch(searchInput.getText());
	}

	public void performSearch(String text) {
		searchInput.setText(text);
		executeSearch(text);
	}

	/* @formatter:off */
	private native void attachBranding(Element element)/*-{
		$wnd.google.load("search", "1", {"callback" : function() {
			$wnd.google.search.Search.getBranding(element);
		}});
	}-*/;
	/* @formatter:on */

	/* @formatter:off */
	private native void executeSearch(String text)/*-{
		var content = this.@mindnotes.client.ui.imagesearch.ImageSearchWidget::imageContainer;
			content.@com.google.gwt.user.client.ui.FlowPanel::clear()();
		var t = this;

		var executeSearch = function() {

			var searchComplete = function(searcher) {
				t.@mindnotes.client.ui.imagesearch.ImageSearchWidget::_waiting = false;
				if (searcher.results && searcher.results.length > 0) {
					var results = searcher.results;
					for (var i = 0; i < results.length; i++) {
						var result = results[i];
						t.@mindnotes.client.ui.imagesearch.ImageSearchWidget::addImage(Lmindnotes/client/ui/imagesearch/ImageResult;)(result);
					}
				}
			}
			var imageSearch = new $wnd.google.search.ImageSearch();

			imageSearch.setSearchCompleteCallback(this, searchComplete, [imageSearch]);
			imageSearch.setResultSetSize(8);

			imageSearch.execute(text);
			t.@mindnotes.client.ui.imagesearch.ImageSearchWidget::_imageSearch = imageSearch;
		}

		$wnd.google.load("search", "1", {"callback" : executeSearch});
	}-*/;
	/* @formatter:on */

	/* @formatter:off */
	public native void nextPage()/*-{
		var imageSearch = this.@mindnotes.client.ui.imagesearch.ImageSearchWidget::_imageSearch;
		if (imageSearch.cursor && imageSearch.cursor.currentPageIndex < 8) {
			imageSearch.gotoPage(imageSearch.cursor.currentPageIndex + 1);
			this.@mindnotes.client.ui.imagesearch.ImageSearchWidget::_waiting = true;
		}
	}-*/;
	/* @formatter:on */

	public void addImage(final ImageResult result) {

		final Image image = new Image(result.getThumbnailUrl(), 0, 0,
				result.getThumbnailWidth(), result.getThumbnailHeight());
		image.addStyleName(style.resultImg());
		image.addMouseOverHandler(new MouseOverHandler() {

			@Override
			public void onMouseOver(MouseOverEvent event) {
				_selectedImage = result;
				previewThumbnail.setUrl(result.getUnescapedUrl());
				previewThumbnail.setPixelSize(result.getThumbnailWidth() * 2,
						result.getThumbnailHeight() * 2);
				previewLink.setText(result.getVisibleUrl());

				previewLink.setHref(result.getOriginalContextUrl());
				previewDimensions.setText(result.getWidth() + " x "
						+ result.getHeight());
				imagePreview.setPopupPositionAndShow(new PositionCallback() {

					@Override
					public void setPosition(int offsetWidth, int offsetHeight) {
						int x = image.getAbsoluteLeft()
								+ (image.getOffsetWidth() - offsetWidth) / 2;
						int y = image.getAbsoluteTop()
								+ (image.getOffsetHeight() - offsetHeight) / 2;
						if (x + offsetWidth > Window.getClientWidth()) {
							x = Window.getClientWidth() - offsetWidth;
						}
						if (x < 0) {
							x = 0;
						}
						if (y + offsetHeight > Window.getClientHeight()) {
							y = Window.getClientHeight() - offsetHeight;
						}
						if (y < 0) {
							y = 0;
						}

						imagePreview.setPopupPosition(x, y);

					}
				});
			}
		});
		imageContainer.add(image);
		// make justification work
		imageContainer.add(new InlineLabel(" "));
		if (_listener != null) {
			_listener.onResize(getOffsetWidth(), getOffsetHeight());
		}
	}

	@UiHandler("previewThumbnail")
	public void onPreviewThumbnailClicked(ClickEvent event) {
		if (_listener != null) {
			_listener.imageChosenGesture(_selectedImage.getUnescapedUrl());
			imagePreview.hide();
		}
	}

	public void setListener(Listener listener) {
		_listener = listener;
	}
}
