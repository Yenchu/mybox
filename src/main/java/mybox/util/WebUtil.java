package mybox.util;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebUtil {

	private static final Logger log = LoggerFactory.getLogger(WebUtil.class);
	
	public static String getUserAddress(HttpServletRequest request) {
		// if there isn't the X-Forwarded-For header, get user address instead 
		String userAddress = request.getHeader("X-Forwarded-For");
		if (userAddress == null || userAddress.equals("")) {
			userAddress = request.getRemoteAddr();
		}
		return userAddress;
	}
	
	public static String getFirstPathAfterContextPath(HttpServletRequest request) {
		//* this path is service path (similar to servlet path)
		String requestUri = request.getRequestURI();
		String contextPath = request.getContextPath();
		String pathWoContext = requestUri.substring(contextPath.length());
		
		String path = null;
		int idx = pathWoContext.indexOf("/", 1);
		if (idx >= 1) {
			path = pathWoContext.substring(0, idx);
		} else {
			path = pathWoContext;
		}
		return path;
	}
	
	public static String getPathAfterServicePath(HttpServletRequest request) {
		//* this path is similar to path info
		String requestUri = request.getRequestURI();
		String contextPath = request.getContextPath();
		String pathWoContext = requestUri.substring(contextPath.length());
		
		String path = null;
		int idx = pathWoContext.indexOf("/", 1);
		if (idx >= 1) {
			path = pathWoContext.substring(idx);
		} else {
			path = pathWoContext;
		}
		return path;
	}
	
	
	/**
	 * Tomcat servlet container uses ISO-8859-1 to decode URL.
	 * @param id
	 * @return
	 */
	protected String pathDecode(String encodedPath) {
		String path = null;
		if (StringUtils.isNotBlank(encodedPath)) {
			try {
				path = new String(encodedPath.getBytes("ISO-8859-1"), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				log.error(e.getMessage(), e);
			}
		}
		return path;
	}
	
	public static void logParameters(HttpServletRequest request) {
        StringBuilder buffer = new StringBuilder("\n");
        Map<String, String[]> paramMap = request.getParameterMap();
        Iterator<Entry<String, String[]>> ite = paramMap.entrySet().iterator();
        while (ite.hasNext()) {
            Map.Entry<String, String[]> entry = (Map.Entry<String, String[]>) ite.next();
            String key = entry.getKey();
            String[] values = entry.getValue();
            
            buffer.append(key).append("=");
            if (values.length > 1) {
            	buffer.append("[");
	            for (int i = 0; i < values.length; i++) {
	                buffer.append(values[i]);
	                if (i + 1 < values.length) {
	                    buffer.append(",");
	                }
	            }
	            buffer.append("]");
            } else {
            	 buffer.append(values[0]);
            }
            buffer.append(" ");
        }
        log.debug("RequestParametes: {}", buffer.toString());
    }
	
	public static void logHeaders(HttpServletRequest request) {
		StringBuilder buf = new StringBuilder();
		Enumeration<String> enumer = request.getHeaderNames();
		while (enumer.hasMoreElements()) {
			String name = enumer.nextElement();
			buf.append(name).append("=");
			Enumeration<String> enumerVal = request.getHeaders(name);
			while (enumerVal.hasMoreElements()) {
				String value = enumerVal.nextElement();
				buf.append(value).append(",");
			}
			buf.append("\n");
		}
		log.debug("\n{}", buf.toString());
	}
}
