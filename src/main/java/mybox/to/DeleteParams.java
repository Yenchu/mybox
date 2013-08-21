package mybox.to;

import mybox.model.User;

public class DeleteParams extends BulkParams {

	public DeleteParams() {
	}
	
	public DeleteParams(User user, String[] paths) {
		this.user = user;
		this.paths = paths;
	}
}
