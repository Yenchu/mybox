package mybox.util;

import java.util.List;

public class DropboxUtil {
	
	public static final String ROOT = "dropbox";
	
	public static String getOauth2AuthorizeUrl(String... qryStr) {
		return buildURL(getAPIServer(), getVersion(), "/oauth2/authorize", qryStr);
	}
	
	public static String getOauth2TokenUrl() {
		return buildURL(getAPIServer(), getVersion(), "/oauth2/token");
	}
	
	public static String getAccountInfoUrl(String... qryStr) {
		return buildURL(getAPIServer(), getVersion(), "/account/info", qryStr);
	}
	
	public static String getMetadataUrl(String path, String... qryStr) {
		return buildURL(getAPIServer(), getVersion(), "/metadata", ROOT, path, qryStr);
	}
	
	public static String getFileUrl(String path, String... qryStr) {
		return buildURL(getContentServer(), getVersion(), "/files", ROOT, path, qryStr);
	}
	
	public static String getThumbnailUrl(String path, String... qryStr) {
		return buildURL(getContentServer(), getVersion(), "/thumbnails", ROOT, path, qryStr);
	}
	
	public static String getFilePostUrl(String path, String... qryStr) {
		return buildURL(getContentServer(), getVersion(), "/files", ROOT, path, qryStr);
	}
	
	public static String getFilePutUrl(String path, String... qryStr) {
		return buildURL(getContentServer(), getVersion(), "/files_put", ROOT, path, qryStr);
	}
	
	public static String getChunkedUploadUrl(String... qryStr) {
		return buildURL(getContentServer(), getVersion(), "/chunked_upload", qryStr);
	}
	
	public static String getCommitChunkedUploadUrl(String path, String... qryStr) {
		return buildURL(getContentServer(), getVersion(), "/commit_chunked_upload", ROOT, path, qryStr);
	}
	
	public static String getDeltaUrl(String... qryStr) {
		return buildURL(getAPIServer(), getVersion(), "/delta", qryStr);
	}
	
	public static String getRevisionUrl(String path, String... qryStr) {
		return buildURL(getAPIServer(), getVersion(), "/revisions", ROOT, path, qryStr);
	}
	
	public static String getRestoreUrl(String path, String... qryStr) {
		return buildURL(getAPIServer(), getVersion(), "/restore", ROOT, path, qryStr);
	}
	
	public static String getShareUrl(String path, String... qryStr) {
		return buildURL(getAPIServer(), getVersion(), "/shares", ROOT, path, qryStr);
	}
	
	public static String getMediaUrl(String path, String... qryStr) {
		return buildURL(getAPIServer(), getVersion(), "/media", ROOT, path, qryStr);
	}
	
	public static String getSearchUrl(String path, String... qryStr) {
		return buildURL(getAPIServer(), getVersion(), "/search", ROOT, path, qryStr);
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

	public static String[] getAuthHeaders(String accessToken, List<String> headers) {
		return getAuthHeaders(accessToken, headers.toArray(new String[headers.size()]));
	}
	
	public static String[] getAuthHeaders(String accessToken, String... headers) {
		String[] authHeaders = null;
		if (headers != null && headers.length > 0) {
			authHeaders = new String[headers.length + 2];
			EncodeUtil.encodeHeaders(authHeaders, headers);
		} else {
			authHeaders = new String[2];
		}

		String authName = "Authorization";
		String authValue = "Bearer " + accessToken;
		
		int idx = authHeaders.length - 2;
		authHeaders[idx] = authName;
		authHeaders[idx + 1] = authValue;
		return authHeaders;
	}

	private static String buildURL(String host, String apiVersion, String api, String root, String path, String... qryStr) {
		StringBuilder buf = new StringBuilder();
		if (!api.startsWith("/")) {
			buf.append("/");
		}
		buf.append(api);
		
		if (root != null && !root.equals("")) {
			buf.append("/").append(root);
		}
		
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
		String result = EncodeUtil.encodeQueryString(qryStr);
		result.replace("*", "%2A");
		return result;
	}
	
	private static String encode(String s) {
		return EncodeUtil.encode(s);
	}
}
