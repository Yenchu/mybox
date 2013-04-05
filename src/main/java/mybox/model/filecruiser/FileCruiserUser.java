package mybox.model.filecruiser;

import java.util.Date;

import mybox.model.User;

public class FileCruiserUser extends User {

	private String domainId;
	
	private String domainName;

	private String token;
	
	private Date expiresAt;
	
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append(super.toString());
		buf.append(", domainId=").append(domainId);
		buf.append(", domainName=").append(domainName);
		buf.append(", token=").append(token);
		buf.append(", expiresAt=").append(expiresAt);
		return buf.toString();
	}

	public String getDomainId() {
		return domainId;
	}

	public void setDomainId(String domainId) {
		this.domainId = domainId;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Date getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(Date expiresAt) {
		this.expiresAt = expiresAt;
	}
}
