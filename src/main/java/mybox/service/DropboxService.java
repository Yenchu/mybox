package mybox.service;

import mybox.model.MetadataEntry;
import mybox.model.dropbox.DeltaPage;
import mybox.to.DeltaParams;

public interface DropboxService extends FileService {

	public DeltaPage<MetadataEntry> delta(DeltaParams params);
	
}