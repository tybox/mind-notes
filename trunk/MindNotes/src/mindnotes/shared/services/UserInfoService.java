package mindnotes.shared.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath(value = "network/userinfo")
public interface UserInfoService extends RemoteService {

	public UserInfo getUserInfo();

}
