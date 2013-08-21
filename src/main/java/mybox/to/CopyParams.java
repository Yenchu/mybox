package mybox.to;

import mybox.model.User;

public class CopyParams extends MoveParams {

	public CopyParams() {
	}
	
	public CopyParams(User user, String[] fromPaths, String[] toPaths) {
		this.user = user;
		this.paths = fromPaths;
		this.toPaths = toPaths;
	}
}
