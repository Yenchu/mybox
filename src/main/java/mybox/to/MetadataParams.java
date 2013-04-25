package mybox.to;

import mybox.model.User;

public class MetadataParams extends EntryParams {

	public static final int METADATA_DEFAULT_LIMIT = 25000;
	
	private String hash;

	private boolean list;

	private boolean includeDeleted;

	private int fileLimit;
	
	public MetadataParams() {
	}
	
	public MetadataParams(String root, String path) {
		super(root, path);
	}
	
	public MetadataParams(User user, String root, String path) {
		super(user, root, path);
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public boolean isList() {
		return list;
	}

	public void setList(boolean list) {
		this.list = list;
	}

	public boolean isIncludeDeleted() {
		return includeDeleted;
	}

	public void setIncludeDeleted(boolean includeDeleted) {
		this.includeDeleted = includeDeleted;
	}

	public int getFileLimit() {
		return fileLimit;
	}

	public void setFileLimit(int fileLimit) {
		this.fileLimit = fileLimit;
	}
}
