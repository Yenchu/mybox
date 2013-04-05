package mybox.model;

public class LoginParams {
	
	private String domain;

	private String username;

	private String password;
	
	private String ip;

	public LoginParams() {
	}
	
	public LoginParams(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public LoginParams(String domain, String username, String password) {
		this.domain = domain;
		this.username = username;
		this.password = password;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
}
