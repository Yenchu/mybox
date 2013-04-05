package mybox.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import mybox.backend.filecruiser.ContentType;
import mybox.backend.filecruiser.Header;
import mybox.backend.filecruiser.Resource;
import mybox.exception.Error;
import mybox.exception.ErrorException;
import mybox.json.JsonConverter;
import mybox.model.BulkParams;
import mybox.model.ChunkedUploadParams;
import mybox.model.CopyParams;
import mybox.model.CreateParams;
import mybox.model.DeleteParams;
import mybox.model.DeltaPage;
import mybox.model.DeltaParams;
import mybox.model.EntryParams;
import mybox.model.EntryUtil;
import mybox.model.FileEntry;
import mybox.model.Link;
import mybox.model.LinkParams;
import mybox.model.LoginParams;
import mybox.model.MetadataEntry;
import mybox.model.MetadataParams;
import mybox.model.MoveParams;
import mybox.model.Params;
import mybox.model.ParamsUtil;
import mybox.model.PathParams;
import mybox.model.RevisionParams;
import mybox.model.SearchParams;
import mybox.model.Space;
import mybox.model.ThumbnailParams;
import mybox.model.UploadParams;
import mybox.model.filecruiser.FileCruiserSpace;
import mybox.model.filecruiser.FileCruiserUser;
import mybox.model.keystone.Auth;
import mybox.model.keystone.Project;
import mybox.model.keystone.Token;
import mybox.model.keystone.User;
import mybox.rest.RestResponse;
import mybox.task.HttpPostWorker;
import mybox.to.FileOperationResponse;
import mybox.util.FileUtil;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileCruiserServiceImpl extends AbstractBackendService implements FileCruiserService {
	
	private static final Logger log = LoggerFactory.getLogger(FileCruiserServiceImpl.class);
	
	@Autowired
	private HttpPostWorker httpPostWorker;
	
	public User getUser(FileCruiserUser user) {
		String token = user.getToken();
		String userId = user.getId();
		String resource = buildPath(Resource.USERS, userId);
		String url = getUserUrl(resource);
		User rtUser = this.get(url, token, User.class, true);
		return rtUser;
	}
	
	public Project getProject(FileCruiserUser user, String projectId) {
		String token = user.getToken();
		String resource = buildPath(Resource.PROJECTS, projectId);
		String url = getUserUrl(resource);
		Project project = this.get(url, token, Project.class, true);
		return project;
	}
	
	@Override
	public FileCruiserUser auth(LoginParams params) {
		String url = getUserUrl(Resource.TOKENS);
		String[] headers = {Header.CONTENT_TYPE, ContentType.JSON};
		
		String domainName = params.getDomain();
		if (StringUtils.isBlank(domainName)) {
			//* for test only
			domainName = systemProp.getDefaultDomain();
		}
		
		Auth auth = new Auth(domainName, params.getUsername(), params.getPassword());
		String body = JsonConverter.toJson(auth, true);
		log.debug("authUrl: {}  body: {}", url, body);
		
		RestResponse<String> restResponse = restClient.post(url, body, headers);
		Token token = JsonConverter.fromJson(restResponse.getBody(), Token.class, true);
		String tokenValue = restResponse.getHeader(Header.X_SUBJECT_TOKEN);
		log.debug("authToken: {}  resp: {}", tokenValue, token);
		
		DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
		DateTime dt = fmt.parseDateTime(token.getExpiresAt());
		
		FileCruiserUser user = new FileCruiserUser();
		user.setToken(tokenValue);
		user.setExpiresAt(dt.toDate());
		user.setId(token.getUser().getId());
		user.setName(token.getUser().getName());
		user.setIp(params.getIp());
		user.setDomainId(token.getUser().getDomain().getId());
		user.setDomainName(token.getUser().getDomain().getName());
		return user;
	}

	@Override
	public Space getDefaultSpace(Params params) {
		return getSpace(params, null);
	}

	@Override
	public Space getSpace(Params params, String spaceId) {
		FileCruiserUser fcUser = (FileCruiserUser) params.getUser();
		String projectId;
		if (StringUtils.isBlank(spaceId)) {
			User user = getUser(fcUser);
			projectId = user.getDefaultProjectId();
		} else {
			projectId = spaceId;
		}
		
		Project project = getProject(fcUser, projectId);
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
		//FileCruiserUser fcUser = (FileCruiserUser) params.getUser();
		//String root = params.getRoot();
		Space space = getSpace(params);
		String path = params.getPath();
		
		String resource = buildPath(Resource.METADATA, path);
		String url = getFileUrl(resource);
		
		MetadataEntry entry = this.get(url, MetadataEntry.class);
		EntryUtil.customEntries(space, entry);
		log.debug("entry: {}", entry);
		return entry;
	}

	@Override
	public MetadataEntry getFiles(MetadataParams params) {
		Space space = getSpace(params);
		String path = params.getPath();
		if (path.equals("/")) {
			path += "."; //* just a workaround for backend defact
		}
		String[] qryStr = ParamsUtil.getQueryString(params);
		
		String resource = buildPath(Resource.METADATA, path);
		String url = getFileUrl(resource, qryStr);
		
		MetadataEntry entry = this.get(url, MetadataEntry.class);
		EntryUtil.customEntries(space, entry);
		log.debug("entry: {}", entry);
		return entry;
	}

	@Override
	public MetadataEntry getFolders(MetadataParams params) {
		MetadataEntry parentEntry = getFiles(params);
		List<MetadataEntry> folderEntries = EntryUtil.getFolders(parentEntry.getContents());
		parentEntry.setContents(folderEntries);
		return parentEntry;
	}

	@Override
	public FileEntry download(EntryParams params) {
		String path = params.getPath();
		String[] qryStr = ParamsUtil.getQueryString(params);

		String resource = buildPath(Resource.FILES, path);
		String url = getFileUrl(resource, qryStr);
		String[] headers = getHeaders(getAdminToken());
		log.debug("downloadUrl: {}", url);

		RestResponse<InputStream> restResponse = restClient.getStream(url, headers);
		log.debug("resp: {}", restResponse);
		FileEntry entry = EntryUtil.convertDownloadResponse(restResponse, "X-Dropbox-Metadata");
		return entry;
	}

	@Override
	public InputStream getThumbnail(ThumbnailParams params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MetadataEntry upload(UploadParams params) {
		String path = params.getPath();
		InputStream is = params.getContent();
		long length = params.getLength();
		String[] qryStr = ParamsUtil.getQueryString(params);

		String resource = buildPath(Resource.FILES, path);
		String url = getFileUrl(resource, qryStr);
		String[] headers = getHeaders(getAdminToken());
		log.debug("uploadUrl: {}", url);

		MetadataEntry entry = restClient.post(MetadataEntry.class, url, is, length, headers);
		customEntry(entry);
		return entry;
	}

	@Override
	public MetadataEntry chunkedUpload(ChunkedUploadParams params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DeltaPage<MetadataEntry> delta(DeltaParams params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MetadataEntry> getRevisions(RevisionParams params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MetadataEntry restore(EntryParams params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Link link(LinkParams params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Link media(PathParams params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MetadataEntry> search(SearchParams params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FileOperationResponse createFolder(CreateParams params) {
		String[] qryStr = ParamsUtil.getQueryString(params);
		
		String resource = buildPath(Resource.CREATE_FOLDER);
		String url = getFileUrl(resource, qryStr);
		String[] headers = getHeaders(getAdminToken());
		log.debug("createFolderUrl: {}", url);
		
		MetadataEntry entry = restClient.post(MetadataEntry.class, url, headers);
		customEntry(entry);
		
		String name = FileUtil.getNameFromPath(params.getPath());
		FileOperationResponse resp = new FileOperationResponse(name);
		resp.setMetadata(entry);
		return resp;
	}

	@Override
	public List<FileOperationResponse> delete(DeleteParams params) {
		String url = getFileUrl(Resource.DELETE_FILE);
		return post(params, url);
	}

	@Override
	public List<FileOperationResponse> move(MoveParams params) {
		String url = getFileUrl(Resource.MOVE_FILE);
		return post(params, url);
	}

	@Override
	public List<FileOperationResponse> copy(CopyParams params) {
		String url = getFileUrl(Resource.COPY_FILE);
		return post(params, url);
	}
	
	protected List<FileOperationResponse> post(BulkParams params, String url) {
		List<List<String>> fieldsList = params.getParamList();
		if (fieldsList == null || fieldsList.size() < 1) {
			throw new ErrorException(Error.badRequest("No any selected files!"));
		}

		String[] headers = getHeaders(getAdminToken());
		Map<String, Future<MetadataEntry>> futures = new LinkedHashMap<String, Future<MetadataEntry>>();
		String[] paths = params.getPaths();
		for (int i = 0, size = fieldsList.size(); i < size; i++) {
			List<String> fields = fieldsList.get(i);
			Future<MetadataEntry> future = httpPostWorker.work(url, fields, headers);
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
		EntryUtil.customEntry(entry);
		
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
