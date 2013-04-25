package mybox.to;

import mybox.model.User;

public class EntryParams extends PathParams {
	
	protected String rev;
	
	public EntryParams() {
	}
	
	public EntryParams(String root, String path) {
		super(root, path);
	}
	
	public EntryParams(User user, String root, String path) {
		super(user, root, path);
	}

	public String getRev() {
		return rev;
	}

	public void setRev(String rev) {
		this.rev = rev;
	}
}
