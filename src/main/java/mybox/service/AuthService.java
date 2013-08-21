package mybox.service;

import mybox.model.AccountInfo;
import mybox.model.Token;
import mybox.model.User;

public interface AuthService {

	public boolean isLogin(User user);

	public String getAuthorizingUrl();

	public Token getToken(String code);
	
	public AccountInfo getAccountInfo(String accessToken);
	
	public User getUser(Token token);

}