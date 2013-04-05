package mybox.service;

import static org.junit.Assert.assertNotNull;
import mybox.SpringUnitTest;
import mybox.model.LoginParams;
import mybox.model.Params;
import mybox.model.Space;
import mybox.model.User;
import mybox.model.filecruiser.FileCruiserUser;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class FileCruiserServiceTest extends SpringUnitTest {

	private static final Logger log = LoggerFactory.getLogger(FileCruiserServiceTest.class);
	
	@Autowired
	private FileCruiserService fileCruiserService;
	
	protected User getUser() {
		User user = new User();
		user.setName("Andrew");
		return user;
	}
	
	@Test
	public void auth() {
		String domainName = "cs.promise.com.tw";
		String username = "Sabi.Smith";
		String password = "Password1";
		LoginParams params = new LoginParams(domainName, username, password);

		FileCruiserUser user = (FileCruiserUser) fileCruiserService.auth(params);
		assertNotNull("User " + username + " login failed!", user.getId());
		log.debug("user: {}", user);
		//user: id=002d76d4cd32466baa52513fbd52f55f, name=Sabi.Smith, ip=null, domainId=b4af29d059744a5ab47f31024976a6e2, domainName=cs.promise.com.tw, token=16afbbda595a4a6992527b95a231b10a, expiresAt=Tue Apr 02 14:47:00 CST 2013
	}
	
	@Test
	public void getSpace() {
		String id = "002d76d4cd32466baa52513fbd52f55f";
		String token = "16afbbda595a4a6992527b95a231b10a";
		String spaceId = null;
		
		FileCruiserUser user = new FileCruiserUser();
		user.setId(id);
		user.setToken(token);
		Params params = new Params();
		params.setUser(user);
		Space space = fileCruiserService.getSpace(params, spaceId);
		assertNotNull("There must be a default space!", space.getId());
		log.debug("space: {}", space);
	}
}
