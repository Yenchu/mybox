package mybox.to;

import java.util.ArrayList;
import java.util.List;

import mybox.model.User;

public class PathParams extends Params {
	
	protected String root;

	protected String path;

	public PathParams() {
	}

	public PathParams(String root, String path) {
		this.root = root;
		this.path = path;
	}
	
	public PathParams(User user, String root, String path) {
		this.user = user;
		this.root = root;
		this.path = path;
	}

	public List<String> getParamList() {
		List<String> params = new ArrayList<String>();
		if (locale != null && !"".equals(locale)) {
			params.add("locale");
			params.add(locale);
		}
		return params;
	}
	
	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}