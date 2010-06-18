package mindnotes.client.ui;

import mindnotes.client.presentation.MindMapView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

public class MindNotesUI extends Composite implements RequiresResize, ProvidesResize {

	private static MindNotesUIUiBinder uiBinder = GWT
			.create(MindNotesUIUiBinder.class);
	
	interface MindNotesUIUiBinder extends UiBinder<Widget, MindNotesUI> {
	}
	
	@UiField MindMapWidget mindMapWidget;
	@UiField DockLayoutPanel dockLayoutPanel;

	public MindNotesUI() {
		
		Event.addNativePreviewHandler(new NativePreviewHandler() {
			
			@Override
			public void onPreviewNativeEvent(NativePreviewEvent event) {
				if (event.getTypeInt() == Event.ONKEYPRESS)  {
					boolean meta = Window.Navigator.getPlatform().equals("MacIntel")?
							event.getNativeEvent().getMetaKey():
							event.getNativeEvent().getCtrlKey();
					
					if (event.getNativeEvent().getKeyCode() == 'z' && meta) {
						DialogBox box = new DialogBox(true, false);
						box.add(new HTML("Undo is not supported yet"));
						box.show();
					}
				}
				
			}
		});
		
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public MindMapView getMindMapView() {
		return mindMapWidget;
	}

	@Override
	public void onResize() {
		dockLayoutPanel.onResize();		
	}

}
