package mybox.service;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import mybox.SpringUnitTest;
import mybox.model.filecruiser.FileCruiserUser;
import mybox.model.keystone.User;

public class UserServiceTest extends SpringUnitTest {

	private static final Logger log = LoggerFactory.getLogger(SpringUnitTest.class);
	
	@Autowired
	private UserService userService;
	
	@Test
	public void validate() {
		String token = "400e863cd5da4e9e9c6056668af7468f";
		FileCruiserUser user = userService.validate(token);
		log.debug("user: {}", user);
	}

	@Test
	public void getUsers() {
		List<User> users = userService.getUsers();
		for (User user: users) {
			log.debug("user: {}", user);
		}
	}
}
