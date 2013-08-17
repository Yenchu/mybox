package mybox.service;

import mybox.model.User;

public interface AuthService {

	public boolean isLogin(String serviceType, User user);

	public String getAuthUrl(String serviceType);

}