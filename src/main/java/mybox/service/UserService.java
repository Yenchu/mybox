package mybox.service;

import java.util.List;

import mybox.model.filecruiser.FileCruiserUser;
import mybox.model.keystone.Project;
import mybox.model.keystone.User;
import mybox.to.LoginParams;
import mybox.to.Params;

public interface UserService {

	public FileCruiserUser auth(LoginParams params);
	
	public FileCruiserUser validate(String token);
	
	public List<User> getUsers();
	
	public User getUser(String userId);
	
	public Project getProject(Params params, String spaceId);
	
}