package mybox.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import mybox.model.User;
import mybox.type.ServiceType;
import mybox.util.PathUtil;
import mybox.util.WebUtil;

import org.apache.commons.lang.StringUtils;
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
			serviceType = ServiceType.DROPBOX.value();
		}
		String serviceUrl = PathUtil.combinePath(request.getContextPath(), serviceType);
		request.setAttribute("service", serviceUrl);
		return "login";
	}
	
	@RequestMapping(value="/logout")
	public String logout(HttpServletRequest request, HttpSession session) {
		User user = WebUtil.getUser(request);
		log.debug("User {} from {} logout!", user, WebUtil.getUserAddress(request));
		session.removeAttribute("user");
		return "login";
	}
}
