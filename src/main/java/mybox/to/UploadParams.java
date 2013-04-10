package mybox.to;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import mybox.model.User;

public class UploadParams extends PathParams {
	
	protected InputStream content;
	
	protected long length;
	
	protected boolean overwrite = true; // default
	
	protected String parentRev;
	
	public UploadParams() {
	}
	
	public UploadParams(String root, String path) {
		super(root, path);
	}
	
	public UploadParams(User user, String root, String path) {
		super(user, root, path);
	}
	
	public List<String> getParamList() {
		List<String> params = new ArrayList<String>();
		if (!overwrite) {
			params.add("overwrite");
			params.add("false");
		}

		if (parentRev != null && !"".equals(parentRev)) {
			params.add("parent_rev");
			params.add(parentRev);
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

	public InputStream getContent() {
		return content;
	}

	public void setContent(InputStream is) {
		this.content = is;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public boolean isOverwrite() {
		return overwrite;
	}

	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}

	public String getParentRev() {
		return parentRev;
	}

	public void setParentRev(String parentRev) {
		this.parentRev = parentRev;
	}
}
