package mybox.to;

import java.util.ArrayList;
import java.util.List;

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
	
	public List<String> getParamList() {
		List<String> params = new ArrayList<String>();
		if (rev != null && !"".equals(rev)) {
			params.add("rev");
			params.add(rev);
		}
		if (locale != null && !"".equals(locale)) {
			params.add("locale");
			params.add(locale);
		}
		return params;
	}

	public String[] getParamArray() {
		List<String> params = getParamList();
		return params.toArray(new String[params.size()]);
	}

	public String getRev() {
		return rev;
	}

	public void setRev(String rev) {
		this.rev = rev;
	}
}
