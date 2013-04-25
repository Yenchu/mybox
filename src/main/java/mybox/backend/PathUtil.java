package mybox.backend;

import mybox.util.UrlEncodeUtil;

import org.apache.commons.lang3.StringUtils;

public class PathUtil {

	public static String getUrl(String url, String resource, String... qryStr) {
		StringBuilder buf = new StringBuilder();
		buf.append(url).append("/").append(UrlEncodeUtil.encodeUrl(resource));
		if (qryStr != null && qryStr.length > 0) {
			buf.append("?").append(UrlEncodeUtil.encodeQueryString(qryStr));
		}
		return buf.toString();
	}
	
	public static String getUrl(String url, String version, String resource, String... qryStr) {
		StringBuilder buf = new StringBuilder();
		buf.append(url).append("/").append(version).append("/").append(UrlEncodeUtil.encodeUrl(resource));
		if (qryStr != null && qryStr.length > 0) {
			buf.append("?").append(UrlEncodeUtil.encodeQueryString(qryStr));
		}
		return buf.toString();
	}
	
	public static String buildPath(String... paths) {
		StringBuilder buf = new StringBuilder();
		for (int i = 0, len = paths.length; i < len; i++) {
			String path = paths[i];
			if (StringUtils.isBlank(path)) {
				continue;
			}
			if (path.equals("/")) {
				continue;
			}
			if (buf.length() > 0) {
				char c = buf.charAt(buf.length() - 1);
				if (c != '/') {
					if (path.charAt(0) != '/') {
						buf.append("/");
					}
				} else {
					if (path.charAt(0) == '/') {
						path = path.substring(1);
					}
				}
			}
			buf.append(path);
		}
		return buf.toString();
	}
}
