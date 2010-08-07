package mindnotes.client.ui.imagesearch;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class ImageSearchWidget extends Composite {

	private JavaScriptObject _imageSearch;
	private JavaScriptObject _searchFormElement;
	private boolean _waiting;

	private static ImageSearchWidgetUiBinder uiBinder = GWT
			.create(ImageSearchWidgetUiBinder.class);

	interface ImageSearchWidgetUiBinder extends
			UiBinder<Widget, ImageSearchWidget> {
	}

	interface Styles extends CssResource {
		String resultImg();
	}

	@UiField
	HTML searchForm;

	@UiField
	FlowPanel imageContainer;

	@UiField
	PopupPanel imagePreview;

	@UiField
	Image previewThumbnail;

	@UiField
	Anchor previewLink;

	@UiField
	Label previewDimensions;

	@UiField
	Label previewURL;

	@UiField
	ScrollPanel scrollPanel;

	@UiField
	Styles style;

	public ImageSearchWidget() {
		loadSearchAPI();
		initWidget(uiBinder.createAndBindUi(this));
		attachSearchForm(searchForm.getElement());
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

	private native void loadSearchAPI() /*-{
		$wnd.google.load('search', '1');
	}-*/;

	public native void performSearch(String text)/*-{
		var sf = this.@mindnotes.client.ui.imagesearch.ImageSearchWidget::_searchFormElement;
		sf.execute(text);
	}-*/;

	/* @formatter:off */
	private native void attachSearchForm(Element element)/*-{
		var searchForm = new $wnd.google.search.SearchForm(false, element);
		var onSubmit = function(searchForm) {
			if (searchForm.input.value) {
				this.@mindnotes.client.ui.imagesearch.ImageSearchWidget::executeSearch(Ljava/lang/String;)(searchForm.input.value);
			}
		}
		searchForm.setOnSubmitCallback(this, onSubmit);
		this.@mindnotes.client.ui.imagesearch.ImageSearchWidget::_searchFormElement = searchForm;
	}-*/;
	/* @formatter:on */

	/* @formatter:off */
	private native void executeSearch(String text)/*-{
		var content = this.@mindnotes.client.ui.imagesearch.ImageSearchWidget::imageContainer;
		content.@com.google.gwt.user.client.ui.FlowPanel::clear()();

		var searchComplete = function(searcher) {
			this.@mindnotes.client.ui.imagesearch.ImageSearchWidget::_waiting = false;
			if (searcher.results && searcher.results.length > 0) {
				var results = searcher.results;
				for (var i = 0; i < results.length; i++) {
					var result = results[i];
					this.@mindnotes.client.ui.imagesearch.ImageSearchWidget::addImage(Lmindnotes/client/ui/imagesearch/ImageResult;)(result);
				}
			}
		}
		var imageSearch = new $wnd.google.search.ImageSearch();

		imageSearch.setSearchCompleteCallback(this, searchComplete, [imageSearch]);
		imageSearch.setResultSetSize(8);

		imageSearch.execute(text);
		this.@mindnotes.client.ui.imagesearch.ImageSearchWidget::_imageSearch = imageSearch;
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

	public void addImage(ImageResult result) {

		Image image = new Image(result.getThumbnailUrl(), 0, 0,
				result.getThumbnailWidth(), result.getThumbnailHeight());
		image.addStyleName(style.resultImg());
		imageContainer.add(image);
		imageContainer.add(new InlineLabel(" "));
	}

}
