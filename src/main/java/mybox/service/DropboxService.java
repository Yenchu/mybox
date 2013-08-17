package mybox.service;

import mybox.model.AccountInfo;
import mybox.model.DropboxUser;
import mybox.model.MetadataEntry;
import mybox.model.DeltaPage;
import mybox.model.User;
import mybox.to.DeltaParams;

public interface DropboxService extends FileService {
	
	public User getToken(String code);
	
	public AccountInfo getAccountInfo(DropboxUser user);

	public DeltaPage<MetadataEntry> delta(DeltaParams params);
	
}