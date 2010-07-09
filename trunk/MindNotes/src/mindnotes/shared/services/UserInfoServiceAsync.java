package mindnotes.shared.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface UserInfoServiceAsync {

	void getUserInfo(AsyncCallback<UserInfo> callback);

}
