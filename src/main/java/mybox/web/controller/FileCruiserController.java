package mybox.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import mybox.exception.Error;
import mybox.exception.ErrorException;
import mybox.model.User;
import mybox.model.filecruiser.SharedFile;
import mybox.model.filecruiser.SharingFile;
import mybox.service.FileCruiserService;
import mybox.to.AuthResponse;
import mybox.to.LoginParams;
import mybox.to.Params;
import mybox.to.PathParams;
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
	
	@RequestMapping(value="/shares", method = RequestMethod.POST)
	@ResponseBody
	public SharedFile share(
			@RequestParam(value = "space", required = false) String space, 
			@RequestParam(value = "file", required = false) String file, 
			@RequestParam(value = "isDir", required = false) Boolean isDir,  
			@RequestParam(value = "user", required = false) String userId,   
			@RequestParam(value = "permission", required = false) Integer permissionCode, 
			HttpServletRequest request) {
		User user = WebUtil.getUser(request);
		String path = decodeUrl(file);
		log.info("User {} share of {}:{} (isDir={}) to user {} with permission {}", user.toString(), space, path, isDir, userId, permissionCode);
		
		SharingFile sharingFile = new SharingFile();
		sharingFile.setFilePath(file);
		sharingFile.setDir(isDir != null ? isDir : false);
		sharingFile.setUserId(userId);
		SharingFile.Permission permission = SharingFile.Permission.getPermission(permissionCode);
		sharingFile.setPermission(permission);
		
		Params params = new Params();
		params.setUser(user);
		SharedFile sharedFile = getService().share(params, sharingFile);
		return sharedFile;
	}
	
	@RequestMapping(value="/shares")
	@ResponseBody
	public List<SharedFile> getShares(
			@RequestParam(value = "space", required = false) String space, 
			@RequestParam(value = "file", required = false) String file,
			HttpServletRequest request) {
		User user = WebUtil.getUser(request);
		String path = decodeUrl(file);
		log.info("User {} get shares of {}:{}", user.toString(), space, path);
		
		PathParams params = new PathParams(user, space, path);
		List<SharedFile> sharedFiles = getService().getShares(params);
		return sharedFiles;
	}

	@Override
	protected FileCruiserService getService() {
		return fileCruiserService;
	}
	
	@Override
	protected int getServicePathLength() {
		return 3;
	}
}
