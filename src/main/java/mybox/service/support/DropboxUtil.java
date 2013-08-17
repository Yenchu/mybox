package mybox.service.support;

import java.util.List;

import mybox.model.DropboxUser;
import mybox.to.Params;
import mybox.util.UrlEncodeUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;

public class DropboxUtil {
	
	private static final Logger log = LoggerFactory.getLogger(DropboxUtil.class);

	public static final int DEFAULT_CHUNK_SIZE = 4 * 1024 * 1024; // 4 MB
	
	public static DropboxUser getDefaultUser() {
		String accessKey = "8ekfi8p3nd3h57r";
		String accessSecret = "n8s7nzjquxnma3i";
		DropboxUser user = new DropboxUser();
		user.setAccessKey(accessKey);
		user.setAccessSecret(accessSecret);
		return user;
	}
	
	public static DropboxUser getUser(Params params) {
		DropboxUser user = (DropboxUser) params.getUser();
		return user;
	}
	
	public static String getOauth2AuthorizeUrl(String... qryStr) {
		return buildURL(getAPIServer(), getVersion(), "/oauth2/authorize", qryStr);
	}
	
	public static String getOauth2TokenUrl() {
		return buildURL(getAPIServer(), getVersion(), "/oauth2/token");
	}
	
	public static String getAccountInfoUrl(String... qryStr) {
		return buildURL(getAPIServer(), getVersion(), "/account/info", qryStr);
	}
	
	public static String getMetadataUrl(String root, String path, String... qryStr) {
		return buildURL(getAPIServer(), getVersion(), "/metadata", root, path, qryStr);
	}
	
	public static String getFileUrl(String root, String path, String... qryStr) {
		return buildURL(getContentServer(), getVersion(), "/files", root, path, qryStr);
	}
	
	public static String getThumbnailUrl(String root, String path, String... qryStr) {
		return buildURL(getContentServer(), getVersion(), "/thumbnails", root, path, qryStr);
	}
	
	public static String getFilePostUrl(String root, String path, String... qryStr) {
		return buildURL(getContentServer(), getVersion(), "/files", root, path, qryStr);
	}
	
	public static String getFilePutUrl(String root, String path, String... qryStr) {
		return buildURL(getContentServer(), getVersion(), "/files_put", root, path, qryStr);
	}
	
	public static String getChunkedUploadUrl(String... qryStr) {
		return buildURL(getContentServer(), getVersion(), "/chunked_upload", qryStr);
	}
	
	public static String getCommitChunkedUploadUrl(String root, String path, String... qryStr) {
		return buildURL(getContentServer(), getVersion(), "/commit_chunked_upload", root, path, qryStr);
	}
	
	public static String getDeltaUrl(String... qryStr) {
		return buildURL(getAPIServer(), getVersion(), "/delta", qryStr);
	}
	
	public static String getRevisionUrl(String root, String path, String... qryStr) {
		return buildURL(getAPIServer(), getVersion(), "/revisions", root, path, qryStr);
	}
	
	public static String getRestoreUrl(String root, String path, String... qryStr) {
		return buildURL(getAPIServer(), getVersion(), "/restore", root, path, qryStr);
	}
	
	public static String getShareUrl(String root, String path, String... qryStr) {
		return buildURL(getAPIServer(), getVersion(), "/shares", root, path, qryStr);
	}
	
	public static String getMediaUrl(String root, String path, String... qryStr) {
		return buildURL(getAPIServer(), getVersion(), "/media", root, path, qryStr);
	}
	
	public static String getSearchUrl(String root, String path, String... qryStr) {
		return buildURL(getAPIServer(), getVersion(), "/search", root, path, qryStr);
	}
	
	public static String getCreateFolderUrl(String... qryStr) {
		return buildURL(getAPIServer(), getVersion(), "/fileops/create_folder", qryStr);
	}
	
	public static String getDeleteUrl(String... qryStr) {
		return buildURL(getAPIServer(), getVersion(), "/fileops/delete", qryStr);
	}
	
	public static String getMoveUrl(String... qryStr) {
		return buildURL(getAPIServer(), getVersion(), "/fileops/move", qryStr);
	}
	
	public static String getCopyUrl(String... qryStr) {
		return buildURL(getAPIServer(), getVersion(), "/fileops/copy", qryStr);
	}
	
	public static String getAPIServer() {
		return "api.dropbox.com";
	}
	
	public static String getContentServer() {
		return "api-content.dropbox.com";
	}

	public static String getVersion() {
		return "1";
	}
	
	public static String getRootId() {
		return "dropbox";
	}
	
	public static String getRootName() {
		return "Dropbox";
	}

	public static String[] getAuthHeaders(DropboxUser user, List<String> headers) {
		return getAuthHeaders(user, headers.toArray(new String[headers.size()]));
	}
	
	public static String[] getAuthHeaders(DropboxUser user, String... headers) {
		String[] authHeaders = null;
		if (headers != null && headers.length > 0) {
			authHeaders = new String[headers.length + 2];
			UrlEncodeUtil.encodeHeaders(authHeaders, headers);
		} else {
			authHeaders = new String[2];
		}

		// Oauth
		/*AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
		AccessTokenPair accessTokenPair = new AccessTokenPair(user.getAccessKey(), user.getAccessSecret());
		String authValue = getAuthValue(appKeyPair, accessTokenPair);*/
		
		// Oauth2
		String authName = "Authorization";
		String authValue = "Bearer " + user.getAccessToken();
		
		int idx = authHeaders.length - 2;
		authHeaders[idx] = authName;
		authHeaders[idx + 1] = authValue;
		return authHeaders;
	}

	private static String getAuthValue(AppKeyPair appKeyPair, AccessTokenPair signingTokenPair) {
		StringBuilder buf = new StringBuilder();
		buf.append("OAuth oauth_version=\"1.0\"");
		buf.append(", oauth_signature_method=\"PLAINTEXT\"");
		buf.append(", oauth_consumer_key=\"").append(encode(appKeyPair.key)).append("\"");

		/*
		 * TODO: This is hacky.  The 'signingTokenPair' is null only in auth
		 * step 1, when we acquire a request token.  We really should have two
		 * different buildOAuthHeader functions for the two different
		 * situations.
		 */
		String sig;
		if (signingTokenPair != null) {
			buf.append(", oauth_token=\"").append(encode(signingTokenPair.key)).append("\"");
			sig = encode(appKeyPair.secret) + "&" + encode(signingTokenPair.secret);
		} else {
			sig = encode(appKeyPair.secret) + "&";
		}
		buf.append(", oauth_signature=\"").append(sig).append("\"");

		// Note: Don't need nonce or timestamp since we do everything over SSL.
		return buf.toString();
	}

	private static String buildURL(String host, String apiVersion, String api, String root, String path, String... qryStr) {
		StringBuilder buf = new StringBuilder();
		if (!api.startsWith("/")) {
			buf.append("/");
		}
		buf.append(api).append("/").append(root);
		
		if (path != null && !path.equals("")) {
			if (!path.startsWith("/")) {
				buf.append("/");
			}
			buf.append(path);
		}
		
		String target = buf.toString();
		return buildURL(host, apiVersion, target, qryStr);
	}

	private static String buildURL(String host, String apiVersion, String target, String... qryStr) {
		// We have to encode the whole line, then remove + and / encoding to get a good OAuth URL.
		StringBuilder buf = new StringBuilder();
		buf.append("/").append(apiVersion);
		if (!target.startsWith("/")) {
			buf.append("/");
		}
		buf.append(target);
		
		String path = encode(buf.toString());
		path = path.replace("%2F", "/");

		buf.delete(0, buf.length());
		buf.append(path);
		if (qryStr != null && qryStr.length > 0) {
			buf.append("?").append(encodeQueryString(qryStr));
		}

		// These substitutions must be made to keep OAuth happy.
		path = buf.toString();
		path = path.replace("+", "%20").replace("*", "%2A");
		
		buf.delete(0, buf.length());
		buf.append("https://").append(host).append(":443").append(path);
		return buf.toString();
	}

	private static String encodeQueryString(String[] qryStr) {
		String result = UrlEncodeUtil.encodeQueryString(qryStr);
		result.replace("*", "%2A");
		return result;
	}
	
	private static String encode(String s) {
		return UrlEncodeUtil.encode(s);
	}
}
