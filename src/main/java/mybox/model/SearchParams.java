package mybox.model;

import java.util.ArrayList;
import java.util.List;

public class SearchParams extends PathParams {
	
	public static final int SEARCH_DEFAULT_LIMIT = 1000;

	private String query;
	
	private int fileLimit;
	
	private boolean includeDeleted;
	
	public SearchParams() {
	}
	
	public SearchParams(String root, String path) {
		super(root, path);
	}
	
	public SearchParams(User user, String root, String path) {
		super(user, root, path);
	}
	
	public List<String> getParamList() {
		List<String> params = new ArrayList<String>();
		if (query != null && !"".equals(query)) {
			params.add("query");
			params.add(query);
		}
		
		if (fileLimit <= 0 || fileLimit > SEARCH_DEFAULT_LIMIT) {
			fileLimit = SEARCH_DEFAULT_LIMIT;
		}
		params.add("file_limit");
		params.add(String.valueOf(fileLimit));

		if (includeDeleted) {
			params.add("include_deleted");
			params.add(String.valueOf(includeDeleted));
		}
		
		if (locale != null && !"".equals(locale)) {
			params.add("locale");
			params.add(locale);
		}
		return params;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String[] getParamArray() {
		List<String> params = getParamList();
		return params.toArray(new String[params.size()]);
	}

	public int getFileLimit() {
		return fileLimit;
	}

	public void setFileLimit(int fileLimit) {
		this.fileLimit = fileLimit;
	}

	public boolean isIncludeDeleted() {
		return includeDeleted;
	}

	public void setIncludeDeleted(boolean includeDeleted) {
		this.includeDeleted = includeDeleted;
	}
}
