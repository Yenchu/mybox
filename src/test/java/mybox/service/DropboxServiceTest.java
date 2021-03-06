package mybox.service;

import java.util.List;

import mybox.SpringUnitTest;
import mybox.model.DeltaPage;
import mybox.model.Link;
import mybox.model.MetadataEntry;
import mybox.model.Token;
import mybox.model.User;
import mybox.to.CreateParams;
import mybox.to.DeltaParams;
import mybox.to.EntryParams;
import mybox.to.FileOperationResponse;
import mybox.to.LinkParams;
import mybox.to.MetadataParams;
import mybox.to.PathParams;
import mybox.to.RevisionParams;
import mybox.to.SearchParams;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class DropboxServiceTest extends SpringUnitTest {

	private static final Logger log = LoggerFactory.getLogger(DropboxServiceTest.class);
	
	@Autowired
	private DropboxService dropboxService;
	
	protected User getUser() {
		Token token = new Token();
		token.setAccessToken("thisisatesttoken");
		User user = new User();
		user.setToken(token);
		return user;
	}
	
	//@Test
	public void getFiles() {
		User user = new User();
		String path = "/";
		PathParams params = new PathParams(user, path);
		MetadataEntry entry = dropboxService.getFiles(params);
		log.debug("file: {}", entry);
	}
	
	//@Test
	public void getFilesIncludeDeleted() {
		User user = new User();
		String path = "/New folder";
		MetadataParams params = new MetadataParams(user, path);
		params.setList(true);
		params.setIncludeDeleted(true);
		//params.setHash("f43bc680e4bbe156128fef189380c5b9");//f43bc680e4bbe156128fef189380c5b9
		MetadataEntry entry = dropboxService.getFiles(params);
		log.debug("\n{}", entry);
	}
	
	//@Test
	public void createFolder() {
		String path = "/Doc/newFolder/folder_1";
		User user = getUser();
		CreateParams params = new CreateParams(user, path);
		FileOperationResponse resp = dropboxService.createFolder(params);
		log.debug("create folder: {}", resp);
	}
	
	//@Test
	public void delta() {
		String cursor = null;
		User user = getUser();
		DeltaParams params = new DeltaParams();
		params.setUser(user);
		params.setCursor(cursor);
		DeltaPage<MetadataEntry> entry = dropboxService.delta(params);
		log.debug("delta: {}", entry);
	}
	
	//@Test
	public void getRevisions() {
		String path = "/Lighthouse.jpg";
		User user = getUser();
		RevisionParams params = new RevisionParams(user, path);
		List<MetadataEntry> entries = dropboxService.getRevisions(params);
		for (MetadataEntry entry: entries) {
			log.debug("revision: {}", entry);
		}
	}
	
	//@Test
	public void restore() {
		String path = "/Lighthouse.jpg";
		String rev = "2ce09ff683f";
		User user = getUser();
		EntryParams params = new EntryParams(user, path);
		params.setRev(rev);
		MetadataEntry entry = dropboxService.restore(params);
		log.debug("restore file: {}", entry);
	}
	
	//@Test
	public void link() {
		String path = "/Test";
		User user = getUser();
		LinkParams params = new LinkParams(user, path);
		params.setShortUrl(false);
		Link link = dropboxService.link(params);
		log.debug("link: {}", link);
	}
	
	//@Test
	public void media() {
		String path = "/Lighthouse.jpg";
		User user = getUser();
		PathParams params = new PathParams(user, path);
		Link link = dropboxService.media(params);
		log.debug("media: {}", link);
	}
	
	//@Test
	public void search() {
		String path = "/Test/tmp";
		String query = ".jpg";
		User user = getUser();
		SearchParams params = new SearchParams(user, path);
		params.setQuery(query);
		List<MetadataEntry> entries = dropboxService.search(params);
		for (MetadataEntry entry: entries) {
			log.debug("path: {}", entry.getPath());
		}
	}
}
