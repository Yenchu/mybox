package mybox.model;

import java.io.InputStream;

public class FileEntry {

    private long fileSize = -1;
    
	private String mimeType = null;
    
	private String charset = null;
	
	private InputStream content;
	
	private MetadataEntry metadata;

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public InputStream getContent() {
		return content;
	}

	public void setContent(InputStream content) {
		this.content = content;
	}

	public MetadataEntry getMetadata() {
		return metadata;
	}

	public void setMetadata(MetadataEntry metadata) {
		this.metadata = metadata;
	}
}
