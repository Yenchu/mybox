package mybox.web.controller;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mybox.exception.Error;
import mybox.exception.ErrorException;
import mybox.model.FileEntry;
import mybox.model.MetadataEntry;
import mybox.model.User;
import mybox.service.DropboxService;
import mybox.to.EntryParams;
import mybox.util.WebUtil;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ExtensionController extends AbstractFileController {

	private static final Logger log = LoggerFactory.getLogger(ExtensionController.class);
	
	@Autowired
	private DropboxService dropboxService;
	
	protected DropboxService getService() {
		return dropboxService;
	}
	
	@RequestMapping(value = "/page/**")
	public void browse(
			HttpServletRequest request, HttpServletResponse response) {
		User user = WebUtil.getUser(request);
		String path = getRestOfPath(request, 5);
		if (path == null || "".equals(path)) {
			throw new ErrorException(Error.badRequest("Browse page request from " + user.getName() + " doesn't have file path!"));
		}
		
		log.info("User {} browse page {}", user.getName(), path);
		EntryParams params = new EntryParams(user, path);
		FileEntry entry = getService().download(params);
		
		MetadataEntry metadata = entry.getMetadata();
		String contentType = entry.getMimeType();
		InputStream in = entry.getContent();
		String fileName = metadata.getName();
		long fileSize = metadata.getBytes();
		log.debug("Get browsed page {} size {} success.", fileName, fileSize);
		
		try {
			response.setContentType(contentType);
			if (contentType.contains("html")) {
				String content = IOUtils.toString(in);
				StringBuilder sb = new StringBuilder();
				sb.append(request.getContextPath()).append("/page/").append("/public/");
				content = content.replaceAll("(.*?)(src|href)(=\"/)([^/].*?)", "$1$2=\"" + sb.toString() + "$4");
				InputStream modifiedIn = IOUtils.toInputStream(content);
				long realSize = IOUtils.copy(modifiedIn, response.getOutputStream());
				//log.debug("Output browsed page {} size {}", fileName, realSize);
			} else {
				if (fileSize >= DOWNLOAD_FILE_SIZE_THRESOLD) {
					long realSize = IOUtils.copyLarge(in, response.getOutputStream());
					//log.debug("Output browsed page {} size {}", fileName, realSize);
				} else {
					int realSize = IOUtils.copy(in, response.getOutputStream());
					//log.debug("Output browsed page {} size {}", fileName, realSize);
				}
			}
			response.flushBuffer();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}
}
