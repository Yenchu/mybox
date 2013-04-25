package mybox.service;

import java.util.List;

import mybox.backend.PathUtil;
import mybox.backend.filecruiser.ContentType;
import mybox.backend.filecruiser.FileCruiserUtil;
import mybox.backend.filecruiser.Header;
import mybox.backend.filecruiser.Resource;
import mybox.json.JsonConverter;
import mybox.model.filecruiser.FileCruiserUser;
import mybox.model.keystone.Auth;
import mybox.model.keystone.Project;
import mybox.model.keystone.Token;
import mybox.model.keystone.User;
import mybox.model.keystone.Users;
import mybox.rest.RestClient;
import mybox.rest.RestClientFactory;
import mybox.rest.RestResponse;
import mybox.to.LoginParams;
import mybox.to.Params;
import mybox.util.UrlEncodeUtil;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends AbstractKeystoneService implements UserService {

	private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
	
	protected RestClient restClient = RestClientFactory.getRestClient();
	
	public FileCruiserUser auth(LoginParams params) {
		String domainName = params.getDomain();
		if (StringUtils.isBlank(domainName)) {
			//* default domain for demo, it should be changed in the future
			domainName = systemProp.getDefaultDomain();
		}

		String url = getUserServiceUrl(Resource.TOKENS);
		String[] headers = {Header.CONTENT_TYPE, ContentType.JSON};
		
		Auth auth = new Auth(domainName, params.getUsername(), params.getPassword());
		String body = JsonConverter.toJson(auth, true);
		log.debug("authUrl: {}  body: {}", url, body);
		
		RestResponse<String> restResponse = restClient.post(url, body, headers);
		return convertToUser(restResponse);
	}
	
	public FileCruiserUser validate(String token) {
		String url = getUserServiceUrl(Resource.TOKENS);
		token = UrlEncodeUtil.encode(token);
		String[] headers = {Header.X_AUTH_TOKEN, token, Header.X_SUBJECT_TOKEN, token, Header.CONTENT_TYPE, ContentType.JSON};
		log.debug("validateUrl: {}  token: {}", url, token);
		
		RestResponse<String> restResponse = restClient.get(url, headers);
		return convertToUser(restResponse);
	}

	private FileCruiserUser convertToUser(RestResponse<String> restResponse) {
		Token token = JsonConverter.fromJson(restResponse.getBody(), Token.class, true);
		String tokenString = restResponse.getHeader(Header.X_SUBJECT_TOKEN);
		
		DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
		DateTime dt = fmt.parseDateTime(token.getExpiresAt());
		
		FileCruiserUser user = new FileCruiserUser();
		user.setToken(tokenString);
		user.setExpiresAt(dt.toDate());
		user.setId(token.getUser().getId());
		user.setName(token.getUser().getName());
		user.setDomainId(token.getUser().getDomain().getId());
		user.setDomainName(token.getUser().getDomain().getName());
		return user;
	}
	
	public List<User> getUsers() {
		String token = getAdminToken();
		String url = getUserServiceUrl(Resource.USERS);
		String[] headers = FileCruiserUtil.getHeaders(token);
		Users users = restClient.get(Users.class, url, headers);
		List<User> userList = users.getUsers();
		return userList;
	}
	
	public User getUser(String userId) {
		String token = getAdminToken();
		String resource = PathUtil.buildPath(Resource.USERS, userId);
		String url = getUserServiceUrl(resource);
		String[] headers = FileCruiserUtil.getHeaders(token);
		User rtUser = get(User.class, url, headers, true);
		return rtUser;
	}
	
	public Project getProject(Params params, String spaceId) {
		FileCruiserUser fcUser = (FileCruiserUser) params.getUser();
		String projectId;
		if (StringUtils.isBlank(spaceId)) {
			User user = getUser(fcUser.getId());
			projectId = user.getDefaultProjectId();
		} else {
			projectId = spaceId;
		}
		
		String token = fcUser.getToken();
		String resource = PathUtil.buildPath(Resource.PROJECTS, projectId);
		String url = getUserServiceUrl(resource);
		String[] headers = FileCruiserUtil.getHeaders(token);
		Project project = get(Project.class, url, headers, true);
		return project;
	}
	
	protected <T> T get(Class<T> clazz, String url, String[] headers, boolean hasJsonRoot) {
		RestResponse<String> restResponse = restClient.get(url, headers);
		return JsonConverter.fromJson(restResponse.getBody(), clazz, hasJsonRoot);
	}
}
