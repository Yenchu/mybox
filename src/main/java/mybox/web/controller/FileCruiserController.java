package mybox.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mybox.model.FileEntry;
import mybox.model.Link;
import mybox.model.MetadataEntry;
import mybox.model.User;
import mybox.model.filecruiser.DeltaPage;
import mybox.model.filecruiser.SharedFile;
import mybox.model.filecruiser.SharingFile;
import mybox.service.FileCruiserService;
import mybox.to.AuthResponse;
import mybox.to.DeltaParams;
import mybox.to.EntryParams;
import mybox.to.LinkParams;
import mybox.to.LoginParams;
import mybox.to.Params;
import mybox.to.PathParams;
import mybox.util.WebUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

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
		User user = getService().auth(param);
		WebUtil.setUser(request, user);
		
		//* FileCruiser is default service, no need service type
		String serviceUrl = request.getContextPath();
		log.debug("Service url: {}", serviceUrl);
		AuthResponse authResp = new AuthResponse();
		authResp.setServiceUrl(serviceUrl);
		return authResp;
	}
	
	@RequestMapping(value = "/delta", method = RequestMethod.POST)
	@ResponseBody
	public DeltaPage delta(
			@RequestParam(value = "cursor", required = false) String cursor, 
			@RequestParam(value = "locale", required = false) String locale, 
			HttpServletRequest request) {
		User user = WebUtil.getUser(request);
		log.debug("User {} get delta.", user.getName());
		
		DeltaParams params = new DeltaParams();
		params.setUser(user);
		params.setCursor(cursor);
		params.setLocale(locale);
		DeltaPage entry = getService().delta(params);
		return entry;
	}
	
	@RequestMapping(value="/linkPage")
	public String getLinkPage(HttpServletRequest request) {
		User user = WebUtil.getUser(request);
		log.debug("User {} get link page.", user.getName());
		return "files/link";
	}
	
	@RequestMapping(value="/links", method = RequestMethod.POST)
	@ResponseBody
	public Link link(
			@RequestParam(value = "space", required = false) String space, 
			@RequestParam(value = "file", required = false) String file, 
			HttpServletRequest request) {
		User user = WebUtil.getUser(request);
		String path = decodeUrl(file);
		log.info("User {} create link of {}", user.toString(), path);
		
		String expires = request.getParameter("expire");
		String password = request.getParameter("passwd");
		String emails = request.getParameter("emails");
		String message = request.getParameter("message");
		log.info("Link settings: expires={}, password={}, emails={}, message={}", expires, password, emails, message);
		
		LinkParams params = new LinkParams();
		params.setUser(user);
		params.setPath(path);
		params.setExpires(expires);
		params.setPassword(password);
		params.setEmails(emails);
		params.setMessage(message);
		Link link = getService().link(params);
		return link;
	}
	
	@RequestMapping(value="/links/**")
	public String link(HttpServletRequest request, HttpServletResponse response) {
		WebUtil.logParameters(request);
		User user = WebUtil.getUser(request);
		String path = getRestOfPath(request, getServicePathLength() + 6);
		log.info("User {} access link of {}", (user != null ? user.toString() : "guest"), path);
		
		EntryParams params = new EntryParams();
		params.setUser(user);
		params.setPath(path);
		FileEntry entry = getService().getLink(params);
		MetadataEntry metadata = entry.getMetadata();
		if (metadata.getIsDir()) {
			request.setAttribute("metadata", metadata);
			return "folderLink";
		} else {
			this.download(entry, request, response);
			return null;
		}
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
	
	@RequestMapping(value="/shares", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public void share(@RequestBody SharedFile[] shareFiles, HttpServletRequest request) {
		WebUtil.logParameters(request);
		for (SharedFile shareFile: shareFiles) {
			log.debug("sharedFile: {}", shareFile);
		}
	}
	
	//@RequestMapping(value="/shares", method = RequestMethod.POST)
	//@ResponseBody
	public SharedFile share(
			@RequestParam(value = "space", required = false) String space,
			@RequestParam(value = "file", required = false) String file, 
			@RequestParam(value = "isDir", required = false) Boolean isDir, 
			@RequestParam(value = "user", required = false) String userId,  
			@RequestParam(value = "notation", required = false) Integer notation, 
			HttpServletRequest request) {
		User user = WebUtil.getUser(request);
		String path = decodeUrl(file);
		log.info("User {} share of {}:{} (isDir={}) to user {} with permission {}", user.toString(), space, path, isDir, userId, notation);
		
		SharingFile sharingFile = new SharingFile();
		sharingFile.setFilePath(file);
		sharingFile.setIsDir(isDir != null ? isDir : false);
		sharingFile.setUserId(userId);
		
		Params params = new Params();
		params.setUser(user);
		SharedFile sharedFile = getService().share(params, sharingFile);
		return sharedFile;
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
