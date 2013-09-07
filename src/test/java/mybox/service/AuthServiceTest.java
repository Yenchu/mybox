package mybox.service;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import mybox.SpringUnitTest;
import mybox.config.SystemProp;

public class AuthServiceTest extends SpringUnitTest {

	private static final Logger log = LoggerFactory.getLogger(AuthServiceTest.class);
	
	@Autowired
	private SystemProp systemProp;
	
	@Test
	public void test() {
		String uri = systemProp.getDropboxOauth2RedirectUri();
		log.debug("uri: {}", uri);
	}
}
