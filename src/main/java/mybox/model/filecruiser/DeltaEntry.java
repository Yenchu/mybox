package mybox.model.filecruiser;

import com.google.gson.annotations.SerializedName;

public class DeltaEntry {

	/*"action": "Upload File", 
    "delta": "Create", 
    "is_dir": false, 
    "path": "a", 
    "size": 1*/
	
	private String path;
	
	private String action;
	
	private String delta;
	
	@SerializedName("is_dir")
	protected boolean isDir;
	
	private long size;
	
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("path=").append(path);
		buf.append(", action=").append(action);
		buf.append(", delta=").append(delta);
		buf.append(", isDir=").append(isDir);
		buf.append(", size=").append(size);
		return buf.toString();
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getDelta() {
		return delta;
	}

	public void setDelta(String delta) {
		this.delta = delta;
	}

	public boolean isDir() {
		return isDir;
	}

	public void setDir(boolean isDir) {
		this.isDir = isDir;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}
}
