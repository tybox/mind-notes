package mindnotes.client.ui.imagesearch;

import com.google.gwt.core.client.JavaScriptObject;

public final class ImageResult extends JavaScriptObject {
	protected ImageResult() {
		// Required for overlay types
	}

	public native String getContent() /*-{
		return this.content;
	}-*/;

	public native String getContentNoFormatting() /*-{
		return this.contentNoFormatting;
	}-*/;

	public native int getHeight() /*-{
		return Number(this.height);
	}-*/;

	public native String getOriginalContextUrl() /*-{
		return this.originalContextUrl;
	}-*/;

	public native int getThumbnailHeight() /*-{
		return Number(this.tbHeight);
	}-*/;

	public native String getThumbnailUrl() /*-{
		return this.tbUrl;
	}-*/;

	public native int getThumbnailWidth() /*-{
		return Number(this.tbWidth);
	}-*/;

	public native String getTitle() /*-{
		return this.title;
	}-*/;

	public native String getTitleNoFormatting() /*-{
		return this.titleNoFormatting;
	}-*/;

	public native String getUnescapedUrl() /*-{
		return this.unescapedUrl;
	}-*/;

	public native String getUrl() /*-{
		return this.url;
	}-*/;

	public native String getVisibleUrl() /*-{
		return this.visibleUrl;
	}-*/;

	public native int getWidth() /*-{
		return Number(this.width);
	}-*/;
}