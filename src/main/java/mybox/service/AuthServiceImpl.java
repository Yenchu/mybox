package mybox.service;

import mybox.config.SystemProp;
import mybox.model.User;
import mybox.service.support.DropboxUtil;
import mybox.type.ServiceType;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
	
	@Autowired
	private SystemProp systemProp;

	public boolean isLogin(String serviceType, User user) {
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
	
	public String getAuthUrl(String serviceType) {
		if (StringUtils.isBlank(serviceType)) {
			serviceType = ServiceType.DROPBOX.value();
		}
		
		String[] qryStr = new String[]{"response_type", "code", "state", "test", 
				"client_id", systemProp.getDropboxAppKey(), "redirect_uri", systemProp.getDropboxOauth2RedirectUri()};
		String url = DropboxUtil.getOauth2AuthorizeUrl(qryStr);
		
		// not impl completely
		return url;
	}
}
