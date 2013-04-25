package mybox.to;

import mybox.model.User;

public class LinkParams extends PathParams {
	
	private boolean shortUrl = true; //default
	
	// extra params for File Cruiser
	private String expires;
	
	private String password;
	
	private String emails;
	
	private String message;

	public LinkParams() {
	}
	
	public LinkParams(String root, String path) {
		super(root, path);
	}
	
	public LinkParams(User user, String root, String path) {
		super(user, root, path);
	}

	public boolean isShortUrl() {
		return shortUrl;
	}

	public void setShortUrl(boolean shortUrl) {
		this.shortUrl = shortUrl;
	}

	public String getExpires() {
		return expires;
	}

	public void setExpires(String expires) {
		this.expires = expires;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmails() {
		return emails;
	}

	public void setEmails(String emails) {
		this.emails = emails;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
