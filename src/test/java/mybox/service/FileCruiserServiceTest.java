package mybox.service;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import mybox.SpringUnitTest;
import mybox.model.FileEntry;
import mybox.model.Link;
import mybox.model.MetadataEntry;
import mybox.model.Space;
import mybox.model.filecruiser.DeltaPage;
import mybox.model.filecruiser.FileCruiserUser;
import mybox.to.DeltaParams;
import mybox.to.EntryParams;
import mybox.to.LinkParams;
import mybox.to.LoginParams;
import mybox.to.Params;
import mybox.to.RevisionParams;
import mybox.to.SearchParams;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class FileCruiserServiceTest extends SpringUnitTest {

	private static final Logger log = LoggerFactory.getLogger(FileCruiserServiceTest.class);
	
	@Autowired
	private FileCruiserService fileCruiserService;
	
	protected FileCruiserUser getUser() {
		String domainName = "cs.promise.com.tw";
		String username = "Andrew.White";
		String password = "Password1";
		LoginParams params = new LoginParams(domainName, username, password);
		FileCruiserUser user = (FileCruiserUser) fileCruiserService.auth(params);
		return user;
	}
	
	@Test
	public void auth() {
		FileCruiserUser user = getUser();
		assertNotNull("User login failed!", user.getId());
		log.debug("user: {}", user);
		//user: id=f97b91d243304b9981ebeac1aa756071, name=Andrew.White, ip=null, domainId=03574102c05d4a35abfd1fe692cc7253, domainName=cs.promise.com.tw, token=400e863cd5da4e9e9c6056668af7468f, expiresAt=Fri Apr 26 17:21:02 CST 2013
	}
	
	@Test
	public void getSpace() {
		String id = "002d76d4cd32466baa52513fbd52f55f";
		String token = "16afbbda595a4a6992527b95a231b10a";
		String spaceId = null;
		
		FileCruiserUser user = new FileCruiserUser();
		user.setId(id);
		user.setToken(token);
		Params params = new Params();
		params.setUser(user);
		
		Space space = fileCruiserService.getSpace(params, spaceId);
		assertNotNull("There must be a default space!", space.getId());
		log.debug("space: {}", space);
	}
	
	@Test
	public void search() {
		FileCruiserUser user = getUser();
		String path = "/tmp";
		String query = "a";
		SearchParams params = new SearchParams();
		params.setUser(user);
		params.setPath(path);
		params.setQuery(query);
		
		List<MetadataEntry> entries = fileCruiserService.search(params);
		for (MetadataEntry entry: entries) {
			log.debug("path: {}", entry.getPath());
		}
	}
	
	@Test
	public void delta() {
		FileCruiserUser user = getUser();
		String cursor = null;
		DeltaParams params = new DeltaParams();
		params.setUser(user);
		params.setCursor(cursor);
		DeltaPage entry = fileCruiserService.delta(params);
		log.debug("delta: {}", entry);
	}
	
	@Test
	public void getRevisions() {
		FileCruiserUser user = getUser();
		String path = "/tomcat.png";
		RevisionParams params = new RevisionParams();
		params.setUser(user);
		params.setPath(path);
		
		List<MetadataEntry> entries = fileCruiserService.getRevisions(params);
		for (MetadataEntry entry: entries) {
			log.debug("revision: {}", entry);
		}
	}
	
	@Test
	public void link() {
		FileCruiserUser user = getUser();
		String path = "/tomcat.png";
		LinkParams params = new LinkParams();
		params.setUser(user);
		params.setPath(path);
		
		Link link = fileCruiserService.link(params);
		log.debug("link: {}", link);
	}
	
	@Test
	public void getLink() {
		FileCruiserUser user = getUser();
		String path = "/639b515c-930c-4617-b7a8-f825f5b5cab7";
		EntryParams params = new EntryParams();
		params.setUser(user);
		params.setPath(path);
		FileEntry entry = fileCruiserService.getLink(params);
	}
	
	@Test
	public void share() {
		String userId = "f97b91d243304b9981ebeac1aa756071";
		
	}
}
