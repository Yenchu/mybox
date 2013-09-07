package mybox.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import mybox.model.Token;
import mybox.model.User;
import mybox.service.AuthService;
import mybox.util.WebUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController extends BaseController {

	private static final Logger log = LoggerFactory.getLogger(AuthController.class);

	@Autowired
	private AuthService authService;
	
	@RequestMapping(value = "/oauth2/code")
	public String receiveOauth2Code(
			@RequestParam(value = "code", required = false) String code, 
			@RequestParam(value = "state", required = false) String state, 
			HttpServletRequest request) {
		log.info("Code={} state={}", code, state);
		Token token = authService.getToken(code);
		User user = authService.getUser(token);
		WebUtil.setUser(request, user);
		return "redirect:/metadata";
	}
	
	@RequestMapping(value="/logout")
	public String logout(HttpServletRequest request, HttpSession session) {
		User user = WebUtil.getUser(request);
		log.debug("User {} from {} logout!", user, WebUtil.getUserAddress(request));
		session.removeAttribute("user");
		return "home";
	}
}
