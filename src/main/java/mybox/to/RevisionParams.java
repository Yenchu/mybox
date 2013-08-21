package mybox.to;

import mybox.model.User;

public class RevisionParams extends PathParams {

	private String revLimit;

	public RevisionParams() {
	}
	
	public RevisionParams(User user, String path) {
		super(user, path);
	}
	
	public String getRevLimit() {
		return revLimit;
	}

	public void setRevLimit(String revLimit) {
		this.revLimit = revLimit;
	}
}
