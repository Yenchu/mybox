package mybox.service;

import static mybox.util.DropboxUtil.getAuthHeaders;
import static mybox.util.DropboxUtil.getCopyUrl;
import static mybox.util.DropboxUtil.getCreateFolderUrl;
import static mybox.util.DropboxUtil.getDeleteUrl;
import static mybox.util.DropboxUtil.getDeltaUrl;
import static mybox.util.DropboxUtil.getFilePostUrl;
import static mybox.util.DropboxUtil.getFilePutUrl;
import static mybox.util.DropboxUtil.getFileUrl;
import static mybox.util.DropboxUtil.getMediaUrl;
import static mybox.util.DropboxUtil.getMetadataUrl;
import static mybox.util.DropboxUtil.getMoveUrl;
import static mybox.util.DropboxUtil.getRestoreUrl;
import static mybox.util.DropboxUtil.getRevisionUrl;
import static mybox.util.DropboxUtil.getSearchUrl;
import static mybox.util.DropboxUtil.getShareUrl;
import static mybox.util.DropboxUtil.getThumbnailUrl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import mybox.config.SystemProp;
import mybox.exception.Error;
import mybox.exception.ErrorException;
import mybox.json.JsonConverter;
import mybox.model.DeltaPage;
import mybox.model.FileEntry;
import mybox.model.Link;
import mybox.model.MetadataEntry;
import mybox.model.User;
import mybox.rest.RestClient;
import mybox.rest.RestClientFactory;
import mybox.rest.RestResponse;
import mybox.service.support.ChunkedUploader;
import mybox.service.support.DeltaResponseHandler;
import mybox.service.support.MetadataListResponseHandler;
import mybox.task.HttpPostWorker;
import mybox.to.BulkParams;
import mybox.to.ChunkedUploadParams;
import mybox.to.CopyParams;
import mybox.to.CreateParams;
import mybox.to.DeleteParams;
import mybox.to.DeltaParams;
import mybox.to.EntryParams;
import mybox.to.FileOperationResponse;
import mybox.to.LinkParams;
import mybox.to.MetadataParams;
import mybox.to.MoveParams;
import mybox.to.PathParams;
import mybox.to.RevisionParams;
import mybox.to.SearchParams;
import mybox.to.ThumbnailParams;
import mybox.to.UploadParams;
import mybox.util.ParamsUtil;
import mybox.util.PathUtil;
import mybox.util.EncodeUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DropboxServiceImpl implements DropboxService {

	private static final Logger log = LoggerFactory.getLogger(DropboxServiceImpl.class);

	@Autowired
	private SystemProp systemProp;
	
	@Autowired
	private HttpPostWorker httpPostWorker;

	private RestClient restClient = RestClientFactory.getRestClient();

	private DeltaResponseHandler deltaResponseHandler = new DeltaResponseHandler();

	private MetadataListResponseHandler metadataListResponseHandler = new MetadataListResponseHandler();
	
	public MetadataEntry getFiles(PathParams params) {
		User user = params.getUser();
		String path = params.getPath();
		
		String url = getMetadataUrl(path);
		String[] headers = getAuthHeaders(user.getAccessToken());
		
		MetadataEntry entry = restClient.get(MetadataEntry.class, url, headers);
		customFolderMetadata(entry);
		return entry;
	}

	public MetadataEntry getFiles(MetadataParams params) {
		User user = params.getUser();
		String path = params.getPath();
		String[] qryStr = ParamsUtil.getQueryString(params);
		
		String url = getMetadataUrl(path, qryStr);
		String[] headers = getAuthHeaders(user.getAccessToken());
		
		MetadataEntry entry = restClient.get(MetadataEntry.class, url, headers);
		customFolderMetadata(entry);
		return entry;
	}

	public MetadataEntry getFolders(MetadataParams params) {
		MetadataEntry parentEntry = getFiles(params);
		List<MetadataEntry> folderEntries = getFolders(parentEntry.getContents());
		parentEntry.setContents(folderEntries);
		return parentEntry;
	}

	public FileEntry download(EntryParams params) {
		User user = params.getUser();
		String path = params.getPath();
		String[] qryStr = ParamsUtil.getQueryString(params);

		String url = getFileUrl(path, qryStr);
		String[] headers = getAuthHeaders(user.getAccessToken());

		RestResponse<InputStream> restResponse = restClient.getStream(url, headers);
		FileEntry entry = convertDownloadResponse(restResponse, "x-dropbox-metadata");
		return entry;
	}

	public InputStream getThumbnail(ThumbnailParams params) {
		User user = params.getUser();
		String path = params.getPath();
		String[] qryStr = ParamsUtil.getQueryString(params);

		String url = getThumbnailUrl(path, qryStr);
		String[] headers = getAuthHeaders(user.getAccessToken());

		RestResponse<InputStream> restResponse = restClient.getStream(url, headers);
		return restResponse.getBody();
	}
	
	public MetadataEntry upload(UploadParams params) {
		return upload(params, true);
	}

	public MetadataEntry upload(UploadParams params, boolean isPut) {
		User user = params.getUser();
		String path = params.getPath();
		InputStream is = params.getContent();
		long length = params.getLength();
		String[] qryStr = ParamsUtil.getQueryString(params);

		String[] headers = getAuthHeaders(user.getAccessToken());
		MetadataEntry entry = null;
		if (isPut) {
			String url = getFilePutUrl(path, qryStr);
			entry = restClient.put(MetadataEntry.class, url, is, length, headers);
		} else {
			String url = getFilePostUrl(path, qryStr);
			entry = restClient.post(MetadataEntry.class, url, is, length, headers);
		}
		customMetadata(entry);
		return entry;
	}

	public MetadataEntry chunkedUpload(ChunkedUploadParams params) {
		ChunkedUploader chunkedUploader = new ChunkedUploader(restClient);
		MetadataEntry entry = null;
		try {
			entry = chunkedUploader.upload(params);
			customMetadata(entry);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ErrorException(Error.internalServerError(e.getMessage()));
		}
		return entry;
	}

	public DeltaPage<MetadataEntry> delta(DeltaParams params) {
		User user = params.getUser();
		String url = getDeltaUrl();
		String[] headers = getAuthHeaders(user.getAccessToken());
		return getDelta(params, url, headers);
	}

	protected DeltaPage<MetadataEntry> getDelta(DeltaParams params, String url, String[] headers) {
		List<String> fields = ParamsUtil.getParamList(params);
		DeltaPage<MetadataEntry> deltaPage = restClient.post(deltaResponseHandler, url, fields, headers);

		if (deltaPage.isHasMore()) {
			log.debug("Has delta more than {}", deltaPage.getEntries().size());
			params.setCursor(deltaPage.getCursor());
			DeltaPage<MetadataEntry> nextPage = getDelta(params, url, headers);
			nextPage.getEntries().addAll(deltaPage.getEntries());
			return nextPage;
		} else {
			return deltaPage;
		}
	}

	public List<MetadataEntry> getRevisions(RevisionParams params) {
		User user = params.getUser();
		String path = params.getPath();

		String url = getRevisionUrl(path);
		String[] headers = getAuthHeaders(user.getAccessToken());

		List<MetadataEntry> entries = restClient.get(metadataListResponseHandler, url, headers);
		customMetadata(entries);
		return entries;
	}

	public MetadataEntry restore(EntryParams params) {
		User user = params.getUser();
		String path = params.getPath();
		List<String> fields = ParamsUtil.getParamList(params);

		String url = getRestoreUrl(path);
		String[] headers = getAuthHeaders(user.getAccessToken());

		MetadataEntry entry = restClient.post(MetadataEntry.class, url, fields, headers);
		customMetadata(entry);
		return entry;
	}

	public Link link(LinkParams params) {
		User user = params.getUser();
		String path = params.getPath();
		List<String> fields = ParamsUtil.getParamList(params);

		String url = getShareUrl(path);
		String[] headers = getAuthHeaders(user.getAccessToken());

		Link link = restClient.post(Link.class, url, fields, headers);
		return link;
	}

	public Link media(PathParams params) {
		User user = params.getUser();
		String path = params.getPath();
		List<String> fields = ParamsUtil.getParamList(params);

		String url = getMediaUrl(path);
		String[] headers = getAuthHeaders(user.getAccessToken());

		Link link = restClient.post(Link.class, url, fields, headers);
		return link;
	}

	public List<MetadataEntry> search(SearchParams params) {
		User user = params.getUser();
		String path = params.getPath();
		List<String> fields = ParamsUtil.getParamList(params);
		
		String url = getSearchUrl(path);
		String[] headers = getAuthHeaders(user.getAccessToken());

		List<MetadataEntry> entries = restClient.post(metadataListResponseHandler, url, fields, headers);
		customMetadata(entries);
		return entries;
	}

	public FileOperationResponse createFolder(CreateParams params) {
		User user = params.getUser();
		List<String> fields = ParamsUtil.getParamList(params);

		String url = getCreateFolderUrl();
		String[] headers = getAuthHeaders(user.getAccessToken());

		MetadataEntry entry = restClient.post(MetadataEntry.class, url, fields, headers);
		customMetadata(entry);
		
		String name = PathUtil.getLastPath(params.getPath());
		FileOperationResponse resp = new FileOperationResponse(name);
		resp.setMetadata(entry);
		return resp;
	}

	public List<FileOperationResponse> delete(DeleteParams params) {
		String url = getDeleteUrl();
		List<List<String>> fieldsList = ParamsUtil.getParamList(params);
		return operateFile(url, params, fieldsList);
	}

	public List<FileOperationResponse> move(MoveParams params) {
		String url = getMoveUrl();
		List<List<String>> fieldsList = ParamsUtil.getParamList(params);
		return operateFile(url, params, fieldsList);
	}

	public List<FileOperationResponse> copy(CopyParams params) {
		String url = getCopyUrl();
		List<List<String>> fieldsList = ParamsUtil.getParamList(params);
		return operateFile(url, params, fieldsList);
	}

	protected List<FileOperationResponse> operateFile(String url, BulkParams params, List<List<String>> fieldsList) {
		if (fieldsList == null || fieldsList.size() < 1) {
			throw new ErrorException(Error.badRequest("No any selected files!"));
		}

		User user = params.getUser();
		String[] headers = getAuthHeaders(user.getAccessToken());

		Map<String, Future<MetadataEntry>> futures = new LinkedHashMap<String, Future<MetadataEntry>>();
		String[] paths = params.getPaths();
		for (int i = 0, size = fieldsList.size(); i < size; i++) {
			List<String> fields = fieldsList.get(i);
			Future<MetadataEntry> future = httpPostWorker.work(MetadataEntry.class, url, fields, headers);
			String path = paths[i];
			futures.put(path, future);
		}

		List<FileOperationResponse> result = new ArrayList<FileOperationResponse>();
		for (Map.Entry<String, Future<MetadataEntry>> pair : futures.entrySet()) {
			String path = pair.getKey();
			String name = PathUtil.getLastPath(path);
			FileOperationResponse resp = new FileOperationResponse(name);
			Future<MetadataEntry> future = pair.getValue();
			try {
				MetadataEntry entry = future.get();
				customMetadata(entry);
				resp.setMetadata(entry);
			} catch (ErrorException e) {
				resp.setError(e.getMessage());
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				resp.setError(e.getMessage());
			} finally {
				result.add(resp);
			}
		}
		return result;
	}
	
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
	
	protected void customFolderMetadata(MetadataEntry folderEntry) {
		if (folderEntry == null) {
			return;
		}
		
		String path = folderEntry.getPath();
		if (PathUtil.isRoot(path)) {
			folderEntry.setId(EncodeUtil.encode(path));
			folderEntry.setName(path);
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
		if (path != null && !path.equals("")) {
			String id = EncodeUtil.encode(path);
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
		} else {
			log.warn("Path is empty!");
			return;
		}
		
		String modified = entry.getModified();
		if (modified != null && !modified.equals("")) {
			// modified data format: Tue, 16 Oct 2012 10:27:40 +0000
			int idx = modified.lastIndexOf("+");
			if (idx > 0) {
				modified = modified.substring(0, idx - 1);
				entry.setModified(modified);
			}
		}
	}
}
