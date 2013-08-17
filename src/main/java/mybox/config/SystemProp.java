package mybox.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

public class SystemProp {
	
	@Autowired
	private Environment env;

	public String getDropboxAppKey() {
		String value = env.getProperty("dropbox.app.key");
		return value;
	}
	
	public String getDropboxAppSecret() {
		String value = env.getProperty("dropbox.app.secret");
		return value;
	}

	public String getDropboxOauth2RedirectUri() {
		String value = env.getProperty("dropbox.oauth2.redirect.uri");
		return value;
	}
}
