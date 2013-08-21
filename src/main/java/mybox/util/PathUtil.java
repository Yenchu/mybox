package mybox.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathUtil {

	private static final Logger log = LoggerFactory.getLogger(PathUtil.class);
	
	public static String getUrl(String url, String resource, String... qryStr) {
		StringBuilder buf = new StringBuilder();
		buf.append(url).append("/").append(EncodeUtil.encodeUrl(resource));
		if (qryStr != null && qryStr.length > 0) {
			buf.append("?").append(EncodeUtil.encodeQueryString(qryStr));
		}
		return buf.toString();
	}
	
	public static String getUrl(String url, String version, String resource, String... qryStr) {
		StringBuilder buf = new StringBuilder();
		buf.append(url).append("/").append(version).append("/").append(EncodeUtil.encodeUrl(resource));
		if (qryStr != null && qryStr.length > 0) {
			buf.append("?").append(EncodeUtil.encodeQueryString(qryStr));
		}
		return buf.toString();
	}
	
	public static String combinePath(String... paths) {
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
	
	public static String getParentPath(String path) {
		if (path.equals("/")) {
			log.warn("Path {} doesn't have a parent!", path);
			return "";
		}

		int idx = path.lastIndexOf('/');
		String parent = null;
		if (idx > 0) {
			parent = path.substring(0, idx);
		} else if (idx == 0) {
			parent = "/";
		} else {
			parent = "";
		}
		return parent;
	}
	
	public static String getLastPath(String path) {
		if (path.equals("/")) {
			log.warn("Path {} doesn't have a name!", path);
			return "";
		}

		int idx = path.lastIndexOf('/');
		String name = null;
		if (idx >= 0) {
			name = path.substring(idx + 1);
		} else {
			name = path;
		}
		return name;
	}

	public static boolean isRoot(String path) {
		return "/".equals(path);
	}
}
