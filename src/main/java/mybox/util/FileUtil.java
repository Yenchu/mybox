package mybox.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtil {

	private static final Logger log = LoggerFactory.getLogger(FileUtil.class);

	public static String getNameFromPath(String path) {
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

	public static String getParentFromPath(String path) {
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

	public static String getFilePath(String folderPath, String fileName) {
		String filePath = null;
		if (!folderPath.equals("/")) {
			filePath = folderPath + "/" + fileName;
		} else {
			filePath = "/" + fileName;
		}
		return filePath;
	}

	public static boolean isRoot(String path) {
		if ("/".equals(path)) {
			return true;
		} else {
			return false;
		}
	}
}
