package mybox.service;

public interface EmailService {

	public void setMailSender(String host, int port, String username, String password);

	public void send(String subject, String toEmails, String fromEmail, String templateLocation, Object model);

}