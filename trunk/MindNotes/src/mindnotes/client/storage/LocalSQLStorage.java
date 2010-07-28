package mindnotes.client.storage;

import java.util.List;

import mindnotes.shared.model.MindMap;
import mindnotes.shared.model.MindMapInfo;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class LocalSQLStorage implements Storage {

	JavaScriptObject _database;
	protected AsyncCallback<Void> _callback;

	@Override
	public void getStoredMaps(final AsyncCallback<List<MindMapInfo>> callback) {
		DeferredCommand.addCommand(new Command() {

			@Override
			public void execute() {
				getStoredMapsSync(callback);

			}
		});
	}

	protected void getStoredMapsSync(AsyncCallback<List<MindMapInfo>> callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadMindMap(MindMapInfo map, AsyncCallback<MindMap> callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveMindMap(final MindMap map,
			final AsyncCallback<Void> callback) {
		DeferredCommand.addCommand(new Command() {

			@Override
			public void execute() {
				try {
					_callback = callback;
					saveMindMapSync(map);
				} catch (Throwable t) {
					callback.onFailure(t);
				}

			}
		});
	}

	public void saveMindMapSync(MindMap map) {
		if (_database == null) {
			if (!isWebSQLSupported())
				throw new RuntimeException(
						"Web SQL Storage not supported by this browser");
			prepareDatabase();
		}
		JSONMindMapBuilder jmmb = new JSONMindMapBuilder();
		map.copyTo(jmmb);
		String content = jmmb.getJSON();
		insertMap(map.getTitle(), content);
	}

	public void callbackFailure(JavaScriptObject error) {
		SQLError se = error.cast();
		String errorText = se.getMessage() + " " + se.getCode();
		_callback.onFailure(new Exception(errorText));
	}

	public void callbackSuccess() {
		_callback.onSuccess(null);

	}

	/* @formatter:off */
	private native void insertMap(String title, String content) /*-{
		@java.lang.System::out.@java.io.PrintStream::println(Ljava/lang/String;)("Tracea!");
		try {
			var db = this.@mindnotes.client.storage.LocalSQLStorage::_database;
			var l = this;
			var onerr = function(error) {
				@java.lang.System::out.@java.io.PrintStream::println(Ljava/lang/String;)("Trace3!");
					l.@mindnotes.client.storage.LocalSQLStorage::callbackFailure(Lcom/google/gwt/core/client/JavaScriptObject;)(error);
				};
			var onsuccess = function() {
					l.@mindnotes.client.storage.LocalSQLStorage::callbackSuccess();
				};
			var ontxerr = function(tx, error) {
				@java.lang.System::out.@java.io.PrintStream::println(Ljava/lang/String;)("Trace2!");
					l.@mindnotes.client.storage.LocalSQLStorage::callbackFailure(Lcom/google/gwt/core/client/JavaScriptObject;)(error);
				};
			var ontxsuccess = function(tx, r) {
			};

			db.transaction(
				function(tx) {
					try {
						tx.executeSql("insert or replace into mindmaps (title, content) values (?, ?) ", [title, content], ontxsuccess, ontxerr);
					} catch (e) {
						@java.lang.System::out.@java.io.PrintStream::println(Ljava/lang/String;)("Trace_tx!");
						this.@mindnotes.client.storage.LocalSQLStorage::callbackFailure(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
					}
				}, onerr, onsuccess);
		} catch(e) {
			@java.lang.System::out.@java.io.PrintStream::println(Ljava/lang/String;)("Trace1!");
			this.@mindnotes.client.storage.LocalSQLStorage::callbackFailure(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
		}
	}-*/;
	/* @formatter:on */

	public native boolean isWebSQLSupported() /*-{
		return !!$wnd.openDatabase;
	}-*/;

	/* @formatter:off */
	private native void prepareDatabase() /*-{
		try {
			var create = function (db) {
				var onerr = function(error) {
					@java.lang.System::out.@java.io.PrintStream::println(Ljava/lang/String;)("Tracex3!");
						l.@mindnotes.client.storage.LocalSQLStorage::callbackFailure(Lcom/google/gwt/core/client/JavaScriptObject;)(error);
					};
				var onsuccess = function() {
						l.@mindnotes.client.storage.LocalSQLStorage::callbackSuccess();
					};
				var ontxerr = function(tx, error) {
					@java.lang.System::out.@java.io.PrintStream::println(Ljava/lang/String;)("Tracex2!");
						l.@mindnotes.client.storage.LocalSQLStorage::callbackFailure(Lcom/google/gwt/core/client/JavaScriptObject;)(error);
					};
				var ontxsuccess = function(tx, r) {
				};
				db.changeVersion('', '1.0', function (t) {
					t.executeSql('create table mindmaps (title, content)', ontxsuccess, ontxerr);
				}, onerr, onsuccess);
			};
			var error = function (err) {
				throw err;
			}
			var db = $wnd.openDatabase("MindNotesStorage", "1.0", "Mind Notes Local Storage", 5*1024*1024, create, error);
			this.@mindnotes.client.storage.LocalSQLStorage::_database = db;
		} catch (err) {
			this.@mindnotes.client.storage.LocalSQLStorage::callbackFailure(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
		}
	}-*/;
	/* @formatter:on */

}
