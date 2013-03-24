package mybox.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import mybox.common.to.Space;
import mybox.dropbox.model.FileEntry;
import mybox.dropbox.model.MetadataEntry;
import mybox.rest.JsonConverter;
import mybox.rest.RestResponse;
import mybox.util.HttpUtil;

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
		log.debug("Get header {}:\n{}", header, metadataStr);
		MetadataEntry metadataEntry = JsonConverter.fromJson(metadataStr, MetadataEntry.class);
		customEntry(metadataEntry);
		fileEntry.setMetadata(metadataEntry);
		fileEntry.setFileSize(metadataEntry.getBytes());

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
	
	protected void customEntries(Space space, MetadataEntry folderEntry) {
		if (folderEntry == null) {
			return;
		}
		
		String path = folderEntry.getPath();
		if (space.getRoot().equals(path)) {
			folderEntry.setId(HttpUtil.encodeUrl(path));
			folderEntry.setName(space.getName());
			folderEntry.setLocation("");
		} else {
			customEntry(folderEntry);
		}
		
		List<MetadataEntry> entries = folderEntry.getContents();
		if (entries == null || entries.size() <= 0) {
			return;
		}
		customEntries(entries);
	}
	
	protected void customEntries(List<MetadataEntry> entries) {
		for (MetadataEntry entry: entries) {
			customEntry(entry);
		}
	}
	
	protected void customEntry(MetadataEntry entry) {
		String path = entry.getPath();
		if (path == null || path.equals("")) {
			log.warn("Path is empty!");
			return;
		}
		
		String id = HttpUtil.encodeUrl(path);
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
