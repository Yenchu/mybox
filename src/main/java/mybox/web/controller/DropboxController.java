package mybox.web.controller;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mybox.exception.Error;
import mybox.exception.ErrorException;
import mybox.model.ChunkedUploadParams;
import mybox.model.EntryParams;
import mybox.model.FileEntry;
import mybox.model.MetadataEntry;
import mybox.model.UploadParams;
import mybox.model.User;
import mybox.service.DropboxService;
import mybox.util.FileUtil;
import mybox.util.WebUtil;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/db")
public class DropboxController extends AbstractFileController {

	private static final Logger log = LoggerFactory.getLogger(DropboxController.class);
	
	private static final long UPLOAD_FILE_SIZE_LIMIT = 12 * 1024 * 1024; // 12M

	@Autowired
	private DropboxService dropboxService;
	
	@Override
	protected DropboxService getService() {
		return dropboxService;
	}
	
	@Override
	protected int getServicePathLength() {
		return 3;
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
	
	@RequestMapping(value = "/website")
	public String browse(@RequestParam(value = "space", required = false) String spaceId, HttpServletRequest request) {
		request.setAttribute("space", spaceId);
		return "website";
	}
	
	@RequestMapping(value = "/website/{space}/**")
	public void browse(
			@PathVariable String space, 
			HttpServletRequest request, HttpServletResponse response) {
		User user = WebUtil.getUser(request);
		String path = getRestOfPath(request, getServicePathLength() + 9 + space.length());
		if (path == null || "".equals(path)) {
			throw new ErrorException(Error.badRequest("Browse file request from " + user.toString() + " doesn't have file path!"));
		}
		log.info("User {} browse {}:{}", user.toString(), space, path);
		
		EntryParams params = new EntryParams(user, space, path);
		FileEntry entry = getService().download(params);
		
		MetadataEntry metadata = entry.getMetadata();
		String contentType = entry.getMimeType();
		InputStream in = entry.getContent();
		String fileName = metadata.getName();
		long fileSize = metadata.getBytes();
		log.debug("Get browsed source {} size {} success.", fileName, fileSize);
		
		try {
			response.setContentType(contentType);
			if (contentType.contains("html")) {
				String content = IOUtils.toString(in);
				StringBuilder sb = new StringBuilder();
				sb.append(request.getContextPath()).append("/dropbox/website/").append(space).append("/html/");
				content = content.replaceAll("(.*?)(src|href)(=\"/)([^/].*?)", "$1$2=\"" + sb.toString() + "$4");
				InputStream modifiedIn = IOUtils.toInputStream(content);
				long realSize = IOUtils.copy(modifiedIn, response.getOutputStream());
				log.debug("Output browsed source {} modified size {}", fileName, realSize);
			} else {
				if (fileSize >= DOWNLOAD_FILE_SIZE_THRESOLD) {
					long realSize = IOUtils.copyLarge(in, response.getOutputStream());
					log.debug("Output browsed source {} x=size {}", fileName, realSize);
				} else {
					int realSize = IOUtils.copy(in, response.getOutputStream());
					log.debug("Output browsed source {} size {}", fileName, realSize);
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
