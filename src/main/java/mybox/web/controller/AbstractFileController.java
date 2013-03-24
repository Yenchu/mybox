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

import mybox.common.to.CopyParams;
import mybox.common.to.CreateParams;
import mybox.common.to.DeleteParams;
import mybox.common.to.DeltaParams;
import mybox.common.to.EntryParams;
import mybox.common.to.LinkParams;
import mybox.common.to.LoginParams;
import mybox.common.to.MetadataParams;
import mybox.common.to.MoveParams;
import mybox.common.to.Params;
import mybox.common.to.RevisionParams;
import mybox.common.to.SearchParams;
import mybox.common.to.Space;
import mybox.common.to.ThumbnailParams;
import mybox.common.to.UploadParams;
import mybox.common.to.User;
import mybox.dropbox.model.DeltaPage;
import mybox.dropbox.model.FileEntry;
import mybox.dropbox.model.Link;
import mybox.dropbox.model.MetadataEntry;
import mybox.json.Json;
import mybox.rest.Fault;
import mybox.rest.FaultException;
import mybox.service.FileService;
import mybox.util.FileUtil;
import mybox.util.HttpUtil;
import mybox.util.UserAgentParser;
import mybox.util.WebUtil;
import mybox.web.to.AuthResponse;
import mybox.web.to.FileOperationResponse;
import mybox.web.to.Page;
import mybox.web.to.TreeNode;
import mybox.web.to.UploadResponse;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


public abstract class AbstractFileController extends BaseController {
	
	private static final Logger log = LoggerFactory.getLogger(AbstractFileController.class);

	protected static final long DOWNLOAD_FILE_SIZE_THRESOLD = 2 * 1024 * 1024 * 1024; // 2G
	
	protected abstract FileService getService();
	
	protected int getServicePathLength() {
		return 0;
	}

	@RequestMapping(value="/login", method = RequestMethod.POST)
	@ResponseBody
	public AuthResponse auth(
			@RequestParam(value = "username", required = false) String username,
			@RequestParam(value = "password", required = false) String password,
			HttpServletRequest request) {
		String address = WebUtil.getUserAddress(request);
		log.info("User {} from {} login!", new Object[] {username, address});
		
		LoginParams param = new LoginParams(username, password);
		param.setAddress(address);
		User user = getService().auth(param);
		
		if (user == null) {
			throw new FaultException(Fault.unauthorized());
		}
		setUser(request, user);
		
		String path = WebUtil.getFirstPathAfterContextPath(request);
		String serviceUrl = request.getContextPath() + path;
		AuthResponse authResp = new AuthResponse();
		authResp.setServiceUrl(serviceUrl);
		return authResp;
	}

	@RequestMapping(value={"", "/"})
	public String home(HttpServletRequest request) {
		return getFiles(null, request);
	}
	
	@RequestMapping(value = "/metadata/**")
	public String getFiles(
			@RequestParam(value = "space", required = false) String spaceId,
			HttpServletRequest request) {
		User user = getUser(request);
		String folder = getRestOfPath(request, getServicePathLength() + 9); // 8 + 9: sum of length(/dropbox + /metadata)
		log.debug("User {} list {}:{}", new Object[]{user, (spaceId != null ? spaceId : "root"), folder});
		
		Params params = new Params();
		params.setUser(user);
		Space space = getService().getSpace(params, spaceId);
		if (folder == null || "".equals(folder)) {
			folder = space.getRoot();
		}
		request.setAttribute("space", space);
		request.setAttribute("currentFolder", folder);
		
		log.debug("{} user agent: {}", WebUtil.getUserAddress(request), request.getHeader("user-agent"));
		return "files";
	}

	@RequestMapping(value = "/metadata", method = RequestMethod.POST)
	@ResponseBody
	public Page<MetadataEntry> pageFiles(
			@RequestParam(value = "space", required = false) String space, 
			@RequestParam(value = "folder", required = false) String folder, 
			HttpServletRequest request) {
		User user = getUser(request);
		String path = urlDecode(folder);
		log.debug("User {} page {}:{}", new Object[]{user.toString(), space, path});

		MetadataParams params = new MetadataParams(user, space, path);
		params.setList(true);
		MetadataEntry entry = getService().getFiles(params);

		List<MetadataEntry> entries = entry.getContents();
		if (entries != null) {
			sortByFolder(entries);
		} else {
			//* just return empty list to jqGrid
			entries = new ArrayList<MetadataEntry>();
		}

		Page<MetadataEntry> page = new Page<MetadataEntry>(entries);
		return page;
	}
	
	@RequestMapping(value="/roottree")
	@ResponseBody
	public List<TreeNode> getRoot(
			@RequestParam(value = "space", required = false) String space, 
			@RequestParam(value = "folder", required = false) String folder, 
			HttpServletRequest request) {
		User user = getUser(request);
		String path = urlDecode(folder);
		log.debug("User {} dir {}:{}", new Object[]{user.toString(), space, path});
		
		MetadataParams params = new MetadataParams(user, space, path);
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
			@RequestParam(value = "space", required = false) String space, 
			@RequestParam(value = "folder", required = false) String folder, 
			HttpServletRequest request) {
		User user = getUser(request);
		String path = urlDecode(folder);
		log.debug("User {} dir {}:{}", new Object[]{user.toString(), space, path});
		
		MetadataParams params = new MetadataParams(user, space, path);
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

	@RequestMapping(value = "/files/{space}/**")
	public void download(
			@PathVariable String space, 
			HttpServletRequest request, HttpServletResponse response) {
		User user = getUser(request);
		String path = getRestOfPath(request, getServicePathLength() + 7 + space.length()); // 8 + 6 + (1 + space.length)
		if (path == null || "".equals(path)) {
			throw new FaultException(Fault.badRequest("Download file request from " + user.toString() + " doesn't have file path!"));
		}
		log.info("User {} download {}:{}", new Object[]{user.toString(), space, path});
		
		EntryParams params = new EntryParams(user, space, path);
		FileEntry entry = getService().download(params);
		
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
				response.setHeader("Content-Disposition", "attachment; filename=\"" + HttpUtil.encodeUrl(fileName).replace("+", "%20") + "\"");
			} else if (UserAgentParser.isSafari(userAgent)) {
				// Safari doesn't support non US-ASCII characters in file name
				response.setCharacterEncoding("UTF-8");
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
	
	@RequestMapping(value = "/thumbnails/{space}/**")
	public void getThumbnail( 
			@PathVariable String space, 
			HttpServletRequest request, HttpServletResponse response) {
		User user = getUser(request);
		String path = getRestOfPath(request, getServicePathLength() + 12 + space.length()); // 8 + 11 + (1 + space.length)
		if (path == null || "".equals(path)) {
			throw new FaultException(Fault.badRequest("Get thumbnail request from " + user.toString() + " doesn't have file path!"));
		}
		log.debug("User {} get thumbnail {}:{}", new Object[]{user.toString(), space, path});
		
		ThumbnailParams params = new ThumbnailParams(user, space, path);
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
		User user = getUser(request);
		log.info("User {} upload file: detectFileSize={}", user.toString(), detectFileSize);
		
		List<UploadResponse> uploadResps = null;
		if (detectFileSize) {
			uploadResps = upload(request);
		} else {
			log.debug("Save uploaded file to tmp folder for request {} from {}.", request.getRequestURI(), WebUtil.getUserAddress(request));
			uploadResps = uploadCached(request);
		}
		
		for (UploadResponse uploadResp: uploadResps) {
			String result = (uploadResp.getError() != null ? uploadResp.getError() : "success");
			log.debug("Uploading {} size {} result: {}.", new Object[] {uploadResp.getName(), uploadResp.getSize(), result});
		}
		
		try {
			// IE 9 doesn't support application/json, so using text/plain as workaround.
			String body = Json.toJson(uploadResps);
			response.setContentType("text/plain;charset=UTF-8");
			response.getWriter().print(body);
			response.getWriter().flush();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			log.error("Got exception when handling request {} from {}: {}", new Object[]{request.getRequestURI(), WebUtil.getUserAddress(request), e.getMessage()});
		}
	}
	
	@RequestMapping(value = "/delta", method = RequestMethod.POST)
	@ResponseBody
	public DeltaPage<MetadataEntry> delta( 
			@RequestParam(value = "cursor", required = false) String cursor, 
			@RequestParam(value = "locale", required = false) String locale, 
			HttpServletRequest request) {
		User user = getUser(request);
		log.debug("User {} get delta.", user.toString());
		
		DeltaParams params = new DeltaParams();
		params.setUser(user);
		params.setCursor(cursor);
		params.setLocale(locale);
		DeltaPage<MetadataEntry> entry = getService().delta(params);
		log.debug("delta: {}", entry);
		return entry;
	}

	@RequestMapping(value = "/fileops/edit", method = RequestMethod.POST)
	@ResponseBody
	public FileOperationResponse edit( 
			@RequestParam(value = "space", required = false) String space, 
			@RequestParam(value = "folder", required = false) String folder, 
			@RequestParam(value = "id", required = false) String id, 
			@RequestParam(value = "name", required = false) String name, 
			HttpServletRequest request) {
		User user = getUser(request);
		String parentPath = urlDecode(folder);
		
		FileOperationResponse resp = null;
		if ("0".equals(id)) {
			// create folder
			String folderPath = FileUtil.getFilePath(parentPath, name);
			log.info("User {} create folder {}: }", new Object[]{user.toString(), space, folderPath});
			
			CreateParams params = new CreateParams(user, space, folderPath);
			resp = getService().createFolder(params);
		} else {
			// rename
			String srcPath = urlDecode(id);
			String destPath = FileUtil.getFilePath(parentPath, name);
			log.info("User {} rename {}:{} to {}", new Object[]{user.toString(), space, srcPath, destPath});
			
			MoveParams params = new MoveParams(user, space, new String[]{srcPath}, new String[]{destPath});
			List<FileOperationResponse> resps = getService().move(params);
			if (resps != null && resps.size() == 1) {
				resp = resps.get(0);
			}
		}
		return resp;
	}
	
	@RequestMapping(value = "/fileops/delete", method = RequestMethod.POST)
	@ResponseBody
	public List<FileOperationResponse> delete( 
			@RequestParam(value = "space", required = false) String space, 
			@RequestParam(value = "files[]", required = false) String[] files, 
			HttpServletRequest request) {
		User user = getUser(request);
		String[] filePaths = urlDecode(files);
		log.info("User {} delete {}:{}", new Object[]{user.toString(), space, filePaths});
		
		DeleteParams params = new DeleteParams(user, space, filePaths);
		List<FileOperationResponse> resps = getService().delete(params);
		return resps;
	}
	
	@RequestMapping(value = "/fileops/move", method = RequestMethod.POST)
	@ResponseBody
	public List<FileOperationResponse> move( 
			@RequestParam(value = "space", required = false) String space, 
			@RequestParam(value = "srcFiles[]", required = false) String[] srcFiles, 
			@RequestParam(value = "destFolder", required = false) String destFolder, 
			HttpServletRequest request) {
		User user = getUser(request);
		String[] srcFilePaths = urlDecode(srcFiles);
		String destFolderPath = urlDecode(destFolder);
		log.info("User {} move {}:{} to {}", new Object[]{user.toString(), space, srcFilePaths, destFolderPath});
		
		int len = srcFilePaths.length;
		String[] destFilePaths = new String[len];
		for (int i = 0; i < len; i++) {
			String srcFileName = FileUtil.getNameFromPath(srcFilePaths[i]);
			String destPath = FileUtil.getFilePath(destFolderPath, srcFileName);
			destFilePaths[i] = destPath;
		}
		
		MoveParams params = new MoveParams(user, space, srcFilePaths, destFilePaths);
		List<FileOperationResponse> resps = getService().move(params);
		return resps;
	}

	@RequestMapping(value = "/fileops/copy", method = RequestMethod.POST)
	@ResponseBody
	public List<FileOperationResponse> copy( 
			@RequestParam(value = "space", required = false) String space, 
			@RequestParam(value = "srcFiles[]", required = false) String[] srcFiles, 
			@RequestParam(value = "destFolder", required = false) String destFolder, 
			HttpServletRequest request) {
		User user = getUser(request);
		String[] srcFilePaths = urlDecode(srcFiles);
		String destFolderPath = urlDecode(destFolder);
		log.info("User {} copy {}:{} to {}", new Object[]{user.toString(), space, srcFilePaths, destFolderPath});
		
		int len = srcFilePaths.length;
		String[] destFilePaths = new String[len];
		for (int i = 0; i < len; i++) {
			String srcFileName = FileUtil.getNameFromPath(srcFilePaths[i]);
			String destPath = FileUtil.getFilePath(destFolderPath, srcFileName);
			destFilePaths[i] = destPath;
		}
		
		CopyParams params = new CopyParams(user, space, srcFilePaths, destFilePaths);
		List<FileOperationResponse> resps = getService().copy(params);
		return resps;
	}
	
	@RequestMapping(value="/revisions/{space}/**")
	public String getRevisions( 
			@PathVariable String space, 
			HttpServletRequest request) {
		User user = getUser(request);
		String path = getRestOfPath(request, getServicePathLength() + 11 + space.length()); // 8 + 10 + (1 + space.length)
		log.debug("User {} get revisions of {}:{}", new Object[]{user.toString(), space, path});
		
		if (StringUtils.isBlank(path) || StringUtils.isBlank(space)) {
			return error(request, "metadata", "No valid file is provided to get its revisions!");
		}
		
		RevisionParams params = new RevisionParams(user, space, path);
		List<MetadataEntry> entries = getService().getRevisions(params);

		request.setAttribute("space", space);
		request.setAttribute("file", path);
		request.setAttribute("entries", entries);
		return "revisions";
	}
	
	@RequestMapping(value="/restore", method = RequestMethod.POST)
	@ResponseBody
	public MetadataEntry restore( 
			@RequestParam(value = "space", required = false) String space, 
			@RequestParam(value = "file", required = false) String file,
			@RequestParam(value = "rev", required = false) String rev, 
			HttpServletRequest request) {
		User user = getUser(request);
		String path = urlDecode(file);
		log.info("User {} want to restore {}:{} version {}", new Object[]{user.toString(), space, path, rev});
		
		if (StringUtils.isBlank(rev)) {
			throw new FaultException(Fault.badRequest("Can not find the restore vision of the file " + path));
		}
		
		EntryParams params = new EntryParams(user, space, path);
		params.setRev(rev);
		MetadataEntry entry = getService().restore(params);
		return entry;
	}
	
	@RequestMapping(value="/link", method = RequestMethod.POST)
	@ResponseBody
	public Link link( 
			@RequestParam(value = "space", required = false) String space, 
			@RequestParam(value = "file", required = false) String file, 
			HttpServletRequest request) {
		User user = getUser(request);
		String path = urlDecode(file);
		log.info("User {} share link of {}:{}", new Object[]{user.toString(), space, path});
		
		LinkParams params = new LinkParams(user, space, path);
		params.setShortUrl(false);
		Link link = getService().link(params);
		return link;
	}
	
	@RequestMapping(value="/search", method = RequestMethod.POST)
	@ResponseBody
	public Page<MetadataEntry> search(
			@RequestParam(value = "space", required = false) String space, 
			@RequestParam(value = "folder", required = false) String folder, 
			@RequestParam(value = "query", required = false) String query, 
			HttpServletRequest request) {
		User user = getUser(request);
		String path = urlDecode(folder);
		log.debug("User {} search {} in {}:{}", new Object[]{user.toString(), query, space, path});
		
		SearchParams params = new SearchParams(user, space, path);
		params.setQuery(query);
		List<MetadataEntry> entries = getService().search(params);
		
		if (entries != null) {
			sortByFolder(entries);
		} else {
			//* just return empty list to jqGrid
			entries = new ArrayList<MetadataEntry>();
		}
		
		Page<MetadataEntry> page = new Page<MetadataEntry>(entries);
		return page;
	}
	
	@RequestMapping(value="/uploadjs")
	public String getUploadJs(
			@RequestParam(value = "useFlash", required = false) Boolean useFlash, 
			HttpServletRequest request) {
		log.debug("useFlash={}", useFlash);
		String page = "upload/jQueryFileUpload";
		if (useFlash != null && useFlash == true) {
			page = "upload/swfUpload";
		}
		return page;
	}
	
	protected List<UploadResponse> upload(HttpServletRequest request) {
		List<UploadResponse> uploadResps = new ArrayList<UploadResponse>();
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if (!isMultipart) {
			log.warn("It's not multipart form post!");
			return uploadResps;
		}

		String space = null;
		String folderPath = null;
		String fileName = null;
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
					if ("space".equals(fieldName)) {
						space = value;
					} else if ("folder".equals(fieldName)) {
						folderPath = urlDecode(value);
					} else if ("fileSize".equals(fieldName)) {
						if (value != null && !"".equals(value)) {
							fileSize = Long.parseLong(value);
						}
					}
				} else {
					fileName = item.getName();
					log.debug("File name {}", fileName);
					fileSize = saveFile(request, space, folderPath, fileName, content, fileSize);
					UploadResponse resp = new UploadResponse(fileName, fileSize);
					uploadResps.add(resp);
				}
			}
		} catch (FaultException e) {
			Fault fault = e.getFault();
			log.warn("Got fault when handling request {} from {}: {}", new Object[]{request.getRequestURI(), WebUtil.getUserAddress(request), fault});
			UploadResponse resp = new UploadResponse(fileName, fileSize, fault.getType());
			uploadResps.add(resp);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			log.error("Got exception when handling request {} from {}: {}", new Object[]{request.getRequestURI(), WebUtil.getUserAddress(request), e.getMessage()});
			Fault fault = Fault.internalServerError(e.getMessage());
			UploadResponse resp = new UploadResponse(fileName, fileSize, fault.getType());
			uploadResps.add(resp);
		}
		return uploadResps;
	}
	
	protected List<UploadResponse> uploadCached(HttpServletRequest request) {
		List<UploadResponse> uploadResps = new ArrayList<UploadResponse>();
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if (!isMultipart) {
			log.warn("It's not multipart form post!");
			return uploadResps;
		}

		String space = null;
		String folderPath = null;
		String fileName = null;
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
					if ("space".equals(fieldName)) {
						space = value;
					} else if ("folder".equals(fieldName)) {
						folderPath = urlDecode(value);
					}
				} else {
					InputStream content = item.getInputStream();
					fileSize = item.getSize();
					fileName = item.getName();
					
					fileSize = saveFile(request, space, folderPath, fileName, content, fileSize);
					UploadResponse resp = new UploadResponse(fileName, fileSize);
					uploadResps.add(resp);
				}
			}
		} catch (FaultException e) {
			Fault fault = e.getFault();
			log.warn("Got fault when handling request {} from {}: {}", new Object[]{request.getRequestURI(), WebUtil.getUserAddress(request), fault});
			UploadResponse resp = new UploadResponse(fileName, fileSize, fault.getType());
			uploadResps.add(resp);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			log.error("Got exception when handling request {} from {}: {}", new Object[]{request.getRequestURI(), WebUtil.getUserAddress(request), e.getMessage()});
			Fault fault = Fault.internalServerError(e.getMessage());
			UploadResponse resp = new UploadResponse(fileName, fileSize, fault.getType());
			uploadResps.add(resp);
		}
		return uploadResps;
	}
	
	protected long saveFile(HttpServletRequest request, String space, String folderPath, String fileName,
			InputStream content, long contentLength) {
		User user = getUser(request);
		String filePath = FileUtil.getFilePath(folderPath, fileName);
		log.info("User {} save {}: {} size {}", new Object[] {user.toString(), space, filePath, contentLength});

		UploadParams params = new UploadParams(user, space, filePath);
		params.setContent(content);
		params.setLength(contentLength);
		MetadataEntry entry = getService().upload(params);
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
