package mybox.model;

import mybox.backend.dropbox.DropboxUtil;

public class ChunkedUploadParams extends UploadParams {

	private int chunkSize;

	public ChunkedUploadParams() {
		this.chunkSize = DropboxUtil.DEFAULT_CHUNK_SIZE;
	}
	
	public ChunkedUploadParams(int chunkSize) {
		this.chunkSize = chunkSize;
	}
	
	public ChunkedUploadParams(User user, String root, String path) {
		super(user, root, path);
		this.chunkSize = DropboxUtil.DEFAULT_CHUNK_SIZE;
	}
	
	public int getChunkSize() {
		return chunkSize;
	}

	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}
}
