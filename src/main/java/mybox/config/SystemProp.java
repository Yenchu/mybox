package mybox.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

public class SystemProp {
	
	@Autowired
	private Environment env;

	public String getAdminToken() {
		String token = env.getProperty("admin.token");
		return token;
	}
	
	public String getDefaultDomain() {
		String url = env.getProperty("default.domain");
		return url;
	}

	public String getAdminServiceUrl() {
		String url = env.getProperty("admin.service.url");
		return url;
	}
	
	public String getUserServiceUrl() {
		String url = env.getProperty("user.service.url");
		return url;
	}
	
	public String getFileServiceUrl() {
		String url = env.getProperty("file.service.url");
		return url;
	}
	
	public String getFileServiceName() {
		String url = env.getProperty("file.service.name");
		return url;
	}
	
	public String getUserPortalUrl() {
		String url = env.getProperty("user.portal.url");
		return url;
	}
	
	public String getUserPortalName() {
		String url = env.getProperty("user.portal.name");
		return url;
	}
}
