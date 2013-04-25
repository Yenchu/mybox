package mybox.web.controller;

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import mybox.model.MetadataEntry;
import mybox.model.User;
import mybox.model.dropbox.DeltaPage;
import mybox.service.DropboxService;
import mybox.to.ChunkedUploadParams;
import mybox.to.DeltaParams;
import mybox.to.UploadParams;
import mybox.util.FileUtil;
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
@RequestMapping(value = "/db")
public class DropboxController extends AbstractFileController {

	private static final Logger log = LoggerFactory.getLogger(DropboxController.class);
	
	private static final long UPLOAD_FILE_SIZE_LIMIT = 12 * 1024 * 1024; // 12M

	@Autowired
	private DropboxService dropboxService;
	
	@RequestMapping(value = "/delta", method = RequestMethod.POST)
	@ResponseBody
	public DeltaPage<MetadataEntry> delta(
			@RequestParam(value = "cursor", required = false) String cursor, 
			@RequestParam(value = "locale", required = false) String locale, 
			HttpServletRequest request) {
		User user = WebUtil.getUser(request);
		log.debug("User {} get delta.", user.toString());
		
		DeltaParams params = new DeltaParams();
		params.setUser(user);
		params.setCursor(cursor);
		params.setLocale(locale);
		DeltaPage<MetadataEntry> entry = getService().delta(params);
		log.debug("delta: {}", entry);
		return entry;
	}
	
	@Override
	protected long saveFile(HttpServletRequest request, String space, String folderPath, String fileName, InputStream content, long contentLength) {
		User user = WebUtil.getUser(request);
		String filePath = FileUtil.getPath(folderPath, fileName);
		log.info("User {} save {}{} size {}", user.toString(), space, filePath, contentLength);

		MetadataEntry entry = null;
		if (contentLength >= UPLOAD_FILE_SIZE_LIMIT) {
			ChunkedUploadParams params = new ChunkedUploadParams(user, space, filePath);
			params.setContent(content);
			params.setLength(contentLength);
			entry = getService().chunkedUpload(params);
		} else {
			UploadParams params = new UploadParams(user, space, filePath);
			params.setContent(content);
			params.setLength(contentLength);
			entry = getService().upload(params);
		}
		return entry.getBytes();
	}
	
	@Override
	protected DropboxService getService() {
		return dropboxService;
	}
	
	@Override
	protected int getServicePathLength() {
		return 3;
	}
}
