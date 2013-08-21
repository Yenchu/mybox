package mybox.to;

import mybox.model.User;

public class LinkParams extends PathParams {
	
	private boolean shortUrl = true; //default

	public LinkParams() {
	}
	
	public LinkParams(User user, String path) {
		super(user, path);
	}

	public boolean isShortUrl() {
		return shortUrl;
	}

	public void setShortUrl(boolean shortUrl) {
		this.shortUrl = shortUrl;
	}
}
