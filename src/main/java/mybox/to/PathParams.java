package mybox.to;

import mybox.model.User;

public class PathParams extends Params {
	
	protected String path;

	public PathParams() {
	}
	
	public PathParams(User user, String path) {
		this.user = user;
		this.path = path;
	}
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}