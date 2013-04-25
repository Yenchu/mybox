package mybox.backend.filecruiser;

import mybox.util.UrlEncodeUtil;

public abstract class FileCruiserUtil {
	
	public static String[] getHeaders(String token) {
		String[] headers = {Header.X_AUTH_TOKEN, UrlEncodeUtil.encode(token), Header.CONTENT_TYPE, ContentType.JSON};
		return headers;
	}
	
	public static String[] getHeadersWoToken(String token) {
		String[] headers = {Header.CONTENT_TYPE, ContentType.JSON};
		return headers;
	}
	
	public static String[] getHeaders4Upload(String token) {
		String[] headers = {Header.X_AUTH_TOKEN, UrlEncodeUtil.encode(token), Header.CONTENT_TYPE, ContentType.JSON, Header.X_META_FC_COMPRESS, "true", Header.X_META_FC_ENCRYPT, "true"};
		return headers;
	}
}
