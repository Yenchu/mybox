package mybox.web.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mybox.exception.Error;
import mybox.exception.ErrorException;
import mybox.json.JsonConverter;
import mybox.model.DeltaPage;
import mybox.model.FileEntry;
import mybox.model.Link;
import mybox.model.MetadataEntry;
import mybox.model.User;
import mybox.service.DropboxService;
import mybox.to.ChunkedUploadParams;
import mybox.to.CopyParams;
import mybox.to.CreateParams;
import mybox.to.DeleteParams;
import mybox.to.DeltaParams;
import mybox.to.EntryParams;
import mybox.to.FileOperationResponse;
import mybox.to.LinkParams;
import mybox.to.MetadataParams;
import mybox.to.MoveParams;
import mybox.to.Page;
import mybox.to.RevisionParams;
import mybox.to.SearchParams;
import mybox.to.ThumbnailParams;
import mybox.to.UploadParams;
import mybox.to.UploadResponse;
import mybox.util.PathUtil;
import mybox.util.EncodeUtil;
import mybox.util.UserAgentParser;
import mybox.util.WebUtil;
import mybox.web.vo.TreeNode;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping(value = "/db")
public class DropboxController extends AbstractFileController {
	
	private static final Logger log = LoggerFactory.getLogger(DropboxController.class);

	@Autowired
	private DropboxService dropboxService;

	protected DropboxService getService() {
		return dropboxService;
	}
	
	protected int getServicePathLength() {
		return 3;
	}

	@RequestMapping(value={"", "/"})
	public String index(HttpServletRequest request) {
		return getFiles(request);
	}
	
	@RequestMapping(value = "/metadata/**")
	public String getFiles(HttpServletRequest request) {
		User user = WebUtil.getUser(request);
		String path = getRestOfPath(request, getServicePathLength() + 9);
		log.debug("User {} list {}", user.getName(), (path != null ? path : "/"));
		
		if (StringUtils.isEmpty(path)) {
			path = "/";
		} else {
			MetadataParams params = new MetadataParams(user, path);
			MetadataEntry entry = getService().getFiles(params);
			if (!entry.getIsDir()) {
				path = entry.getLocation();
			}
		}
		request.setAttribute("currentFolder", path);
		
		log.debug("{} user agent: {}", WebUtil.getUserAddress(request), request.getHeader("user-agent"));
		return "files";
	}

	@RequestMapping(value = "/metadata", method = RequestMethod.POST)
	@ResponseBody
	public Page<MetadataEntry> pageFiles(
			@RequestParam(value = "folder", required = false) String folder, 
			HttpServletRequest request) {
		User user = WebUtil.getUser(request);
		String path = decodeUrl(folder);
		log.debug("User {} page {}", user.getName(), path);

		MetadataParams params = new MetadataParams(user, path);
		params.setList(true);
		MetadataEntry entry = getService().getFiles(params);

		List<MetadataEntry> entries = null;
		if (entry != null) {
			entries = entry.getContents();
			if (entries != null && entries.size() > 0) {
				sortByFolder(entries);
			}
		}
		
		Page<MetadataEntry> page = new Page<MetadataEntry>(entries);
		return page;
	}
	
	@RequestMapping(value="/roottree")
	@ResponseBody
	public List<TreeNode> getRoot(
			@RequestParam(value = "folder", required = false) String folder, 
			HttpServletRequest request) {
		User user = WebUtil.getUser(request);
		String path = decodeUrl(folder);
		log.debug("User {} dir {}", user.getName(), path);
		
		MetadataParams params = new MetadataParams(user, path);
		params.setList(true);
		MetadataEntry folderEntry = getService().getFolders(params);
		
		TreeNode rootNode = new TreeNode(folderEntry.getId(), folderEntry.getName(), folderEntry.getIsDir());
		List<MetadataEntry> entries = folderEntry.getContents();
		for (MetadataEntry entry: entries) {
			TreeNode node = new TreeNode(entry.getId(), entry.getName(), entry.getIsDir());
			node.setIsLazy(true);
			rootNode.addChild(node);
		}

		List<TreeNode> treeNodes = new ArrayList<TreeNode>();
		treeNodes.add(rootNode);
		return treeNodes;
	}
	
	@RequestMapping(value="/dirtree")
	@ResponseBody
	public List<TreeNode> getFolders(
			@RequestParam(value = "folder", required = false) String folder, 
			HttpServletRequest request) {
		User user = WebUtil.getUser(request);
		String path = decodeUrl(folder);
		log.debug("User {} dir {}", user.getName(), path);
		
		MetadataParams params = new MetadataParams(user, path);
		params.setList(true);
		MetadataEntry folderEntry = getService().getFolders(params);

		List<TreeNode> treeNodes = new ArrayList<TreeNode>();
		List<MetadataEntry> entries = folderEntry.getContents();
		for (MetadataEntry entry: entries) {
			TreeNode node = new TreeNode(entry.getId(), entry.getName(), entry.getIsDir());
			node.setIsLazy(true);
			treeNodes.add(node);
		}
		return treeNodes;
	}

	@RequestMapping(value = "/files/**")
	public void download(
			HttpServletRequest request, HttpServletResponse response) {
		User user = WebUtil.getUser(request);
		String path = getRestOfPath(request, getServicePathLength() + 6);
		if (path == null || "".equals(path)) {
			throw new ErrorException(Error.badRequest("Download file request from " + user.getName() + " doesn't have file path!"));
		}
		log.info("User {} download {}", user.getName(), path);
		
		EntryParams params = new EntryParams(user, path);
		FileEntry entry = getService().download(params);
		download(entry, request, response);
	}
	
	protected void download(FileEntry entry, HttpServletRequest request, HttpServletResponse response) {
		MetadataEntry metadata = entry.getMetadata();
		String contentType = entry.getMimeType();
		InputStream in = entry.getContent();
		String fileName = metadata.getName();
		long fileSize = metadata.getBytes();
		log.debug("Download {} size {} success.", fileName, fileSize);
		
		try {
			response.setContentType(contentType);
			String userAgent = request.getHeader("user-agent");
			if (UserAgentParser.isIE(userAgent)) {
				// IE required URL-encoded file name 
				response.setHeader("Content-Disposition", "attachment; filename=\"" + EncodeUtil.encode(fileName).replace("+", "%20") + "\"");
			} else if (UserAgentParser.isSafari(userAgent)) {
				// Safari doesn't support non US-ASCII characters in file name
				//response.setCharacterEncoding("UTF-8");
				response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			} else {
				// to support non US-ASCII characters in file name
				response.setHeader("Content-Disposition", "attachment; filename=\"" + MimeUtility.encodeText(fileName) + "\"");
			}
			
			if (fileSize >= DOWNLOAD_FILE_SIZE_THRESOLD) {
				long realSize = IOUtils.copyLarge(in, response.getOutputStream());
				log.debug("Download {} real size {}", fileName, realSize);
			} else {
				int realSize = IOUtils.copy(in, response.getOutputStream());
				log.debug("Download {} real size {}", fileName, realSize);
			}
			response.flushBuffer();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}
	
	@RequestMapping(value = "/thumbnails/**")
	public void getThumbnail(
			HttpServletRequest request, HttpServletResponse response) {
		User user = WebUtil.getUser(request);
		String path = getRestOfPath(request, getServicePathLength() + 11);
		if (path == null || "".equals(path)) {
			throw new ErrorException(Error.badRequest("Get thumbnail request from " + user.getName() + " doesn't have file path!"));
		}
		log.debug("User {} get thumbnail {}", user.getName(), path);
		
		ThumbnailParams params = new ThumbnailParams(user, path);
		InputStream content = getService().getThumbnail(params);
		
		try {
			IOUtils.copy(content, response.getOutputStream());
			response.flushBuffer();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(content);
		}
	}
	
	@RequestMapping(value = "/files", method = RequestMethod.POST)
	public void upload( 
			@RequestParam(value = "detectFileSize", required = false) Boolean detectFileSize, 
			HttpServletRequest request, HttpServletResponse response) {
		this.upload(false, detectFileSize, request, response);
	}
	
	@RequestMapping(value = "/files_put", method = {RequestMethod.PUT, RequestMethod.POST})
	public void uploadByPut( 
			@RequestParam(value = "detectFileSize", required = false) Boolean detectFileSize, 
			HttpServletRequest request, HttpServletResponse response) {
		this.upload(true, detectFileSize, request, response);
	}
	
	protected void upload(Boolean isPut, Boolean detectFileSize, HttpServletRequest request, HttpServletResponse response) {
		User user = WebUtil.getUser(request);
		log.info("User {} upload file: detectFileSize={}", user.getName(), detectFileSize);
		
		List<UploadResponse> uploadResps = null;
		if (detectFileSize != null && detectFileSize == true) {
			uploadResps = upload(isPut, request);
		} else {
			log.debug("Save uploaded file to tmp folder for request {} from {}.", request.getRequestURI(), WebUtil.getUserAddress(request));
			uploadResps = uploadCached(isPut, request);
		}
		
		for (UploadResponse uploadResp: uploadResps) {
			String result = (uploadResp.getError() != null ? uploadResp.getError() : "success");
			log.debug("Uploading {} size {} result: {}.", uploadResp.getName(), uploadResp.getSize(), result);
		}
		
		try {
			// IE 9 doesn't support application/json, so using text/plain as workaround.
			String body = JsonConverter.toJson(uploadResps);
			response.setContentType("text/plain;charset=UTF-8");
			response.getWriter().print(body);
			response.getWriter().flush();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			log.error("Got exception when handling request {} from  {}", request.getRequestURI(), WebUtil.getUserAddress(request), e.getMessage());
		}
	}
	
	@RequestMapping(value = "/fileops/create_folder", method = RequestMethod.POST)
	@ResponseBody
	public FileOperationResponse createFolder(
			@RequestParam(value = "folder", required = false) String folder, 
			@RequestParam(value = "name", required = false) String name, 
			HttpServletRequest request) {
		User user = WebUtil.getUser(request);
		String parentPath = decodeUrl(folder);
		String folderPath = PathUtil.combinePath(parentPath, name);
		log.info("User {} create folder {}", user.getName(), folderPath);
		
		CreateParams params = new CreateParams(user, folderPath);
		FileOperationResponse resp = getService().createFolder(params);
		return resp;
	}
	
	@RequestMapping(value = "/fileops/rename", method = RequestMethod.POST)
	@ResponseBody
	public FileOperationResponse rename(
			@RequestParam(value = "id", required = false) String id, 
			@RequestParam(value = "folder", required = false) String folder, 
			@RequestParam(value = "name", required = false) String name, 
			HttpServletRequest request) {
		User user = WebUtil.getUser(request);
		String srcPath = decodeUrl(id);
		String destParentPath = decodeUrl(folder);
		String destPath = PathUtil.combinePath(destParentPath, name);
		log.info("User {} rename {} to {}", user.getName(), srcPath, destPath);
		
		MoveParams params = new MoveParams(user, new String[]{srcPath}, new String[]{destPath});
		List<FileOperationResponse> resps = getService().move(params);
		FileOperationResponse resp = null;
		if (resps != null && resps.size() == 1) {
			resp = resps.get(0);
		}
		return resp;
	}
	
	@RequestMapping(value = "/fileops/delete", method = RequestMethod.POST)
	@ResponseBody
	public List<FileOperationResponse> delete( 
			@RequestParam(value = "files[]", required = false) String[] files, 
			HttpServletRequest request) {
		User user = WebUtil.getUser(request);
		String[] filePaths = decodeUrl(files);
		log.info("User {} delete {}", user.getName(), filePaths);
		
		DeleteParams params = new DeleteParams(user, filePaths);
		List<FileOperationResponse> resps = getService().delete(params);
		return resps;
	}
	
	@RequestMapping(value = "/fileops/move", method = RequestMethod.POST)
	@ResponseBody
	public List<FileOperationResponse> move(
			@RequestParam(value = "srcFiles[]", required = false) String[] srcFiles, 
			@RequestParam(value = "destFolder", required = false) String destFolder, 
			HttpServletRequest request) {
		User user = WebUtil.getUser(request);
		String[] srcFilePaths = decodeUrl(srcFiles);
		String destFolderPath = decodeUrl(destFolder);
		log.info("User {} move {} to {}", user.getName(), srcFilePaths, destFolderPath);
		
		int len = srcFilePaths.length;
		String[] destFilePaths = new String[len];
		for (int i = 0; i < len; i++) {
			String srcFileName = PathUtil.getLastPath(srcFilePaths[i]);
			String destPath = PathUtil.combinePath(destFolderPath, srcFileName);
			destFilePaths[i] = destPath;
		}
		
		MoveParams params = new MoveParams(user, srcFilePaths, destFilePaths);
		List<FileOperationResponse> resps = getService().move(params);
		return resps;
	}

	@RequestMapping(value = "/fileops/copy", method = RequestMethod.POST)
	@ResponseBody
	public List<FileOperationResponse> copy(
			@RequestParam(value = "srcFiles[]", required = false) String[] srcFiles, 
			@RequestParam(value = "destFolder", required = false) String destFolder, 
			HttpServletRequest request) {
		User user = WebUtil.getUser(request);
		String[] srcFilePaths = decodeUrl(srcFiles);
		String destFolderPath = decodeUrl(destFolder);
		log.info("User {} copy {} to {}", user.getName(), srcFilePaths, destFolderPath);
		
		int len = srcFilePaths.length;
		String[] destFilePaths = new String[len];
		for (int i = 0; i < len; i++) {
			String srcFileName = PathUtil.getLastPath(srcFilePaths[i]);
			String destPath = PathUtil.combinePath(destFolderPath, srcFileName);
			destFilePaths[i] = destPath;
		}
		
		CopyParams params = new CopyParams(user, srcFilePaths, destFilePaths);
		List<FileOperationResponse> resps = getService().copy(params);
		return resps;
	}
	
	@RequestMapping(value="/revisions/**")
	public String getRevisions(
			HttpServletRequest request) {
		User user = WebUtil.getUser(request);
		String path = getRestOfPath(request, getServicePathLength() + 10);
		log.debug("User {} get revisions of {}", user.getName(), path);
		
		if (StringUtils.isBlank(path)) {
			return error(request, "metadata", "No valid file is provided to get its revisions!");
		}
		
		RevisionParams params = new RevisionParams(user, path);
		List<MetadataEntry> entries = getService().getRevisions(params);

		request.setAttribute("file", path);
		request.setAttribute("entries", entries);
		return "files/revisions";
	}
	
	@RequestMapping(value="/restore/**", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public void restore(
			@RequestParam(value = "rev", required = false) String rev, 
			HttpServletRequest request) {
		User user = WebUtil.getUser(request);
		String path = getRestOfPath(request, getServicePathLength() + 8);
		log.info("User {} want to restore {} version {}", user.getName(), path, rev);
		
		if (StringUtils.isBlank(rev)) {
			throw new ErrorException(Error.badRequest("Can not find the restore vision of the file " + path));
		}
		
		EntryParams params = new EntryParams(user, path);
		params.setRev(rev);
		getService().restore(params);
	}
	
	/**
	 * Use ResponseEntity to avoid HttpMediaTypeNotAcceptableException: Could not find acceptable representation.
	 * If url path has '.' in the last path, eg: http://ip/xxx/mmm.nnn, Spring will throw HttpMediaTypeNotAcceptableException if no acceptable representation found.
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/shares/**", method = RequestMethod.POST)
	public ResponseEntity<String> link(HttpServletRequest request) {
		User user = WebUtil.getUser(request);
		String path = getRestOfPath(request, getServicePathLength() + 7);
		log.info("User {} create link of {}", user.getName(), path);
		
		LinkParams params = new LinkParams(user, path);
		params.setShortUrl(false);
		Link link = getService().link(params);
		
		String body = JsonConverter.toJson(link);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		ResponseEntity<String> responseEntity = new ResponseEntity<String>(body, headers, HttpStatus.OK);
		return responseEntity;
	}
	
	@RequestMapping(value="/search/**", method = {RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public Page<MetadataEntry> search(
			@RequestParam(value = "query", required = false) String query, 
			HttpServletRequest request) {
		User user = WebUtil.getUser(request);
		String path = getRestOfPath(request, getServicePathLength() + 7);
		log.debug("User {} search {} in {}", user.getName(), query, path);
		
		SearchParams params = new SearchParams(user, path);
		params.setQuery(query);
		List<MetadataEntry> entries = getService().search(params);
		
		if (entries != null) {
			sortByFolder(entries);
		} else {
			//* just return empty list
			entries = new ArrayList<MetadataEntry>();
		}
		
		Page<MetadataEntry> page = new Page<MetadataEntry>(entries);
		return page;
	}
	
	@RequestMapping(value = "/delta", method = RequestMethod.POST)
	@ResponseBody
	public DeltaPage<MetadataEntry> delta(
			@RequestParam(value = "cursor", required = false) String cursor, 
			@RequestParam(value = "locale", required = false) String locale, 
			HttpServletRequest request) {
		User user = WebUtil.getUser(request);
		log.debug("User {} get delta.", user.getName());
		
		DeltaParams params = new DeltaParams();
		params.setUser(user);
		params.setCursor(cursor);
		params.setLocale(locale);
		DeltaPage<MetadataEntry> entry = getService().delta(params);
		log.debug("delta: {}", entry);
		return entry;
	}
	
	@RequestMapping(value="/uploadjs")
	public String getUploadJs(
			@RequestParam(value = "useFlash", required = false) Boolean useFlash, 
			HttpServletRequest request) {
		log.debug("useFlash={}", useFlash);
		String page = "files/jQueryFileUpload";
		if (useFlash != null && useFlash == true) {
			page = "files/swfUpload";
		}
		return page;
	}
	
	protected List<UploadResponse> upload(Boolean isPut, HttpServletRequest request) {
		List<UploadResponse> uploadResps = new ArrayList<UploadResponse>();
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if (!isMultipart) {
			log.warn("It's not multipart form post!");
			return uploadResps;
		}

		String folderPath = null;
		String fileName = null;
		Boolean overwrite = true; // default
		long fileSize = -1;
		try {
			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator iter = upload.getItemIterator(request);
			if (!iter.hasNext()) {
				log.warn("No file item available!");
				return uploadResps;
			}
			
			while (iter.hasNext()) {
				FileItemStream item = iter.next();
				String fieldName = item.getFieldName();
				InputStream content = item.openStream();
				if (item.isFormField()) {
					String value = Streams.asString(content);
					log.debug("Form field name {} value {}", fieldName, value);
					if ("folder".equals(fieldName)) {
						folderPath = decodeUrl(value);
					} else if ("fileSize".equals(fieldName)) {
						if (value != null && !"".equals(value)) {
							fileSize = Long.parseLong(value);
						}
					} else if ("overwrite".equals(fieldName)) {
						if (value != null && !"".equals(value)) {
							overwrite = Boolean.parseBoolean(value);
						}
					}
				} else {
					fileName = item.getName();
					log.debug("File name {}", fileName);
					fileSize = saveFile(request, folderPath, fileName, content, fileSize, overwrite, isPut);
					UploadResponse resp = new UploadResponse(fileName, fileSize);
					uploadResps.add(resp);
				}
			}
		} catch (ErrorException e) {
			Error error = e.getError();
			log.warn("Got error when handling request {} from  {}", request.getRequestURI(), WebUtil.getUserAddress(request), error);
			UploadResponse resp = new UploadResponse(fileName, fileSize, error.getTitle() != null ? error.getTitle() : "failed");
			uploadResps.add(resp);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			log.error("Got exception when handling request {} from  {}", request.getRequestURI(), WebUtil.getUserAddress(request), e.getMessage());
			Error error = Error.internalServerError(e.getMessage());
			UploadResponse resp = new UploadResponse(fileName, fileSize, error.getTitle() != null ? error.getTitle() : "failed");
			uploadResps.add(resp);
		}
		return uploadResps;
	}
	
	protected List<UploadResponse> uploadCached(Boolean isPut, HttpServletRequest request) {
		List<UploadResponse> uploadResps = new ArrayList<UploadResponse>();
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if (!isMultipart) {
			log.warn("It's not multipart form post!");
			return uploadResps;
		}

		String folderPath = null;
		String fileName = null;
		Boolean overwrite = true; // default
		long fileSize = -1;
		try {
			DiskFileItemFactory factory = new DiskFileItemFactory();
			factory.setSizeThreshold(1024);
			ServletFileUpload upload = new ServletFileUpload(factory);
			List<?> items = upload.parseRequest(request);
			Iterator<?> iter = items.iterator();
			if (!iter.hasNext()) {
				log.warn("No file item available!");
				return uploadResps;
			}
			
			while (iter.hasNext()) {
				FileItem item = (FileItem) iter.next();
				String fieldName = item.getFieldName();
				if (item.isFormField()) {
					String value = item.getString();
					log.debug("Form field name {} value {}", fieldName, value);
					if ("folder".equals(fieldName)) {
						folderPath = decodeUrl(value);
					} else if ("overwrite".equals(fieldName)) {
						if (value != null && !"".equals(value)) {
							overwrite = Boolean.parseBoolean(value);
						}
					}
				} else {
					InputStream content = item.getInputStream();
					fileSize = item.getSize();
					fileName = item.getName();
					
					fileSize = saveFile(request, folderPath, fileName, content, fileSize, overwrite, isPut);
					UploadResponse resp = new UploadResponse(fileName, fileSize);
					uploadResps.add(resp);
				}
			}
		} catch (ErrorException e) {
			Error error = e.getError();
			log.warn("Got error when handling request {} from  {}", request.getRequestURI(), WebUtil.getUserAddress(request), error);
			UploadResponse resp = new UploadResponse(fileName, fileSize, error.getTitle() != null ? error.getTitle() : "failed");
			uploadResps.add(resp);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			log.error("Got exception when handling request {} from  {}", request.getRequestURI(), WebUtil.getUserAddress(request), e.getMessage());
			Error error = Error.internalServerError(e.getMessage());
			UploadResponse resp = new UploadResponse(fileName, fileSize, error.getTitle() != null ? error.getTitle() : "failed");
			uploadResps.add(resp);
		}
		return uploadResps;
	}
	
	protected long saveFile(HttpServletRequest request, String folderPath, String fileName, 
			InputStream content, long contentLength, Boolean overwrite, Boolean isPut) {
		User user = WebUtil.getUser(request);
		String filePath = PathUtil.combinePath(folderPath, fileName);
		log.info("User {} save {} size {}", user.getName(), filePath, contentLength);

		MetadataEntry entry = null;
		if (contentLength >= UPLOAD_FILE_SIZE_LIMIT) {
			ChunkedUploadParams params = new ChunkedUploadParams(user, filePath);
			params.setOverwrite(overwrite);
			params.setContent(content);
			params.setLength(contentLength);
			entry = getService().chunkedUpload(params);
		} else {
			UploadParams params = new UploadParams(user, filePath);
			params.setOverwrite(overwrite);
			params.setContent(content);
			params.setLength(contentLength);
			entry = getService().upload(params, isPut);
		}
		return entry.getBytes();
	}
	
	protected void sortByFolder(List<MetadataEntry> list) {
		Collections.sort(list, new Comparator<MetadataEntry>() {
			@Override
			public int compare(MetadataEntry o1, MetadataEntry o2) {
				if (o1.getIsDir() && !o2.getIsDir()) {
					return -1;
				}
				if (!o1.getIsDir() && o2.getIsDir()) {
					return 1;
				}
				return o1.getName().compareTo(o2.getName());
			}
		});
	}
}
