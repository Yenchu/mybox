package mybox.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import mybox.exception.Error;
import mybox.exception.ErrorException;
import mybox.json.JsonConverter;
import mybox.model.User;
import mybox.to.Notice;
import mybox.util.HttpUtil;
import mybox.util.WebUtil;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

public class BaseController {

	private static final Logger log = LoggerFactory.getLogger(BaseController.class);
	
	protected User getUser(HttpServletRequest request) {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		return user;
	}
	
	protected void setUser(HttpServletRequest request, User user) {
		HttpSession session = request.getSession();
		session.setAttribute("user", user);
	}
	
	protected String getRestOfPath(HttpServletRequest request, int startIndex) {
		String path = request.getRequestURI();
		path = path.substring(request.getContextPath().length());
		path = urlDecode(path);
		
		String restPath = null;
		if (path.length() > startIndex) {
			restPath = path.substring(startIndex);
		}
		//log.debug("startIndex={}, path={}, restPath={}", startIndex, path, restPath);
		return restPath;
	}
	
	protected String[] urlDecode(String[] encodedPaths) {
		int len = encodedPaths.length;
		String[] pathes = new String[len];
		for (int i = 0; i < len; i++) {
			pathes[i] = urlDecode(encodedPaths[i]);
		}
		return pathes;
	}
	
	protected String urlDecode(String encodedPath) {
		String path = null;
		if (StringUtils.isNotBlank(encodedPath)) {
			path = HttpUtil.decode(encodedPath);
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
	
	@ExceptionHandler(ErrorException.class)
	@ResponseBody
	public ResponseEntity<String> handleFaultException(ErrorException e, HttpServletRequest request) {
		Error error = e.getError();
		log.warn("Got error when handling request {} from {}: {}", new Object[]{request.getRequestURI(), WebUtil.getUserAddress(request), error});
		return handleResponse(request, error);
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseBody
	public ResponseEntity<String> handleException(Exception e, HttpServletRequest request) {
		log.error(e.getMessage(), e);
		log.error("Got exception when handling request {} from {}: {}", new Object[]{request.getRequestURI(), WebUtil.getUserAddress(request), e.getMessage()});
		Error error = Error.internalServerError(e.getMessage());
		return handleResponse(request, error);
	}
	
	protected ResponseEntity<String> handleResponse(HttpServletRequest request, Error fault) {
		String body = JsonConverter.toJson(fault);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		ResponseEntity<String> responseEntity = new ResponseEntity<String>(body, headers, HttpStatus.valueOf(fault.getCode()));
		return responseEntity;
	}
}
