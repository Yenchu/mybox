package mybox.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import mybox.model.filecruiser.FileCruiserUser;
import mybox.model.keystone.User;
import mybox.service.UserService;
import mybox.to.Page;
import mybox.util.WebUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserController extends BaseController {

	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserService userService;
	
	@RequestMapping(value="/users")
	@ResponseBody
	public Page<User> getUsers(HttpServletRequest request) {
		FileCruiserUser user = (FileCruiserUser) WebUtil.getUser(request);
		log.debug("User {} get users.", user.toString());
		List<User> users = userService.getUsers(user);
		return new Page<User>(users);
	}
}
