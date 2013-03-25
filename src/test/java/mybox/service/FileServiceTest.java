package mybox.service;

import mybox.BaseUnitTest;
import mybox.service.FileService;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


public class FileServiceTest extends BaseUnitTest {

	private static final Logger log = LoggerFactory.getLogger(FileServiceTest.class);
	
	@Autowired
	private FileService fileService;
	
	@Test
	public void getFiles() {
		
	}
}