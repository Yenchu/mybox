package mybox.common.to;

public class CopyParams extends MoveParams {

	public CopyParams() {
	}
	
	public CopyParams(User user, String root, String[] fromPaths, String[] toPaths) {
		this.user = user;
		this.root = root;
		this.paths = fromPaths;
		this.toPaths = toPaths;
	}
}
