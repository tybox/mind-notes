package mindnotes.shared.services;

import com.google.gwt.user.client.rpc.IsSerializable;

public class UserInfo implements IsSerializable {

	private String _email;
	private String _userId;
	private String _nickname;
	private String _logoutURL;
	private String _loginURL;

	public String getEmail() {
		return _email;
	}

	public void setEmail(String email) {
		_email = email;
	}

	public void setNickname(String nickname) {
		_nickname = nickname;
	}

	public String getNickname() {
		return _nickname;
	}

	public void setLogoutURL(String logoutURL) {
		_logoutURL = logoutURL;
	}

	public String getLogoutURL() {
		return _logoutURL;
	}

	public void setUserId(String userId) {
		_userId = userId;
	}

	public String getUserId() {
		return _userId;
	}

	public void setLoginURL(String loginURL) {
		_loginURL = loginURL;
	}

	public String getLoginURL() {
		return _loginURL;
	}

	public boolean isLoggedIn() {
		return _userId != null;
	}
}
