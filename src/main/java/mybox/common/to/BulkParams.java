package mybox.common.to;

import java.util.ArrayList;
import java.util.List;

public class BulkParams extends Params {
	
	protected String root;

	protected String[] paths;
	
	public List<List<String>> getParamList() {
		if (paths == null || paths.length < 1) {
			return null;
		}
		
		List<List<String>> paramsList = new ArrayList<List<String>>();
		for (String path: paths) {
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
			paramsList.add(params);
		}
		return paramsList;
	}
	
	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	public String[] getPaths() {
		return paths;
	}

	public void setPaths(String[] paths) {
		this.paths = paths;
	}
}
