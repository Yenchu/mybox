package mybox.common.to;

public class DeleteParams extends BulkParams {

	public DeleteParams() {
	}
	
	public DeleteParams(User user, String root, String[] paths) {
		this.user = user;
		this.root = root;
		this.paths = paths;
	}
}
