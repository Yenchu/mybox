package mybox.service;

import java.util.List;

import mybox.model.FileEntry;
import mybox.model.filecruiser.DeltaPage;
import mybox.model.filecruiser.SharedFile;
import mybox.model.filecruiser.SharingFile;
import mybox.to.DeltaParams;
import mybox.to.EntryParams;
import mybox.to.Params;
import mybox.to.PathParams;

public interface FileCruiserService extends FileService {
	
	public DeltaPage delta(DeltaParams params);
	
	public FileEntry getLink(EntryParams params);

	public SharedFile share(Params params, SharingFile sharingFile);
	
	public List<SharedFile> getShares(PathParams params);
	
}
