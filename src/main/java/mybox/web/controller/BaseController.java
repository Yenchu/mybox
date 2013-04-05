package mybox.web.controller;

import javax.servlet.http.HttpServletRequest;

import mybox.to.Notice;
import mybox.util.HttpUtil;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseController {

	private static final Logger log = LoggerFactory.getLogger(BaseController.class);
	
	protected String getRestOfPath(HttpServletRequest request, int startIndex) {
		String path = request.getRequestURI();
		path = path.substring(request.getContextPath().length());
		path = decodeUrl(path);
		
		String restPath = null;
		if (path.length() > startIndex) {
			restPath = path.substring(startIndex);
		}
		//log.debug("startIndex={}, path={}, restPath={}", startIndex, path, restPath);
		return restPath;
	}
	
	protected String[] decodeUrl(String[] encodedPaths) {
		int len = encodedPaths.length;
		String[] pathes = new String[len];
		for (int i = 0; i < len; i++) {
			pathes[i] = decodeUrl(encodedPaths[i]);
		}
		return pathes;
	}
	
	protected String decodeUrl(String encodedPath) {
		String path = null;
		if (StringUtils.isNotBlank(encodedPath)) {
			path = HttpUtil.decodeUrl(encodedPath);
		}
		return path;
	}
	
	protected String info(HttpServletRequest request, String page, String... messages) {
		notifyInfo(request, messages);
		return page;
	}
	
	protected String error(HttpServletRequest request, String page, String... messages) {
		notifyError(request, messages);
		return page;
	}
	
	protected void notifyInfo(HttpServletRequest request, String... messages) {
		Notice notice = null;
		if (messages != null) {
			if (messages.length > 1) {
				StringBuilder sb = new StringBuilder();
				for (String message: messages) {
					if (message != null) {
						sb.append(message);
					}
				}
				notice = Notice.info(sb.toString());
			} else {
				notice = Notice.info(messages[0]);
			}
		} else {
			notice = Notice.info();
		}
		request.setAttribute("notice", notice);
	}
	
	protected void notifyError(HttpServletRequest request, String... messages) {
		Notice notice = null;
		if (messages != null) {
			if (messages.length > 1) {
				StringBuilder sb = new StringBuilder();
				for (String message: messages) {
					if (message != null) {
						sb.append(message);
					}
				}
				notice = Notice.error(sb.toString());
			} else {
				notice = Notice.error(messages[0]);
			}
		} else {
			notice = Notice.error();
		}
		request.setAttribute("notice", notice);
	}
}
