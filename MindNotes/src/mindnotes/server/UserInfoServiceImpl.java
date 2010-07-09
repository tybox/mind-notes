package mindnotes.server;

import mindnotes.shared.services.UserInfo;
import mindnotes.shared.services.UserInfoService;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class UserInfoServiceImpl extends RemoteServiceServlet implements
		UserInfoService {

	@Override
	public UserInfo getUserInfo() {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if (user == null)
			throw new RuntimeException("user not logged in");
		UserInfo info = new UserInfo();
		info.setLogoutURL(userService.createLogoutURL("/MindNotes.html"));

		info.setEmail(user.getEmail());
		info.setNickname(user.getNickname());
		info.setUserId(user.getUserId());
		return info;
	}
}
