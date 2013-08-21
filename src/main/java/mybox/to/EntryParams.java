package mybox.to;

import mybox.model.User;

public class EntryParams extends PathParams {
	
	protected String rev;
	
	public EntryParams() {
	}
	
	public EntryParams(User user, String path) {
		super(user, path);
	}

	public String getRev() {
		return rev;
	}

	public void setRev(String rev) {
		this.rev = rev;
	}
}
