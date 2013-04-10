package mybox.service;

import java.util.List;

import mybox.model.filecruiser.SharedFile;
import mybox.model.filecruiser.SharingFile;
import mybox.to.Params;
import mybox.to.PathParams;

public interface FileCruiserService extends FileService {

	public SharedFile share(Params params, SharingFile sharingFile);
	
	public List<SharedFile> getShares(PathParams params);
	
}
