package mybox.model;

import java.io.Serializable;

public class User implements Serializable {

	protected Token token;

	protected AccountInfo accountInfo;

	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("token={").append(token).append("}");
		buf.append(", accountInfo={").append(accountInfo).append("}");
		return buf.toString();
	}

	public String getId() {
		return accountInfo != null ? accountInfo.getUid().toString() : null;
	}

	public String getName() {
		return accountInfo != null ? accountInfo.getDisplayName() : null;
	}

	public String getAccessToken() {
		return token != null ? token.getAccessToken() : null;
	}

	public Token getToken() {
		return token;
	}

	public void setToken(Token token) {
		this.token = token;
	}

	public AccountInfo getAccountInfo() {
		return accountInfo;
	}

	public void setAccountInfo(AccountInfo accountInfo) {
		this.accountInfo = accountInfo;
	}
}
