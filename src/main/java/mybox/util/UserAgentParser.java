package mybox.util;

import javax.servlet.http.HttpServletRequest;

public class UserAgentParser {
	
	public static boolean isIE(String userAgent) {
		//Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Win64; x64; Trident/5.0),
		if (userAgent.indexOf("MSIE") >= 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isIE(HttpServletRequest request) {
		String userAgent = request.getHeader("user-agent");
		return isIE(userAgent);
	}
	
	public static boolean isSafari(String userAgent) {
		//Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.57.2 (KHTML, like Gecko) Version/5.1.7 Safari/534.57.2
		if (userAgent.indexOf("Safari") >= 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isSafari(HttpServletRequest request) {
		String userAgent = request.getHeader("user-agent");
		return isSafari(userAgent);
	}
}
