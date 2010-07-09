package mindnotes.shared.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath(value = "userinfo")
public interface UserInfoService extends RemoteService {

	public UserInfo getUserInfo();

}
