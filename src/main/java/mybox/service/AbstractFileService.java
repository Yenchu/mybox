package mybox.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import mybox.json.JsonConverter;
import mybox.model.FileEntry;
import mybox.model.MetadataEntry;
import mybox.model.Space;
import mybox.rest.RestResponse;
import mybox.util.UrlEncodeUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractFileService {

	private static final Logger log = LoggerFactory.getLogger(AbstractFileService.class);

	protected List<MetadataEntry> getFolders(List<MetadataEntry> entries) {
		List<MetadataEntry> folderEntries = new ArrayList<MetadataEntry>();
		for (MetadataEntry entry : entries) {
			if (entry.getIsDir()) {
				folderEntries.add(entry);
			}
		}
		return folderEntries;
	}
	
	protected FileEntry convertDownloadResponse(RestResponse<InputStream> restResponse, String header) {
		InputStream content = restResponse.getBody();
		FileEntry fileEntry = new FileEntry();
		fileEntry.setContent(content);
		
		String metadataStr = restResponse.getHeader(header);
		if (metadataStr != null && !"".equals(metadataStr)) {
			log.debug("Get header {}:\n{}", header, metadataStr);
			MetadataEntry metadataEntry = JsonConverter.fromJson(metadataStr, MetadataEntry.class);
			customMetadata(metadataEntry);
			fileEntry.setMetadata(metadataEntry);
			fileEntry.setFileSize(metadataEntry.getBytes());
		}

		String contentType = restResponse.getHeader("Content-Type");
		if (contentType != null && !"".equals(contentType)) {
			String[] splits = contentType.split(";");
			if (splits.length > 0) {
				String mimeType = splits[0].trim();
				fileEntry.setMimeType(mimeType);
			}
			if (splits.length > 1) {
				splits = splits[1].split("=");
				if (splits.length > 1) {
					String charset = splits[1].trim();
					fileEntry.setCharset(charset);
				}
			}
		}
		return fileEntry;
	}
	
	protected void customFolderMetadata(Space space, MetadataEntry folderEntry) {
		if (folderEntry == null) {
			return;
		}
		
		String path = folderEntry.getPath();
		if (space.getRoot().equals(path)) {
			folderEntry.setId(UrlEncodeUtil.encode(path));
			folderEntry.setName(space.getName());
			folderEntry.setLocation("");
		} else {
			customMetadata(folderEntry);
		}
		
		List<MetadataEntry> entries = folderEntry.getContents();
		if (entries == null || entries.size() <= 0) {
			return;
		}
		customMetadata(entries);
	}
	
	protected void customMetadata(List<MetadataEntry> entries) {
		for (MetadataEntry entry: entries) {
			customMetadata(entry);
		}
	}
	
	protected void customMetadata(MetadataEntry entry) {
		//{"hash": "c89bb0d81ea153f4c3c25be81c4e245f", "bytes": 0, "thumb_exists": false, "path": "/", "is_dir": true, 
		//"icon": "folder_public", "rev": "c89bb0d81ea153f4c3c25be81c4e245f", "modified": "Mon, 01 Apr 2013 23:42:29 +0000", "size": "0 Bytes", "root": "File Cruiser" 
		//"contents": [{"size": "1.0 MB", "store_size": "1.0 MB", "encrypt": false, "rev": "303b7c009b01907f563749361efe2e6c", "thumb_exists": false, 
		//"bytes": 1048576, "modified": "Tue, 02 Apr 2013 06:42:29 +0000", "store_bytes": 1048576, "path": "/size1.txt", "is_dir": false, "icon": "page_white_acrobat", "root": "File Cruiser", "compress": false}]}

		String path = entry.getPath();
		if (path == null || path.equals("")) {
			log.warn("Path is empty!");
			return;
		}
		
		String id = UrlEncodeUtil.encode(path);
		entry.setId(id);

		int idx = path.lastIndexOf('/');
		String location = null;
		String name = null;
		if (idx > 0) {
			location = path.substring(0, idx);
			name = path.substring(idx + 1);
		} else if (idx == 0) {
			location = "/";
			name = path.substring(1);
		} else {
			location = "";
			name = path;
		}
		entry.setLocation(location);
		entry.setName(name);
	}
}
