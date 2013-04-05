package mybox.backend.filecruiser;

public class UrlUtil {
	
	public static String buildPath(String... strings) {
		StringBuilder buf = new StringBuilder();
		for (String str: strings) {
			buf.append(str);
		}
		return buf.toString();
	}
}
