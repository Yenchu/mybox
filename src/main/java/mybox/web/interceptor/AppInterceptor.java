package mybox.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mybox.model.User;
import mybox.util.WebUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class AppInterceptor extends HandlerInterceptorAdapter {

	private static final Logger log = LoggerFactory.getLogger(AppInterceptor.class);
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		User user = WebUtil.getUser(request);
		if (user == null) {
			String path = WebUtil.getPathAfterServicePath(request);
			if (!path.equals("/login") && !path.startsWith("/login?")) {
				log.debug("Request {} from {} isn't login!", request.getRequestURI(), WebUtil.getUserAddress(request));
				String contextPath = request.getContextPath();
				response.sendRedirect(contextPath + "/login");
				return false;
			}
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		String contextPath = request.getContextPath();
		request.setAttribute("contextPath", contextPath);
		
		String asset = contextPath + "/public";
		request.setAttribute("asset", asset);
		
		String path = WebUtil.getFirstPathAfterContextPath(request);
		if (path.equals("/login") || path.startsWith("/login?") || path.equals("/logout")) {
			return;
		}

		String service = contextPath + path;
		request.setAttribute("service", service);
	}
}
