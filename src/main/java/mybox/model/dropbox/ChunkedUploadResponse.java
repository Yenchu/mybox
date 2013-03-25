package mybox.model.dropbox;

import com.google.gson.annotations.SerializedName;

public class ChunkedUploadResponse {

	@SerializedName("upload_id")
	private String uploadId;
	
	private long offset;
	
	private String expires;
	
	private String error;

	public String getUploadId() {
		return uploadId;
	}

	public void setUploadId(String uploadId) {
		this.uploadId = uploadId;
	}

	public long getOffset() {
		return offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public String getExpires() {
		return expires;
	}

	public void setExpires(String expires) {
		this.expires = expires;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
}
