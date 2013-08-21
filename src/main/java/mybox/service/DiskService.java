package mybox.service;

import mybox.model.MetadataEntry;
import mybox.to.Page;

import org.springframework.data.domain.Pageable;

public interface DiskService {
	
	public Page<MetadataEntry> getFiles(String folderPathStr, Pageable pageable);
	
}