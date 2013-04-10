package mybox.service;

import java.util.List;

import mybox.backend.filecruiser.Resource;
import mybox.model.filecruiser.FileCruiserUser;
import mybox.model.keystone.User;
import mybox.model.keystone.Users;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends AbstractBackendService implements UserService {

	private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
	
	public List<User> getUsers(FileCruiserUser user) {
		String token = user.getToken();
		String url = getUserUrl(Resource.USERS);
		Users users = this.get(url, token, Users.class, false);
		List<User> userList = users.getUsers();
		return userList;
	}
	
	public User getUser(FileCruiserUser user) {
		String token = user.getToken();
		String userId = user.getId();
		String resource = buildPath(Resource.USERS, userId);
		String url = getUserUrl(resource);
		User rtUser = this.get(url, token, User.class, true);
		return rtUser;
	}
	
	public User getUser(String userId) {
		String resource = buildPath(Resource.USERS, userId);
		String url = getUserUrl(resource);
		User rtUser = this.get(url, getAdminToken(), User.class, true);
		return rtUser;
	}
}
