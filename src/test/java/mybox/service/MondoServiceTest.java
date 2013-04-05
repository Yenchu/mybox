package mybox.service;

import java.util.List;

import mybox.SpringUnitTest;
import mybox.model.CreateParams;
import mybox.model.LoginParams;
import mybox.model.MetadataEntry;
import mybox.model.MetadataParams;
import mybox.model.Params;
import mybox.model.PathParams;
import mybox.model.Space;
import mybox.model.mondo.Group;
import mybox.model.mondo.MondoUser;
import mybox.service.MondoService;
import mybox.to.FileOperationResponse;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


public class MondoServiceTest extends SpringUnitTest {

	private static final Logger log = LoggerFactory.getLogger(MondoServiceTest.class);
	
	@Autowired
	private MondoService mondoService;

	protected MondoUser getUser() {
		String username = "John";
		String password = "abxcdef";
		LoginParams param = new LoginParams(username, password);
		MondoUser user = (MondoUser) mondoService.auth(param);
		return user;
	}
	
	protected Space getSpace(MondoUser user) {
		Params params = new Params();
		params.setUser(user);
		Space space = mondoService.getDefaultSpace(params);
		return space;
	}
	
	@Test
	public void login() {
		MondoUser user = getUser();
		log.debug("account: {}", user.getAccount());
	}
	
	@Test
	public void getGroups() {
		MondoUser user = getUser();
		List<Group> groups = mondoService.getGroups(user);
		log.debug("groups: {}", groups);
	}
	
	@Test
	public void getFiles() {
		MondoUser user = getUser();
		Space space = getSpace(user);
		String root = space.getId();
		String path = "/";
		PathParams params = new PathParams(user, root, path);
		
		MetadataEntry entry = mondoService.getFiles(params);
		log.debug("metadata: {}", entry);
	}
	
	@Test
	public void getFileList() {
		MondoUser user = getUser();
		Space space = getSpace(user);
		String root = space.getId();
		String path = "/xyz";
		MetadataParams params = new MetadataParams(user, root, path);
		params.setList(true);
		MetadataEntry entry = mondoService.getFiles(params);
		log.debug("\n{}", entry);
	}
	
	@Test
	public void createFolder() {
		MondoUser user = getUser();
		Space space = getSpace(user);
		String root = space.getId();
		String path = "/test";
		CreateParams params = new CreateParams(user, root, path);
		
		FileOperationResponse resp = mondoService.createFolder(params);
		log.debug("create folder: {}", resp);
	}
}
