package mybox.to;

import mybox.model.User;

public class MoveParams extends BulkParams {

	protected String[] toPaths;
	
	public MoveParams() {
	}
	
	public MoveParams(User user, String root, String[] fromPaths, String[] toPaths) {
		this.user = user;
		this.root = root;
		this.paths = fromPaths;
		this.toPaths = toPaths;
	}

	public String[] getToPaths() {
		return toPaths;
	}

	public void setToPaths(String[] toPaths) {
		this.toPaths= toPaths;
	}
}
