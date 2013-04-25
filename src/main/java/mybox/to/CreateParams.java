package mybox.to;

import mybox.model.User;

public class CreateParams extends PathParams {

	public CreateParams() {
	}

	public CreateParams(String root, String path) {
		this.root = root;
		this.path = path;
	}
	
	public CreateParams(User user, String root, String path) {
		this.user = user;
		this.root = root;
		this.path = path;
	}
}
