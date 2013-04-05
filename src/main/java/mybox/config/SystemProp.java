package mybox.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

public class SystemProp {
	
	@Autowired
	private Environment env;
	
	public String getVersion() {
		String version = env.getProperty("version");
		return version;
	}

	public String getAdminToken() {
		String token = env.getProperty("admin.token");
		return token;
	}

	public String getAdminUrl() {
		String url = env.getProperty("admin.url");
		return url;
	}
	
	public String getUserUrl() {
		String url = env.getProperty("user.url");
		return url;
	}
	
	public String getFileUrl() {
		String url = env.getProperty("file.url");
		return url;
	}
	
	public String getDefaultDomain() {
		String url = env.getProperty("default.domain");
		return url;
	}
}
