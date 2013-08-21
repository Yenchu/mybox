package mybox.to;

import mybox.model.User;

public class ChunkedUploadParams extends UploadParams {

	public static final int DEFAULT_CHUNK_SIZE = 4 * 1024 * 1024; // 4 MB

	private int chunkSize;

	public ChunkedUploadParams() {
		this.chunkSize = DEFAULT_CHUNK_SIZE;
	}
	
	public ChunkedUploadParams(int chunkSize) {
		this.chunkSize = chunkSize;
	}
	
	public ChunkedUploadParams(User user, String path) {
		super(user, path);
		this.chunkSize = DEFAULT_CHUNK_SIZE;
	}
	
	public int getChunkSize() {
		return chunkSize;
	}

	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}
}
