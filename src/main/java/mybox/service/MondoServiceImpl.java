package mybox.service;

import static mybox.backend.mondo.MondoUtil.getQueryString;
import static mybox.backend.mondo.MondoUtil.getSignedHeaders;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mybox.backend.mondo.HttpHeader;
import mybox.backend.mondo.MondoUtil;
import mybox.backend.mondo.QueryString;
import mybox.exception.Error;
import mybox.exception.ErrorException;
import mybox.model.ChunkedUploadParams;
import mybox.model.CopyParams;
import mybox.model.CreateParams;
import mybox.model.DeleteParams;
import mybox.model.DeltaParams;
import mybox.model.EntryParams;
import mybox.model.LinkParams;
import mybox.model.LoginParams;
import mybox.model.MetadataParams;
import mybox.model.MoveParams;
import mybox.model.Params;
import mybox.model.PathParams;
import mybox.model.RevisionParams;
import mybox.model.SearchParams;
import mybox.model.Space;
import mybox.model.ThumbnailParams;
import mybox.model.UploadParams;
import mybox.model.dropbox.DeltaPage;
import mybox.model.dropbox.FileEntry;
import mybox.model.dropbox.Link;
import mybox.model.dropbox.MetadataEntry;
import mybox.model.mondo.Account;
import mybox.model.mondo.Group;
import mybox.model.mondo.MondoUser;
import mybox.rest.RestResponse;
import mybox.rest.RestClient;
import mybox.to.FileOperationResponse;
import mybox.util.FileUtil;
import mybox.util.HttpUtil;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

@Service
public class MondoServiceImpl extends AbstractFileService implements MondoService {

	private static final Logger log = LoggerFactory.getLogger(MondoServiceImpl.class);
	
	@Autowired
	protected Environment env;
	
	@Autowired
	private RestClient restClient;
	
	public MondoUser auth(LoginParams params) {
		String url = getServiceUrl("auth");
		String[] headers = HttpUtil.encodedHeaders(HttpHeader.AUTH_USER.value(), params.getUsername(), HttpHeader.AUTH_PASSWD.value(), params.getPassword());
		RestResponse<String> restResponse = restClient.post(url, headers);
		
		String token = restResponse.getHeader(HttpHeader.AUTH_TOKEN.value());
		log.debug("token: {}", token);
		if (StringUtils.isBlank(token)) {
			throw new ErrorException(Error.unauthorized());
		}
		
		Account account = getAccount(token);
		MondoUser user = new MondoUser(account, token);
		user.setName(params.getUsername());
		user.setAddress(params.getAddress());
		return user;
	}
	
	public Account getAccount(String token) {
		String url = getServiceUrl("account/info");
		String[] headers = HttpUtil.encodedHeaders(HttpHeader.AUTH_TOKEN.value(), token);
		Account account = restClient.get(Account.class, url, headers);
		customGroups(account.getGroups());
		return account;
	}
	
	public List<Group> getGroups(MondoUser user) {
		String url = getServiceUrl("groups/list");
		String[] headers = getSignedHeaders(user);

		String offset = "0";
		String limitSize = "1000";
		Map<String, String> qryStr = getQueryString(QueryString.OFFSET.value(), offset, QueryString.LIMIT_SIZE.value(), limitSize);
		
		RestResponse<String> restResponse = restClient.get(url, qryStr, headers);
		List<Group> groups = parseListResponse(Group.class, restResponse.getBody());
		customGroups(groups);
		return groups;
	}

	public Space getDefaultSpace(Params params) {
		MondoUser user = getUser(params);
		String userName = user.getName();
		Account account = user.getAccount();
		List<Group> groups = account.getGroups();
		
		Group defaultGroup = null;
		for (Group group: groups) {
			String groupName = group.getName();
			if (userName.equals(groupName)) {
				defaultGroup = group;
				customGroup(defaultGroup);
				break;
			}
		}
		return defaultGroup;
	}
	
	public Space getSpace(Params params, String spaceId) {
		if (StringUtils.isBlank(spaceId)) {
			return getDefaultSpace(params);
		}
		
		MondoUser user = getUser(params);
		Account account = user.getAccount();
		List<Group> groups = account.getGroups();
		
		Group rtGroup = null;
		for (Group group: groups) {
			String groupId = group.getId();
			if (spaceId.equals(groupId)) {
				rtGroup = group;
				customGroup(rtGroup);
				break;
			}
		}
		return rtGroup;
	}
	
	public Space getSpace(PathParams params) {
		return getSpace(params, params.getRoot());
	}
	
	public MetadataEntry getFiles(PathParams params) {
		MondoUser user = getUser(params);
		Space space = getSpace(params);
		String root = params.getRoot();
		String path = params.getPath();
		
		String url = getServiceUrl("metadata" + path);
		String[] headers = getSignedHeaders(user, HttpHeader.GROUP_ID.value(), root);
		
		MetadataEntry entry = restClient.get(MetadataEntry.class, url, headers);
		customEntries(space, entry);
		return entry;
	}

	public MetadataEntry getFiles(MetadataParams params) {
		MondoUser user = getUser(params);
		Space space = getSpace(params);
		String root = params.getRoot();
		String path = params.getPath();
		String[] qryStr = params.getParamArray();
		
		String url = getServiceUrl("metadata" + path, qryStr);
		String[] headers = getSignedHeaders(user, HttpHeader.GROUP_ID.value(), root);
		
		MetadataEntry entry = restClient.get(MetadataEntry.class, url, headers);
		customEntries(space, entry);
		return entry;
	}

	public MetadataEntry getFolders(MetadataParams params) {
		MetadataEntry parentEntry = getFiles(params);
		List<MetadataEntry> folderEntries = getFolders(parentEntry.getContents());
		parentEntry.setContents(folderEntries);
		return parentEntry;
	}
	
	public MetadataEntry upload(UploadParams params) {
		MondoUser user = getUser(params);
		String root = params.getRoot();
		String path = params.getPath();
		InputStream is = params.getContent();
		long length = params.getLength();
		String[] qryStr = params.getParamArray();

		String url = getServiceUrl("files_put" + path, qryStr);
		String[] headers = getSignedHeaders(user, HttpHeader.GROUP_ID.value(), root);

		MetadataEntry entry = restClient.put(MetadataEntry.class, url, is, length, headers);
		customEntry(entry);
		return entry;
	}

	public FileEntry download(EntryParams params) {
		MondoUser user = getUser(params);
		String root = params.getRoot();
		String path = params.getPath();
		String[] qryStr = params.getParamArray();

		String url = getServiceUrl("files" + path, qryStr);
		String[] headers = getSignedHeaders(user, HttpHeader.GROUP_ID.value(), root);

		RestResponse<InputStream> restResponse = restClient.getStream(url, headers);
		FileEntry entry = convertDownloadResponse(restResponse, "x-mondo-metadata");
		return entry;
	}

	public FileOperationResponse createFolder(CreateParams params) {
		MondoUser user = getUser(params);
		String root = params.getRoot();
		List<String> fields = params.getParamList();
		
		String url = getServiceUrl("fileops/create_folder");
		String[] headers = getSignedHeaders(user, HttpHeader.GROUP_ID.value(), root);
		
		MetadataEntry entry = restClient.post(MetadataEntry.class, url, fields, headers);
		customEntry(entry);
		
		String name = FileUtil.getNameFromPath(params.getPath());
		FileOperationResponse resp = new FileOperationResponse(name);
		resp.setMetadata(entry);
		return resp;
	}

	protected <T> List<T> parseListResponse(Class<T> clazz, String content) {
		JsonParser parser = new JsonParser();
		JsonElement je = parser.parse(content);
		JsonArray ja = je.getAsJsonArray();
		Gson gson = new Gson();
		
		List<T> entities = new ArrayList<T>();
		Iterator<JsonElement> ite = ja.iterator();
		while (ite.hasNext()) {
			JsonElement groupElem = ite.next();
			T entity = gson.fromJson(groupElem, clazz);
			entities.add(entity);
		}
		return entities;
	}
	
	protected void customGroups(List<Group> groups) {
		if (groups == null || groups.size() <= 0) {
			return;
		}
		for (Group group: groups) {
			customGroup(group);
		}
	}

	protected void customGroup(Group group) {
		group.setRoot("/");
		
		int expire = group.getExpiration();
		DateTime date = new DateTime(expire * 1000L);
		group.setExpiryDate(date.toString());
	}
	
	@Override
	protected void customEntry(MetadataEntry entry) {
		super.customEntry(entry);
		
		String modified = entry.getModified();
		if (modified == null || modified.equals("")) {
			return;
		}
		
		entry.setModified(modified);
		// modified data format: 1355367079.39390
		//Double dTime = Double.parseDouble(modified);
		//Long lTime = Math.round(dTime * 1000L);
		//DateTime date = new DateTime(lTime);
		//entry.setModified(date.toString());
	}
	
	protected MondoUser getUser(Params params) {
		MondoUser user = (MondoUser) params.getUser();
		return user;
	}
	
	protected String getServiceUrl(String resource, String... qryStr) {
		String ip = env.getProperty("mondo.ip");
		String port = env.getProperty("mondo.port");
		StringBuilder buf = new StringBuilder();
		buf.append("http://").append(ip).append(":").append(port)
			.append("/").append(getVersion()).append("/").append(MondoUtil.encodeUrl(resource));
		if (qryStr != null && qryStr.length > 0) {
			buf.append("?").append(HttpUtil.encodeQueryString(qryStr));
		}
		return buf.toString();
	}
	
	protected String getVersion() {
		return "1";
	}

	@Override
	public InputStream getThumbnail(ThumbnailParams params) {
		// TODO Auto-generated method stub
		return null;
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
	public List<FileOperationResponse> delete(DeleteParams delParams) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<FileOperationResponse> move(MoveParams moveParams) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<FileOperationResponse> copy(CopyParams params) {
		// TODO Auto-generated method stub
		return null;
	}
}
