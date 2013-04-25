package mybox.service;

import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

@Service
public class EmailServiceImpl implements EmailService {

	private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

	@Autowired
	private VelocityEngine velocityEngine;

	private JavaMailSenderImpl mailSender;

	public EmailServiceImpl() {
		setMailSender("192.168.202.252", 0, "", "");
	}

	public void setMailSender(String host, int port, String username,
			String password) {
		if (mailSender == null) {
			mailSender = new JavaMailSenderImpl();
		}
		
		mailSender.setDefaultEncoding("UTF-8");
		mailSender.setHost(host);
		if (port > 0) {
			mailSender.setPort(port);
		}
		mailSender.setUsername(username);
		mailSender.setPassword(password);
	}

	public void send(final String subject, final String toEmails, final String fromEmail, final String templateLocation, final Object data) {
		log.info("Send email from {} to {} with message {}", fromEmail, toEmails, data);
		MimeMessagePreparator preparator = new MimeMessagePreparator() {
			public void prepare(MimeMessage mimeMessage) throws Exception {
				MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
				message.setSubject(subject);
				message.setTo(toEmails);
				message.setFrom(fromEmail);
				Map<String, Object> model = new HashMap<String, Object>();
				model.put("data", data);
				String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templateLocation, "UTF-8", model);
				message.setText(text, true);
			}
		};
		this.mailSender.send(preparator);
	}
}
