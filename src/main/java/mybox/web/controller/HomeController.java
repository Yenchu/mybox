package mybox.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

	@RequestMapping(value={"", "/"})
	public String index() {
		return "home";
	}
	
	@RequestMapping(value = "/home")
	public String home(HttpServletRequest request) {
		return "home";
	}
}
