package mybox.model;

import java.util.ArrayList;
import java.util.List;

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
	
	public List<String> getParamList() {
		List<String> params = new ArrayList<String>();
		params.add("list");
		params.add(String.valueOf(list));
		if (list) {
			params.add("include_deleted");
			params.add(String.valueOf(includeDeleted));
		}
		
		if (fileLimit <= 0 || fileLimit > METADATA_DEFAULT_LIMIT) {
			fileLimit = METADATA_DEFAULT_LIMIT;
		}
		params.add("file_limit");
		params.add(String.valueOf(fileLimit));
		
		if (hash != null && !"".equals(hash)) {
			params.add("hash");
			params.add(hash);
		}
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
