package mybox.to;

import mybox.model.MetadataEntry;

public class FileOperationResponse {

	private String error;
	
	private String name;
	
	private MetadataEntry metadata;

	public FileOperationResponse() {
	}
	
	public FileOperationResponse(String name) {
		this.name = name;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public MetadataEntry getMetadata() {
		return metadata;
	}

	public void setMetadata(MetadataEntry metadata) {
		this.metadata = metadata;
	}
}
