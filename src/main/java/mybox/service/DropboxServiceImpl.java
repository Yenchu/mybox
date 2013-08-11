package mybox.service;

import static mybox.service.support.DropboxUtil.getCopyUrl;
import static mybox.service.support.DropboxUtil.getCreateFolderUrl;
import static mybox.service.support.DropboxUtil.getDefaultUser;
import static mybox.service.support.DropboxUtil.getDeleteUrl;
import static mybox.service.support.DropboxUtil.getDeltaUrl;
import static mybox.service.support.DropboxUtil.getFilePostUrl;
import static mybox.service.support.DropboxUtil.getFilePutUrl;
import static mybox.service.support.DropboxUtil.getFileUrl;
import static mybox.service.support.DropboxUtil.getMediaUrl;
import static mybox.service.support.DropboxUtil.getMetadataUrl;
import static mybox.service.support.DropboxUtil.getMoveUrl;
import static mybox.service.support.DropboxUtil.getRestoreUrl;
import static mybox.service.support.DropboxUtil.getRevisionUrl;
import static mybox.service.support.DropboxUtil.getRootId;
import static mybox.service.support.DropboxUtil.getRootName;
import static mybox.service.support.DropboxUtil.getSearchUrl;
import static mybox.service.support.DropboxUtil.getShareUrl;
import static mybox.service.support.DropboxUtil.getSignedHeaders;
import static mybox.service.support.DropboxUtil.getThumbnailUrl;
import static mybox.service.support.DropboxUtil.getUser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import mybox.exception.Error;
import mybox.exception.ErrorException;
import mybox.model.DeltaPage;
import mybox.model.DropboxUser;
import mybox.model.FileEntry;
import mybox.model.Link;
import mybox.model.MetadataEntry;
import mybox.model.Space;
import mybox.model.User;
import mybox.rest.RestClient;
import mybox.rest.RestClientFactory;
import mybox.rest.RestResponse;
import mybox.service.support.ChunkedUploader;
import mybox.service.support.DeltaResponseHandler;
import mybox.service.support.DropboxUtil;
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
import mybox.to.LoginParams;
import mybox.to.MetadataParams;
import mybox.to.MoveParams;
import mybox.to.Params;
import mybox.to.PathParams;
import mybox.to.RevisionParams;
import mybox.to.SearchParams;
import mybox.to.ThumbnailParams;
import mybox.to.UploadParams;
import mybox.util.ParamsUtil;
import mybox.util.PathUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DropboxServiceImpl extends AbstractFileService implements DropboxService {

	private static final Logger log = LoggerFactory.getLogger(DropboxServiceImpl.class);

	@Autowired
	private HttpPostWorker httpPostWorker;

	private RestClient restClient = RestClientFactory.getRestClient();

	private DeltaResponseHandler deltaResponseHandler = new DeltaResponseHandler();

	private MetadataListResponseHandler metadataListResponseHandler = new MetadataListResponseHandler();

	public User auth(LoginParams params) {
		//* just for test
		if (!params.getPassword().equals("cloud")) {
			log.info("{} login failed!", params.getUsername());
			return null;
		}

		DropboxUser user = getDefaultUser();
		user.setName(params.getUsername());
		user.setIp(params.getIp());
		return user;
	}
	
	public Space getDefaultSpace(Params params) {
		//* for demo: Dropbox only have a space
		Space space = new Space();
		space.setId(getRootId());
		space.setName(getRootName());
		space.setRoot("/");
		return space;
	}
	
	public Space getSpace(Params params, String spaceId) {
		return getDefaultSpace(params);
	}
	
	public Space getSpace(PathParams params) {
		return getSpace(params, params.getRoot());
	}

	public MetadataEntry getFiles(PathParams params) {
		DropboxUser user = getUser(params);
		Space space = getSpace(params);
		String root = params.getRoot();
		String path = params.getPath();
		
		String url = getMetadataUrl(root, path);
		String[] headers = getSignedHeaders(user);
		
		MetadataEntry entry = restClient.get(MetadataEntry.class, url, headers);
		customFolderMetadata(space, entry);
		return entry;
	}

	public MetadataEntry getFiles(MetadataParams params) {
		DropboxUser user = getUser(params);
		Space space = getSpace(params);
		String root = params.getRoot();
		String path = params.getPath();
		String[] qryStr = ParamsUtil.getQueryString(params);
		
		String url = getMetadataUrl(root, path, qryStr);
		String[] headers = getSignedHeaders(user);
		
		MetadataEntry entry = restClient.get(MetadataEntry.class, url, headers);
		customFolderMetadata(space, entry);
		return entry;
	}

	public MetadataEntry getFolders(MetadataParams params) {
		MetadataEntry parentEntry = getFiles(params);
		List<MetadataEntry> folderEntries = getFolders(parentEntry.getContents());
		parentEntry.setContents(folderEntries);
		return parentEntry;
	}

	public FileEntry download(EntryParams params) {
		DropboxUser user = DropboxUtil.getUser(params);
		String root = params.getRoot();
		String path = params.getPath();
		String[] qryStr = ParamsUtil.getQueryString(params);

		String url = getFileUrl(root, path, qryStr);
		String[] headers = getSignedHeaders(user);

		RestResponse<InputStream> restResponse = restClient.getStream(url, headers);
		FileEntry entry = convertDownloadResponse(restResponse, "x-dropbox-metadata");
		return entry;
	}

	public InputStream getThumbnail(ThumbnailParams params) {
		DropboxUser user = getUser(params);
		String root = params.getRoot();
		String path = params.getPath();
		String[] qryStr = ParamsUtil.getQueryString(params);

		String url = getThumbnailUrl(root, path, qryStr);
		String[] headers = getSignedHeaders(user);

		RestResponse<InputStream> restResponse = restClient.getStream(url, headers);
		return restResponse.getBody();
	}
	
	public MetadataEntry upload(UploadParams params) {
		return upload(params, true);
	}

	public MetadataEntry upload(UploadParams params, boolean isPut) {
		DropboxUser user = getUser(params);
		String root = params.getRoot();
		String path = params.getPath();
		InputStream is = params.getContent();
		long length = params.getLength();
		String[] qryStr = ParamsUtil.getQueryString(params);

		String[] headers = getSignedHeaders(user);
		MetadataEntry entry = null;
		if (isPut) {
			String url = getFilePutUrl(root, path, qryStr);
			entry = restClient.put(MetadataEntry.class, url, is, length, headers);
		} else {
			String url = getFilePostUrl(root, path, qryStr);
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
		DropboxUser user = getUser(params);
		String url = getDeltaUrl();
		String[] headers = getSignedHeaders(user);
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
		DropboxUser user = getUser(params);
		String root = params.getRoot();
		String path = params.getPath();

		String url = getRevisionUrl(root, path);
		String[] headers = getSignedHeaders(user);

		List<MetadataEntry> entries = restClient.get(metadataListResponseHandler, url, headers);
		customMetadata(entries);
		return entries;
	}

	public MetadataEntry restore(EntryParams params) {
		DropboxUser user = getUser(params);
		String root = params.getRoot();
		String path = params.getPath();
		List<String> fields = ParamsUtil.getParamList(params);

		String url = getRestoreUrl(root, path);
		String[] headers = getSignedHeaders(user);

		MetadataEntry entry = restClient.post(MetadataEntry.class, url, fields, headers);
		customMetadata(entry);
		return entry;
	}

	public Link link(LinkParams params) {
		DropboxUser user = getUser(params);
		String root = params.getRoot();
		String path = params.getPath();
		List<String> fields = ParamsUtil.getParamList(params);

		String url = getShareUrl(root, path);
		String[] headers = getSignedHeaders(user);

		Link link = restClient.post(Link.class, url, fields, headers);
		return link;
	}

	public Link media(PathParams params) {
		DropboxUser user = getUser(params);
		String root = params.getRoot();
		String path = params.getPath();
		List<String> fields = ParamsUtil.getParamList(params);

		String url = getMediaUrl(root, path);
		String[] headers = getSignedHeaders(user);

		Link link = restClient.post(Link.class, url, fields, headers);
		return link;
	}

	public List<MetadataEntry> search(SearchParams params) {
		DropboxUser user = getUser(params);
		String root = params.getRoot();
		String path = params.getPath();
		List<String> fields = ParamsUtil.getParamList(params);
		
		String url = getSearchUrl(root, path);
		String[] headers = getSignedHeaders(user);

		List<MetadataEntry> entries = restClient.post(metadataListResponseHandler, url, fields, headers);
		customMetadata(entries);
		return entries;
	}

	public FileOperationResponse createFolder(CreateParams params) {
		DropboxUser user = getUser(params);
		List<String> fields = ParamsUtil.getParamList(params);

		String url = getCreateFolderUrl();
		String[] headers = getSignedHeaders(user);

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

		DropboxUser user = getUser(params);
		String[] headers = getSignedHeaders(user);

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
	
	protected void customMetadata(MetadataEntry entry) {
		super.customMetadata(entry);
		
		String modified = entry.getModified();
		if (modified == null || modified.equals("")) {
			return;
		}
		
		// modified data format: Tue, 16 Oct 2012 10:27:40 +0000
		int idx = modified.lastIndexOf("+");
		if (idx > 0) {
			modified = modified.substring(0, idx - 1);
			entry.setModified(modified);
		}		
	}
}
