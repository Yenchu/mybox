package mybox.web.controller;

import javax.servlet.http.HttpServletRequest;

import mybox.common.to.User;
import mybox.dropbox.model.Account;
import mybox.util.WebUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class UserController extends BaseController {

	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	@RequestMapping(value="/users")
	public String getUsers() {
		return "users";
	}
	
	@RequestMapping(value="/account/profile")
	public String getProfile(HttpServletRequest request) {
		User user = getUser(request);
		Account account = new Account();
		account.setDisplayName(user.getName());
		account.setQuota(1000);
		account.setRole(new int[]{1});
		request.setAttribute("account", account);
		return "account";
	}
	
	@RequestMapping(value="/account/profile", method = RequestMethod.POST)
	public String setProfile(@ModelAttribute("account") Account account, HttpServletRequest request) {
		WebUtil.logParameters(request);
		log.debug("account name={}, quota={}, role={}", new Object[]{account.getDisplayName(), account.getQuota(), account.getRole()});
		return "account";
	}
	
	@RequestMapping(value="/account/password")
	public String changePassword() {
		return "users";
	}
}
