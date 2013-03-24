package mybox.service;

import static mybox.dropbox.DropboxUtil.getCopyUrl;
import static mybox.dropbox.DropboxUtil.getCreateFolderUrl;
import static mybox.dropbox.DropboxUtil.getDefaultUser;
import static mybox.dropbox.DropboxUtil.getDeleteUrl;
import static mybox.dropbox.DropboxUtil.getDeltaUrl;
import static mybox.dropbox.DropboxUtil.getFilePutUrl;
import static mybox.dropbox.DropboxUtil.getFileUrl;
import static mybox.dropbox.DropboxUtil.getMediaUrl;
import static mybox.dropbox.DropboxUtil.getMetadataUrl;
import static mybox.dropbox.DropboxUtil.getMoveUrl;
import static mybox.dropbox.DropboxUtil.getRestoreUrl;
import static mybox.dropbox.DropboxUtil.getRevisionUrl;
import static mybox.dropbox.DropboxUtil.getRootId;
import static mybox.dropbox.DropboxUtil.getRootName;
import static mybox.dropbox.DropboxUtil.getSearchUrl;
import static mybox.dropbox.DropboxUtil.getShareUrl;
import static mybox.dropbox.DropboxUtil.getSignedHeaders;
import static mybox.dropbox.DropboxUtil.getThumbnailUrl;
import static mybox.dropbox.DropboxUtil.getUser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import mybox.common.to.BulkParams;
import mybox.common.to.ChunkedUploadParams;
import mybox.common.to.CopyParams;
import mybox.common.to.CreateParams;
import mybox.common.to.DeleteParams;
import mybox.common.to.DeltaParams;
import mybox.common.to.EntryParams;
import mybox.common.to.LinkParams;
import mybox.common.to.LoginParams;
import mybox.common.to.MetadataParams;
import mybox.common.to.MoveParams;
import mybox.common.to.Params;
import mybox.common.to.PathParams;
import mybox.common.to.RevisionParams;
import mybox.common.to.SearchParams;
import mybox.common.to.Space;
import mybox.common.to.ThumbnailParams;
import mybox.common.to.UploadParams;
import mybox.common.to.User;
import mybox.dropbox.ChunkedUploader;
import mybox.dropbox.DropboxUtil;
import mybox.dropbox.model.DeltaEntry;
import mybox.dropbox.model.DeltaPage;
import mybox.dropbox.model.FileEntry;
import mybox.dropbox.model.Link;
import mybox.dropbox.model.MetadataEntry;
import mybox.dropbox.to.DropboxUser;
import mybox.rest.Fault;
import mybox.rest.FaultException;
import mybox.rest.RestResponse;
import mybox.rest.RestResponseHandler;
import mybox.task.HttpPostWorker;
import mybox.util.FileUtil;
import mybox.web.to.FileOperationResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Service
public class DropboxServiceImpl extends AbstractFileService implements DropboxService {

	private static final Logger log = LoggerFactory.getLogger(DropboxServiceImpl.class);

	@Autowired
	private RestService restService;

	@Autowired
	private HttpPostWorker httpPostWorker;

	private DeltaResponseHandler deltaResponseHandler = new DeltaResponseHandler();

	private MetadataListResponseHandler metadataListResponseHandler = new MetadataListResponseHandler();

	public User auth(LoginParams params) {
		if (!params.getPassword().equals("cloud")) {
			log.info("{} from {} login failed!", params.getUsername(), params.getAddress());
			return null;
		}

		DropboxUser user = getDefaultUser();
		user.setName(params.getUsername());
		user.setAddress(params.getAddress());
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
		
		MetadataEntry entry = restService.get(MetadataEntry.class, url, headers);
		customEntries(space, entry);
		return entry;
	}

	public MetadataEntry getFiles(MetadataParams params) {
		DropboxUser user = getUser(params);
		Space space = getSpace(params);
		String root = params.getRoot();
		String path = params.getPath();
		String[] qryStr = params.getParamArray();
		
		String url = getMetadataUrl(root, path, qryStr);
		String[] headers = getSignedHeaders(user);
		
		MetadataEntry entry = restService.get(MetadataEntry.class, url, headers);
		customEntries(space, entry);
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
		String[] qryStr = params.getParamArray();

		String url = getFileUrl(root, path, qryStr);
		String[] headers = getSignedHeaders(user);

		RestResponse<InputStream> restResponse = restService.getStream(url, headers);
		FileEntry entry = convertDownloadResponse(restResponse, "x-dropbox-metadata");
		return entry;
	}

	public InputStream getThumbnail(ThumbnailParams params) {
		DropboxUser user = getUser(params);
		String root = params.getRoot();
		String path = params.getPath();
		String[] qryStr = params.getParamArray();

		String url = getThumbnailUrl(root, path, qryStr);
		String[] headers = getSignedHeaders(user);

		RestResponse<InputStream> restResponse = restService.getStream(url, headers);
		return restResponse.getBody();
	}

	public MetadataEntry upload(UploadParams params) {
		DropboxUser user = getUser(params);
		String root = params.getRoot();
		String path = params.getPath();
		InputStream is = params.getContent();
		long length = params.getLength();
		String[] qryStr = params.getParamArray();

		String url = getFilePutUrl(root, path, qryStr);
		String[] headers = getSignedHeaders(user);

		MetadataEntry entry = restService.put(MetadataEntry.class, url, is, length, headers);
		customEntry(entry);
		return entry;
	}

	public MetadataEntry chunkedUpload(ChunkedUploadParams params) {
		ChunkedUploader chunkedUploader = new ChunkedUploader(restService);
		MetadataEntry entry = null;
		try {
			entry = chunkedUploader.upload(params);
			customEntry(entry);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new FaultException(Fault.internalServerError(e.getMessage()));
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
		List<String> fields = params.getParamList();
		DeltaPage<MetadataEntry> deltaPage = restService.post(deltaResponseHandler, url, fields, headers);

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

		List<MetadataEntry> entries = restService.get(metadataListResponseHandler, url, headers);
		customEntries(entries);
		return entries;
	}

	public MetadataEntry restore(EntryParams params) {
		DropboxUser user = getUser(params);
		String root = params.getRoot();
		String path = params.getPath();
		List<String> fields = params.getParamList();

		String url = getRestoreUrl(root, path);
		String[] headers = getSignedHeaders(user);

		MetadataEntry entry = restService.post(MetadataEntry.class, url, fields, headers);
		customEntry(entry);
		return entry;
	}

	public Link link(LinkParams params) {
		DropboxUser user = getUser(params);
		String root = params.getRoot();
		String path = params.getPath();
		List<String> fields = params.getParamList();

		String url = getShareUrl(root, path);
		String[] headers = getSignedHeaders(user);

		Link link = restService.post(Link.class, url, fields, headers);
		return link;
	}

	public Link media(PathParams params) {
		DropboxUser user = getUser(params);
		String root = params.getRoot();
		String path = params.getPath();
		List<String> fields = params.getParamList();

		String url = getMediaUrl(root, path);
		String[] headers = getSignedHeaders(user);

		Link link = restService.post(Link.class, url, fields, headers);
		return link;
	}

	public List<MetadataEntry> search(SearchParams params) {
		DropboxUser user = getUser(params);
		String root = params.getRoot();
		String path = params.getPath();
		List<String> fields = params.getParamList();
		
		String url = getSearchUrl(root, path);
		String[] headers = getSignedHeaders(user);

		List<MetadataEntry> entries = restService.post(metadataListResponseHandler, url, fields, headers);
		customEntries(entries);
		return entries;
	}

	public FileOperationResponse createFolder(CreateParams params) {
		DropboxUser user = getUser(params);
		List<String> fields = params.getParamList();

		String url = getCreateFolderUrl();
		String[] headers = getSignedHeaders(user);

		MetadataEntry entry = restService.post(MetadataEntry.class, url, fields, headers);
		customEntry(entry);
		
		String name = FileUtil.getNameFromPath(params.getPath());
		FileOperationResponse resp = new FileOperationResponse(name);
		resp.setMetadata(entry);
		return resp;
	}

	public List<FileOperationResponse> delete(DeleteParams params) {
		String url = getDeleteUrl();
		return post(params, url);
	}

	public List<FileOperationResponse> move(MoveParams params) {
		String url = getMoveUrl();
		return post(params, url);
	}

	public List<FileOperationResponse> copy(CopyParams params) {
		String url = getCopyUrl();
		return post(params, url);
	}

	protected List<FileOperationResponse> post(BulkParams params, String url) {
		List<List<String>> fieldsList = params.getParamList();
		if (fieldsList == null || fieldsList.size() < 1) {
			throw new FaultException(Fault.badRequest("No any to-be-deleted path!"));
		}

		DropboxUser user = getUser(params);
		String[] headers = getSignedHeaders(user);

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
			} catch (FaultException e) {
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
	
	@Override
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

	protected class DeltaResponseHandler extends RestResponseHandler<DeltaPage<MetadataEntry>, String> {

		@Override
		public DeltaPage<MetadataEntry> handle(RestResponse<String> restResponse) {
			checkResponse(restResponse);

			String content = restResponse.getBody();
			JsonParser parser = new JsonParser();
			JsonElement je = parser.parse(content);
			JsonObject jo = je.getAsJsonObject();

			DeltaPage<MetadataEntry> deltaPage = new DeltaPage<MetadataEntry>();
			JsonElement resetElem = jo.get("reset");
			if (resetElem != null) {
				deltaPage.setReset(resetElem.getAsBoolean());
			}
			JsonElement hasMoreElem = jo.get("has_more");
			if (hasMoreElem != null) {
				deltaPage.setHasMore(hasMoreElem.getAsBoolean());
			}
			JsonElement cursorElem = jo.get("cursor");
			if (cursorElem != null) {
				deltaPage.setCursor(cursorElem.getAsString());
			}

			List<DeltaEntry<MetadataEntry>> entries = new ArrayList<DeltaEntry<MetadataEntry>>();
			deltaPage.setEntries(entries);

			JsonArray entriesArr = jo.getAsJsonArray("entries");
			Gson gson = new Gson();
			Iterator<JsonElement> ite = entriesArr.iterator();
			while (ite.hasNext()) {
				JsonElement elem = ite.next();
				JsonArray arr = elem.getAsJsonArray();

				DeltaEntry<MetadataEntry> deltaEntry = new DeltaEntry<MetadataEntry>();
				JsonElement lcPathElem = arr.get(0);
				deltaEntry.setLcPath(lcPathElem.getAsString());

				JsonElement metadataPathElem = arr.get(1);
				MetadataEntry metadata = gson.fromJson(metadataPathElem, MetadataEntry.class);
				deltaEntry.setMetadata(metadata);
				entries.add(deltaEntry);
			}
			return deltaPage;
		}
	}

	protected class MetadataListResponseHandler extends RestResponseHandler<List<MetadataEntry>, String> {

		@Override
		public List<MetadataEntry> handle(RestResponse<String> restResponse) {
			checkResponse(restResponse);

			String content = restResponse.getBody();
			JsonParser parser = new JsonParser();
			JsonElement je = parser.parse(content);
			JsonArray entriesArr = je.getAsJsonArray();

			Gson gson = new Gson();
			List<MetadataEntry> entries = new ArrayList<MetadataEntry>();
			Iterator<JsonElement> ite = entriesArr.iterator();
			while (ite.hasNext()) {
				JsonElement elem = ite.next();
				MetadataEntry entry = gson.fromJson(elem, MetadataEntry.class);
				entries.add(entry);
			}
			return entries;
		}
	}
}
