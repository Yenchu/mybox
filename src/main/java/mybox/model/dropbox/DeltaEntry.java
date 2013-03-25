package mybox.model.dropbox;

public class DeltaEntry<MD> {
	
	//for deleted entry, metadata is null, eg: lcPath=/costa rican frog.jpg, metadata=null
	
	private String lcPath;
	
	private MD metadata;

	public DeltaEntry() {
	}
	
    public DeltaEntry(String lcPath, MD metadata) {
        this.lcPath = lcPath;
        this.metadata = metadata;
    }
    
    public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("lcPath=").append(lcPath);
		buf.append(", metadata=").append(metadata);
		return buf.toString();
	}
    
	public String getLcPath() {
		return lcPath;
	}

	public void setLcPath(String lcPath) {
		this.lcPath = lcPath;
	}

	public MD getMetadata() {
		return metadata;
	}

	public void setMetadata(MD metadata) {
		this.metadata = metadata;
	}
}
