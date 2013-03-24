package mybox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainTest {

	private static final Logger log = LoggerFactory.getLogger(MainTest.class);
	
	public static void main(String[] args) {
		String content = "<li><a href=\"test.html\"><img src=\"/img/001_jpg_0.jpg\" height=\"36\" width=\"36\">Test 3</a></li>";
		content = content.replaceAll("(.*?)(src|href)(=\"/)([^/].*?)", "$1$2=\"dropbox/website/dropbox/html/$4");
		log.debug("{}", content);
		content = "<script src=\"//netdna.bootstrapcdn.com/twitter-bootstrap/2.2.2/js/bootstrap.min.js\"></script>";
		content = content.replaceAll("(.*?)(src|href)(=\"/)([^/].*?)", "$1$2=\"dropbox/website/dropbox/html/$4");
		log.debug("{}", content);
		content = "<li><a href=\"/test.html\">測試</a></li>";
		content = content.replaceAll("(.*?)(src|href)(=\"/)([^/].*?)", "$1$2=\"dropbox/website/dropbox/html/$4");
		log.debug("{}", content);
	}
}
