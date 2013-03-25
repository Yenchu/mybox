package mybox.model.dropbox;

public class Link {

	private String url;
	
	private String expires;
	
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("url=").append(url);
		buf.append(", expires=").append(expires);
		return buf.toString();
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getExpires() {
		return expires;
	}

	public void setExpires(String expires) {
		this.expires = expires;
	}
}
