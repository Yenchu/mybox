package mybox.to;

import java.io.InputStream;

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
