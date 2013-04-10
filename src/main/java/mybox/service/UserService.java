package mybox.service;

import java.util.List;

import mybox.model.filecruiser.FileCruiserUser;
import mybox.model.keystone.User;

public interface UserService {

	public List<User> getUsers(FileCruiserUser user);
	
	public User getUser(FileCruiserUser user);
	
	public User getUser(String userId);
	
}