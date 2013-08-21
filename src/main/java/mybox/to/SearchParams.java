package mybox.to;

import mybox.model.User;

public class SearchParams extends PathParams {
	
	public static final int SEARCH_DEFAULT_LIMIT = 1000;

	private String query;
	
	private int fileLimit;
	
	private boolean includeDeleted;
	
	public SearchParams() {
	}
	
	public SearchParams(User user, String path) {
		super(user, path);
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
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
