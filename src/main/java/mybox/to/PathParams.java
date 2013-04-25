package mybox.to;

import mybox.model.User;

public class PathParams extends Params {
	
	protected String root;

	protected String path;

	public PathParams() {
	}

	public PathParams(String root, String path) {
		this.root = root;
		this.path = path;
	}
	
	public PathParams(User user, String root, String path) {
		this.user = user;
		this.root = root;
		this.path = path;
	}
	
	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}