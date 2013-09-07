package mybox.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mybox.model.User;
import mybox.service.AuthService;
import mybox.util.WebUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class AppInterceptor extends HandlerInterceptorAdapter {

	private static final Logger log = LoggerFactory.getLogger(AppInterceptor.class);
	
	@Autowired
	private AuthService authService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		User user = WebUtil.getUser(request);
		if (authService.isLogin(user)) {
			return true;
		}
		
		String path = WebUtil.getPathAfterContextPath(request);
		if (path.endsWith("/oauth2/code")) {
			return true;
		}
		
		log.debug("Request {} from {} isn't login!", request.getRequestURI(), WebUtil.getUserAddress(request));
		String redirectUrl = authService.getAuthorizingUrl();
		response.sendRedirect(redirectUrl);
		return false;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		String contextPath = request.getContextPath();
		request.setAttribute("contextPath", contextPath);
		request.setAttribute("service", contextPath);
		
		String asset = contextPath + "/public";
		request.setAttribute("asset", asset);
	}
}
