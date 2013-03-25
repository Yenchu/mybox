package mybox.model;

import java.util.ArrayList;
import java.util.List;

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
	
	public List<List<String>> getParamList() {
		if (paths == null || paths.length < 1) {
			return null;
		}
		
		List<List<String>> paramsList = new ArrayList<List<String>>();
		for (int i = 0, size = paths.length; i < size; i++) {
			String fromPath = paths[i];
			String toPath = toPaths[i];
			
			List<String> params = new ArrayList<String>();
			params.add("from_path");
			params.add(fromPath);
			params.add("to_path");
			params.add(toPath);
			
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

	public String[] getToPaths() {
		return toPaths;
	}

	public void setToPaths(String[] toPaths) {
		this.toPaths= toPaths;
	}
}
