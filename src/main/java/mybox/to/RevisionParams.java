package mybox.to;

import mybox.model.User;

public class RevisionParams extends PathParams {

	private String revLimit;

	public RevisionParams() {
	}
	
	public RevisionParams(String root, String path) {
		super(root, path);
	}
	
	public RevisionParams(User user, String root, String path) {
		super(user, root, path);
	}
	
	public String getRevLimit() {
		return revLimit;
	}

	public void setRevLimit(String revLimit) {
		this.revLimit = revLimit;
	}
}
