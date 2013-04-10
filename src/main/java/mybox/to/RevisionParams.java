package mybox.to;

import java.util.ArrayList;
import java.util.List;

import mybox.model.User;

public class RevisionParams extends PathParams {

	private String revLimit;

	public RevisionParams() {
	}
	
	public RevisionParams(String root, String path) {
		super(root, path);
	}
	
	public RevisionParams(User user, String root, String path) {
		super(user, root, path);
	}
	
	public List<String> getParamList() {
		List<String> params = new ArrayList<String>();
		if (revLimit != null && !"".equals(revLimit)) {
			params.add("rev_limit");
			params.add(revLimit);
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
	
	public String getRevLimit() {
		return revLimit;
	}

	public void setRevLimit(String revLimit) {
		this.revLimit = revLimit;
	}
}
