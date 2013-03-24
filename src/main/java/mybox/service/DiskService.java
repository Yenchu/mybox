package mybox.service;

import mybox.dropbox.model.MetadataEntry;
import mybox.web.to.Page;

import org.springframework.data.domain.Pageable;


public interface DiskService extends FileService {
	
	public Page<MetadataEntry> getFiles(String folderPathStr, Pageable pageable);
	
}