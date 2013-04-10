package mybox.to;

import java.util.ArrayList;
import java.util.List;

import mybox.model.User;

public class CreateParams extends PathParams {

	public CreateParams() {
	}

	public CreateParams(String root, String path) {
		this.root = root;
		this.path = path;
	}
	
	public CreateParams(User user, String root, String path) {
		this.user = user;
		this.root = root;
		this.path = path;
	}

	public List<String> getParamList() {
		List<String> params = new ArrayList<String>();
		params.add("path");
		params.add(path);
		if (root != null && !"".equals(root)) {
			params.add("root");
			params.add(root);
		}
		if (locale != null && !"".equals(locale)) {
			params.add("locale");
			params.add(locale);
		}
		return params;
	}
}
