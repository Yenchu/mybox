package mybox.to;

import mybox.model.User;

public class CreateParams extends PathParams {

	public CreateParams() {
	}
	
	public CreateParams(User user, String path) {
		this.user = user;
		this.path = path;
	}
}
