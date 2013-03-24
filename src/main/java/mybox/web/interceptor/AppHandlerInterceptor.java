package mybox.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import mybox.common.to.User;
import mybox.util.WebUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


public class AppHandlerInterceptor extends HandlerInterceptorAdapter {

	private static final Logger log = LoggerFactory.getLogger(AppHandlerInterceptor.class);
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
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
		request.setAttribute("appContext", contextPath);
		
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
