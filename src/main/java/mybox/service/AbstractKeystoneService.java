package mybox.service;

import org.springframework.beans.factory.annotation.Autowired;

import mybox.backend.PathUtil;
import mybox.config.SystemProp;

public abstract class AbstractKeystoneService {

	@Autowired
	protected SystemProp systemProp;
	
	protected String getAdminToken() {
		String token = systemProp.getAdminToken();
		return token;
	}
	
	protected String getAdminServiceUrl(String resource, String... qryStr) {
		String url = systemProp.getAdminServiceUrl();
		return PathUtil.getUrl(url, resource, qryStr);
	}
	
	protected String getUserServiceUrl(String resource, String... qryStr) {
		String url = systemProp.getUserServiceUrl();
		return PathUtil.getUrl(url, resource, qryStr);
	}
}
