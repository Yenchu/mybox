package mybox.service.support;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mybox.model.DropboxUser;
import mybox.to.UploadParams;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.WebAuthSession;
import com.dropbox.client2.session.Session.AccessType;

public class DropboxSample {
	
	private static final Logger log = LoggerFactory.getLogger(DropboxSample.class);

	public static final AccessType ACCESS_TYPE = AccessType.DROPBOX;

	public static final String APP_KEY = "k3e0ss2wkrf5ltp";

	public static final String APP_SECRET = "y1x9tnk0cjn9ooy";
	
	// methods using Dropbox API for test
	public static void auth() {
		try {
			AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
			WebAuthSession session = new WebAuthSession(appKeyPair, AccessType.APP_FOLDER);
			WebAuthSession.WebAuthInfo authInfo = session.getAuthInfo();
			log.debug("Dropbox auth url: {}", authInfo.url);

			// just to get url from console
			try {
				Thread.sleep(20 * 1000);
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
			}

			session.retrieveWebAccessToken(authInfo.requestTokenPair);
			AccessTokenPair accessToken = session.getAccessTokenPair();
			log.debug("accessKey={}, accessSecret={}", accessToken.key, accessToken.secret);
		} catch (DropboxException e) {
			log.error(e.getMessage(), e);
		}
	}

	public static DropboxAPI.Account getAccount(DropboxUser user) {
		DropboxAPI.Account account = null;
		try {
			DropboxAPI<?> client = getDropboxClient(user.getAccessKey(), user.getAccessSecret());
			account = client.accountInfo();
		} catch (DropboxException e) {
			log.error(e.getMessage(), e);
		}
		return account;
	}

	public static void putFileOverwrite(UploadParams params) {
		try {
			DropboxUser user = (DropboxUser) params.getUser();
			String path = params.getPath();
			InputStream is = params.getContent();
			long length = params.getLength();

			DropboxAPI<?> client = getDropboxClient(user.getAccessKey(), user.getAccessSecret());
			client.putFileOverwrite(path, is, length, null);
		} catch (DropboxException e) {
			log.error(e.getMessage(), e);
		}
	}
	
	public static DropboxAPI<?> getDropboxClient(String accessKey, String accessSecret) {
		AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
		WebAuthSession session = new WebAuthSession(appKeyPair, ACCESS_TYPE);
		AccessTokenPair accessTokenPair = new AccessTokenPair(accessKey, accessSecret);
		session.setAccessTokenPair(accessTokenPair);
		DropboxAPI<?> client = new DropboxAPI<WebAuthSession>(session);
		return client;
	}
}
