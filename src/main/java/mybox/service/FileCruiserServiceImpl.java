package mybox.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import mybox.backend.MetadataListResponseHandler;
import mybox.backend.ParamsUtil;
import mybox.backend.PathUtil;
import mybox.backend.filecruiser.FileCruiserRestResponseValidator;
import mybox.backend.filecruiser.FileCruiserUtil;
import mybox.backend.filecruiser.Header;
import mybox.backend.filecruiser.Resource;
import mybox.config.SystemProp;
import mybox.exception.Error;
import mybox.exception.ErrorException;
import mybox.model.FileEntry;
import mybox.model.Link;
import mybox.model.MetadataEntry;
import mybox.model.Space;
import mybox.model.filecruiser.DeltaPage;
import mybox.model.filecruiser.FileCruiserSpace;
import mybox.model.filecruiser.FileCruiserUser;
import mybox.model.filecruiser.SharedFile;
import mybox.model.filecruiser.SharingFile;
import mybox.model.keystone.Project;
import mybox.rest.RestClient;
import mybox.rest.RestClientFactory;
import mybox.rest.RestResponse;
import mybox.task.FileCruiserHttpPostWorker;
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
import mybox.util.FileUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileCruiserServiceImpl extends AbstractFileService implements FileCruiserService {
	
	private static final Logger log = LoggerFactory.getLogger(FileCruiserServiceImpl.class);
	
	@Autowired
	private SystemProp systemProp;

	@Autowired
	protected UserService userService;
	
	@Autowired
	private EndpointService endpointService;
	
	@Autowired
	private FileCruiserHttpPostWorker httpPostWorker;

	private RestClient restClient;
	
	private FileCruiserRestResponseValidator restResponseValidator;
	
	private MetadataListResponseHandler metadataListResponseHandler;
	
	public FileCruiserServiceImpl() {
		restResponseValidator = new FileCruiserRestResponseValidator();
		restClient = RestClientFactory.getRestClient(restResponseValidator);
		metadataListResponseHandler = new MetadataListResponseHandler(restResponseValidator);
	}

	@Override
	public FileCruiserUser auth(LoginParams params) {
		return userService.auth(params);
	}

	@Override
	public Space getDefaultSpace(Params params) {
		return getSpace(params, null);
	}

	@Override
	public Space getSpace(Params params, String spaceId) {
		Project project = userService.getProject(params, spaceId);
		FileCruiserSpace space = new FileCruiserSpace();
		space.setProject(project);
		space.setRoot("/");
		return space;
	}

	@Override
	public Space getSpace(PathParams params) {
		return getSpace(params, params.getRoot());
	}

	@Override
	public MetadataEntry getFiles(PathParams params) {
		FileCruiserUser user = (FileCruiserUser) params.getUser();
		Space space = getSpace(params);
		String path = params.getPath();
		
		String resource = PathUtil.buildPath(Resource.METADATA, path);
		String url = getFileServiceUrl(resource);
		String[] headers = FileCruiserUtil.getHeaders(user.getToken());
		
		MetadataEntry entry = restClient.get(MetadataEntry.class, url, headers);
		customEntries(space, entry);
		log.debug("metadata: {}", entry);
		return entry;
	}

	@Override
	public MetadataEntry getFiles(MetadataParams params) {
		FileCruiserUser user = (FileCruiserUser) params.getUser();
		Space space = getSpace(params);
		String path = params.getPath();
		String[] qryStr = ParamsUtil.getQueryString(params);
		
		String resource = PathUtil.buildPath(Resource.METADATA, path);
		String url = getFileServiceUrl(resource, qryStr);
		String[] headers = FileCruiserUtil.getHeaders(user.getToken());
		log.debug("getFileUrl: {}", url);
		
		MetadataEntry entry = restClient.get(MetadataEntry.class, url, headers);
		customEntries(space, entry);
		log.debug("metadata: {}", entry);
		return entry;
	}

	@Override
	public MetadataEntry getFolders(MetadataParams params) {
		MetadataEntry parentEntry = getFiles(params);
		List<MetadataEntry> folderEntries = getFolders(parentEntry.getContents());
		parentEntry.setContents(folderEntries);
		return parentEntry;
	}

	@Override
	public FileEntry download(EntryParams params) {
		FileCruiserUser user = (FileCruiserUser) params.getUser();
		String path = params.getPath();
		String[] qryStr = ParamsUtil.getQueryString(params);

		String resource = PathUtil.buildPath(Resource.FILES, path);
		String url = getFileServiceUrl(resource, qryStr);
		String[] headers = FileCruiserUtil.getHeaders(user.getToken());
		log.debug("downloadUrl: {}", url);

		RestResponse<InputStream> restResponse = restClient.getStream(url, headers);
		FileEntry entry = convertDownloadResponse(restResponse, Header.X_METATADA);
		return entry;
	}

	@Override
	public InputStream getThumbnail(ThumbnailParams params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Link media(PathParams params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MetadataEntry upload(UploadParams params) {
		FileCruiserUser user = (FileCruiserUser) params.getUser();
		String path = params.getPath();
		InputStream is = params.getContent();
		long length = params.getLength();
		String[] qryStr = ParamsUtil.getQueryString(params);

		String resource = PathUtil.buildPath(Resource.FILES, path);
		String url = getFileServiceUrl(resource, qryStr);
		String[] headers = FileCruiserUtil.getHeaders4Upload(user.getToken());
		log.debug("uploadUrl: {}", url);

		MetadataEntry entry = restClient.post(MetadataEntry.class, url, is, length, headers);
		customEntry(entry);
		log.debug("upload metadata: {}", entry);
		return entry;
	}

	@Override
	public MetadataEntry chunkedUpload(ChunkedUploadParams params) {
		// TODO Auto-generated method stub
		return null;
	}

	public DeltaPage delta(DeltaParams params) {
		FileCruiserUser user = (FileCruiserUser) params.getUser();
		String[] headers = FileCruiserUtil.getHeaders(user.getToken());
		DeltaPage deltaPage = getDelta(params, headers);
		log.debug("deltaPage: {}", deltaPage);
		return deltaPage;
	}

	protected DeltaPage getDelta(DeltaParams params, String[] headers) {
		String[] qryStr = ParamsUtil.getQueryString(params);
		String url = getFileServiceUrl(Resource.DELTA, qryStr);
		log.debug("deltaUrl: {}", url);
		
		DeltaPage deltaPage = restClient.post(DeltaPage.class, url, headers);
		if (deltaPage.isHasMore()) {
			log.debug("Has delta more than {}", deltaPage.getEntries().size());
			params.setCursor(deltaPage.getCursor());
			DeltaPage nextPage = getDelta(params, headers);
			nextPage.getEntries().addAll(deltaPage.getEntries());
			return nextPage;
		} else {
			return deltaPage;
		}
	}

	@Override
	public List<MetadataEntry> search(SearchParams params) {
		FileCruiserUser user = (FileCruiserUser) params.getUser();
		String path = params.getPath();
		String[] qryStr = ParamsUtil.getQueryString(params);
		
		String resource = PathUtil.buildPath(Resource.SEARCH, path);
		String url = getFileServiceUrl(resource, qryStr);
		String[] headers = FileCruiserUtil.getHeaders(user.getToken());
		log.debug("searchUrl: {}", url);
		
		List<MetadataEntry> entries = restClient.post(metadataListResponseHandler, url, null, headers);
		customEntries(entries);
		log.debug("search metadata: {}", entries);
		return entries;
	}

	@Override
	public List<MetadataEntry> getRevisions(RevisionParams params) {
		FileCruiserUser user = (FileCruiserUser) params.getUser();
		String path = params.getPath();

		String resource = PathUtil.buildPath(Resource.REVISIONS, path);
		String url = getFileServiceUrl(resource);
		String[] headers = FileCruiserUtil.getHeaders(user.getToken());
		log.debug("getRevisionsdUrl: {}", url);

		List<MetadataEntry> entries = restClient.get(metadataListResponseHandler, url, headers);
		customEntries(entries);
		log.debug("revision metadata: {}", entries);
		return entries;
	}

	@Override
	public MetadataEntry restore(EntryParams params) {
		FileCruiserUser user = (FileCruiserUser) params.getUser();
		String path = params.getPath();
		String[] qryStr = ParamsUtil.getQueryString(params);

		String resource = PathUtil.buildPath(Resource.RESTORE, path);
		String url = getFileServiceUrl(resource, qryStr);
		String[] headers = FileCruiserUtil.getHeaders(user.getToken());
		log.debug("restoreUrl: {}", url);

		MetadataEntry entry = restClient.post(MetadataEntry.class, url, headers);
		customEntry(entry);
		log.debug("restore metadata: {}", entry);
		return entry;
	}

	@Override
	public Link link(LinkParams params) {
		FileCruiserUser user = (FileCruiserUser) params.getUser();
		String path = params.getPath();
		String[] qryStr = ParamsUtil.getQueryString(params);

		String resource = PathUtil.buildPath(Resource.SHARE_LINK, path);
		String url = getFileServiceUrl(resource, qryStr);
		String[] headers = FileCruiserUtil.getHeaders(user.getToken());
		log.debug("linkUrl: {}", url);

		Link link = restClient.post(Link.class, url, headers);
		log.debug("link: {}", link);
		String privateUrl = link.getUrl();
		int idx = privateUrl.indexOf("/links");
		String linkPath = privateUrl.substring(idx);
		String portalUrl = getUserPortalUrl();
		String publicUrl = PathUtil.buildPath(portalUrl, "/fc", linkPath);
		link.setUrl(publicUrl);
		return link;
	}
	
	public FileEntry getLink(EntryParams params) {
		String path = params.getPath();
		String[] qryStr = ParamsUtil.getQueryString(params);

		String resource = PathUtil.buildPath(Resource.LINKS, path);
		String url = getFileServiceUrlWoVersion(resource, qryStr); //* getFileServiceUrlWoVersion for test
		log.debug("getLinkUrl: {}", url);

		RestResponse<InputStream> restResponse = restClient.getStream(url);
		FileEntry entry = convertDownloadResponse(restResponse, Header.X_METATADA);
		
		MetadataEntry metadata = entry.getMetadata();
		if (metadata.getIsDir()) {
			resource = PathUtil.buildPath(Resource.METADATA, metadata.getPath());
			url = getFileServiceUrl(resource);
			String token = systemProp.getAdminToken(); //* for test
			String[] headers = FileCruiserUtil.getHeaders(token);
			
			metadata = restClient.get(MetadataEntry.class, url, headers);
			customEntries(metadata);
			log.debug("metadata: {}", metadata);
			entry.setMetadata(metadata);
		}
		return entry;
	}
	
	public List<Link> getLinkes() {
		// new API, not impl.
		return null;
	}
	
	public SharedFile share(Params params, SharingFile sharingFile) {
		FileCruiserUser user = (FileCruiserUser) params.getUser();
		String url = getFileServiceUrl(Resource.SHARE_FILE);
		String[] headers = FileCruiserUtil.getHeaders(user.getToken());
		log.debug("shareUrl: {}", url);
		//restClient.post(url, headers, sharingFile, SharingFile.class, false);
		return null;
	}
	
	public List<SharedFile> getShares(PathParams params) {
		return null;
	}

	@Override
	public FileOperationResponse createFolder(CreateParams params) {
		FileCruiserUser user = (FileCruiserUser) params.getUser();
		String[] qryStr = ParamsUtil.getQueryString(params);
		
		String url = getFileServiceUrl(Resource.CREATE_FOLDER, qryStr);
		String[] headers = FileCruiserUtil.getHeaders(user.getToken());
		log.debug("createFolderUrl: {}", url);
		
		MetadataEntry entry = restClient.post(MetadataEntry.class, url, headers);
		customEntry(entry);
		
		String name = FileUtil.getNameFromPath(params.getPath());
		FileOperationResponse resp = new FileOperationResponse(name);
		resp.setMetadata(entry);
		log.debug("creating folder metadata: {}", entry);
		return resp;
	}

	@Override
	public List<FileOperationResponse> delete(DeleteParams params) {
		List<List<String>> fieldsList = ParamsUtil.getParamList(params);
		return post(Resource.DELETE_FILE, params, fieldsList);
	}

	@Override
	public List<FileOperationResponse> move(MoveParams params) {
		List<List<String>> fieldsList = ParamsUtil.getParamList(params);
		return post(Resource.MOVE_FILE, params, fieldsList);
	}

	@Override
	public List<FileOperationResponse> copy(CopyParams params) {
		List<List<String>> fieldsList = ParamsUtil.getParamList(params);
		return post(Resource.COPY_FILE, params, fieldsList);
	}
	
	protected List<FileOperationResponse> post(String resource, BulkParams params, List<List<String>> fieldsList) {
		if (fieldsList == null || fieldsList.size() < 1) {
			throw new ErrorException(Error.badRequest("No any selected files!"));
		}
		
		FileCruiserUser user = (FileCruiserUser) params.getUser();
		String[] headers = FileCruiserUtil.getHeaders(user.getToken());
		Map<String, Future<MetadataEntry>> futures = new LinkedHashMap<String, Future<MetadataEntry>>();
		String[] paths = params.getPaths();
		for (int i = 0, size = fieldsList.size(); i < size; i++) {
			List<String> qryStr = fieldsList.get(i);
			String url = getFileServiceUrl(resource, qryStr.toArray(new String[qryStr.size()]));
			log.debug("fileOpUrl: {}", url);
			Future<MetadataEntry> future = httpPostWorker.work(url, headers);
			String path = paths[i];
			futures.put(path, future);
		}

		List<FileOperationResponse> result = new ArrayList<FileOperationResponse>();
		for (Map.Entry<String, Future<MetadataEntry>> pair : futures.entrySet()) {
			String path = pair.getKey();
			String name = FileUtil.getNameFromPath(path);
			FileOperationResponse resp = new FileOperationResponse(name);
			Future<MetadataEntry> future = pair.getValue();
			try {
				MetadataEntry entry = future.get();
				customEntry(entry);
				resp.setMetadata(entry);
				log.debug("file operation metadata: {}", entry);
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
	
	protected void customEntry(MetadataEntry entry) {
		super.customEntry(entry);
		
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
	
	protected String getFileServiceUrl(String resource, String... qryStr) {
		String fileServiceBaseUrl = endpointService.getFileServiceUrl();
		String url = PathUtil.getUrl(fileServiceBaseUrl, resource, qryStr);
		return url;
	}
	
	protected String getFileServiceUrlWoVersion(String resource, String... qryStr) {
		String fileServiceBaseUrl = endpointService.getFileServiceUrl();
		int idx = fileServiceBaseUrl.lastIndexOf("/v1");
		if (idx > 0) {
			fileServiceBaseUrl = fileServiceBaseUrl.substring(0, idx);
		}
		String url = PathUtil.getUrl(fileServiceBaseUrl, resource, qryStr);
		return url;
	}
	
	protected String getUserPortalUrl() {
		return systemProp.getUserPortalUrl(); //* for test
		//return endpointService.getUserPortalUrl();
	}
}
