package mybox.dropbox.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class MetadataEntry extends Entry {
	
	// location isn't provided by Dropbox
	protected String location;

	protected String size;

	protected long bytes;

	@SerializedName("is_dir")
	protected boolean isDir;

	protected String hash;

	protected String modified;

	@SerializedName("client_mtime")
	protected String clientMtime;

	@SerializedName("mime_type")
	protected String mimeType;

	@SerializedName("is_deleted")
	protected boolean isDeleted;

	protected String rev;
	
	protected long revision;

	protected String icon;

	@SerializedName("thumb_exists")
	protected boolean thumbExists;

	protected List<MetadataEntry> contents;
	
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("path=").append(path);
		buf.append(", root=").append(root);
		buf.append(", size=").append(size);
		buf.append(", bytes=").append(bytes);
		buf.append(", isDir=").append(isDir);
		buf.append(", hash=").append(hash);
		buf.append(", modified=").append(modified);
		buf.append(", clientMtime=").append(clientMtime);
		buf.append(", mimeType=").append(mimeType);
		buf.append(", isDeleted=").append(isDeleted);
		buf.append(", rev=").append(rev);
		buf.append(", icon=").append(icon);
		buf.append(", thumbExists=").append(thumbExists);
		buf.append(", contents=").append(contents);
		return buf.toString();
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public long getBytes() {
		return bytes;
	}

	public void setBytes(long bytes) {
		this.bytes = bytes;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public boolean getIsDir() {
		return isDir;
	}

	public void setIsDir(boolean isDir) {
		this.isDir = isDir;
	}

	public String getModified() {
		return modified;
	}

	public void setModified(String modified) {
		this.modified = modified;
	}

	public String getClientMtime() {
		return clientMtime;
	}

	public void setClientMtime(String clientMtime) {
		this.clientMtime = clientMtime;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getRev() {
		return rev;
	}

	public void setRev(String rev) {
		this.rev = rev;
	}

	public long getRevision() {
		return revision;
	}

	public void setRevision(long revision) {
		this.revision = revision;
	}

	public boolean isThumbExists() {
		return thumbExists;
	}

	public void setThumbExists(boolean thumbExists) {
		this.thumbExists = thumbExists;
	}

	public boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public List<MetadataEntry> getContents() {
		return contents;
	}

	public void setContents(List<MetadataEntry> contents) {
		this.contents = contents;
	}
}
