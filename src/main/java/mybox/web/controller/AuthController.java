package mybox.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import mybox.model.User;
import mybox.util.WebUtil;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController extends BaseController {

	private static final Logger log = LoggerFactory.getLogger(AuthController.class);
	
	@RequestMapping(value="/login")
	public String login(@RequestParam(value = "service", required = false) String serviceType, 
			HttpServletRequest request) {
		if (StringUtils.isBlank(serviceType)) {
			//* default service for test
			serviceType = ServiceType.DROPBOX.value();
		}
		
		String contextPath = request.getContextPath();
		request.setAttribute("service", contextPath + "/" + serviceType);
		return "login";
	}
	
	@RequestMapping(value="/logout")
	public String logout(HttpServletRequest request, HttpSession session) {
		User user = getUser(request);
		log.debug("User {} from {} logout!", user, WebUtil.getUserAddress(request));
		session.removeAttribute("user");
		return "login";
	}
	
	public enum ServiceType {

		DISK("dk"), DROPBOX("db"), MONDO("md");
		
		private final String value;
		
		private ServiceType(String value) {
			this.value = value;
		}
		
		public String value() {
			return value;
		}
	}
}
