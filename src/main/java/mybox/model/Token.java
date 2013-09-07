package mybox.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class Token implements Serializable {

	private String uid;
    
	@SerializedName("access_token")
	private String accessToken;
	
	@SerializedName("token_type")
	private String tokenType;
	
    public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("uid=").append(uid);
		buf.append(", accessToken=").append(accessToken);
		buf.append(", tokenType=").append(tokenType);
		return buf.toString();
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}
}
