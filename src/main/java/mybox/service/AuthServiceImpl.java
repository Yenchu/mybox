package mybox.service;

import static mybox.util.DropboxUtil.getAccountInfoUrl;
import static mybox.util.DropboxUtil.getAuthHeaders;
import static mybox.util.DropboxUtil.getOauth2TokenUrl;

import java.util.Arrays;
import java.util.List;

import mybox.config.SystemProp;
import mybox.model.AccountInfo;
import mybox.model.Token;
import mybox.model.User;
import mybox.rest.RestClient;
import mybox.rest.RestClientFactory;
import mybox.util.DropboxUtil;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dropbox.core.util.StringUtil;

@Service
public class AuthServiceImpl implements AuthService {
	
	private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
	
	@Autowired
	private SystemProp systemProp;
	
	private RestClient restClient = RestClientFactory.getRestClient();

	public boolean isLogin(User user) {
		if (user == null) {
			return false;
		}
		
		String token = user.getAccessToken();
		if (StringUtils.isBlank(token)) {
			return false;
		}
		
		// not impl completely
		return true;
	}
	
	public String getAuthorizingUrl() {
		String[] qryStr = new String[]{"response_type", "code", "state", "test", 
				"client_id", systemProp.getDropboxAppKey(), "redirect_uri", systemProp.getDropboxOauth2RedirectUri()};
		String url = DropboxUtil.getOauth2AuthorizeUrl(qryStr);
		return url;
	}
	
	public Token getToken(String code) {
		String[] params = new String[]{"code", code, "grant_type", "authorization_code", 
				"redirect_uri", systemProp.getDropboxOauth2RedirectUri()};
		List<String> fields = Arrays.asList(params);

		String credentials = systemProp.getDropboxAppKey() + ":" + systemProp.getDropboxAppSecret();
        String base64Credentials = StringUtil.base64Encode(StringUtil.stringToUtf8(credentials));
        String[] headers = {"Authorization", "Basic " + base64Credentials};
        
		String url = getOauth2TokenUrl();
		Token token = restClient.post(Token.class, url, fields, headers);
		log.debug("token: {}", token);
		return token;
	}
	
	public AccountInfo getAccountInfo(String accessToken) {
		String url = getAccountInfoUrl();
		String[] headers = getAuthHeaders(accessToken);
		AccountInfo accountInfo = restClient.get(AccountInfo.class, url, headers);
		log.debug("accountInfo: {}", accountInfo);
		return accountInfo;
	}
	
	public User getUser(Token token) {
		User user = new User();
		user.setToken(token);
		
		String accessToken = token.getAccessToken();
		AccountInfo accountInfo = getAccountInfo(accessToken);
		user.setAccountInfo(accountInfo);
		return user;
	}
}
