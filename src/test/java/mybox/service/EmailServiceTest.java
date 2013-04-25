package mybox.service;

import mybox.SpringUnitTest;
import mybox.to.LinkParams;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class EmailServiceTest extends SpringUnitTest {

	private static final Logger log = LoggerFactory.getLogger(EmailServiceTest.class);
	
	@Autowired
	private EmailService EmailService;
	
	@Test
	public void sendEmail() {
		String subject = "Test email";
		String toEmails = "andrew.chen@tw.promise.com";
		String fromEmail = "andrew.chen@tw.promise.com";
		String templateLocation = "velocity/link.vm";
		String path = "/tomcat.png";
		LinkParams params = new LinkParams();
		params.setPath(path);
		params.setEmails(toEmails);
		EmailService.send(subject, toEmails, fromEmail, templateLocation, params);
	}
}
