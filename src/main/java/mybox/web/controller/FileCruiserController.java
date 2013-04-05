package mybox.web.controller;

import javax.servlet.http.HttpServletRequest;

import mybox.exception.Error;
import mybox.exception.ErrorException;
import mybox.model.LoginParams;
import mybox.model.User;
import mybox.service.FileCruiserService;
import mybox.service.FileService;
import mybox.to.AuthResponse;
import mybox.util.WebUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/fc")
public class FileCruiserController extends AbstractFileController {

	private static final Logger log = LoggerFactory.getLogger(FileCruiserController.class);
	
	@Autowired
	private FileCruiserService fileCruiserService;
	
	@RequestMapping(value="/login", method = RequestMethod.POST)
	@ResponseBody
	public AuthResponse auth(
			@RequestParam(value = "username", required = false) String username,
			@RequestParam(value = "password", required = false) String password,
			HttpServletRequest request) {
		String ip = WebUtil.getUserAddress(request);
		String domainname = request.getParameter("domain");
		log.info("User {}/{} from {} login!", domainname, username, ip);
		
		LoginParams param = new LoginParams(domainname, username, password);
		param.setIp(ip);
		User user = getService().auth(param);
		if (user == null) {
			throw new ErrorException(Error.unauthorized());
		}
		WebUtil.setUser(request, user);
		
		String path = WebUtil.getFirstPathAfterContextPath(request);
		String serviceUrl = request.getContextPath() + path;
		AuthResponse authResp = new AuthResponse();
		authResp.setServiceUrl(serviceUrl);
		return authResp;
	}

	@Override
	protected FileService getService() {
		return fileCruiserService;
	}
	
	@Override
	protected int getServicePathLength() {
		return 3;
	}
}
