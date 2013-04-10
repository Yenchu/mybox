package mybox.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mybox.model.DeltaPage;
import mybox.model.FileEntry;
import mybox.model.Link;
import mybox.model.MetadataEntry;
import mybox.model.Space;
import mybox.model.User;
import mybox.to.ChunkedUploadParams;
import mybox.to.CopyParams;
import mybox.to.CreateParams;
import mybox.to.DeleteParams;
import mybox.to.DeltaParams;
import mybox.to.EntryParams;
import mybox.to.FileOperationResponse;
import mybox.to.LinkParams;
import mybox.to.LoginParams;
import mybox.to.MetadataParams;
import mybox.to.MoveParams;
import mybox.to.Page;
import mybox.to.Params;
import mybox.to.PathParams;
import mybox.to.RevisionParams;
import mybox.to.SearchParams;
import mybox.to.ThumbnailParams;
import mybox.to.UploadParams;
import mybox.util.EntryUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DiskServiceImpl implements DiskService {
	
	private static final Logger log = LoggerFactory.getLogger(DiskServiceImpl.class);

	protected String DEFAULT_ROOT = "../logs";//"/home/t1/Downloads"; // limit file access to this folder

	public User auth(LoginParams params) {
		if (!params.getPassword().equals("cloud")) {
			log.info("{} from {} login failed!", params.getUsername(), params.getIp());
			return null;
		}

		User user = new User();
		user.setName(params.getUsername());
		user.setIp(params.getIp());
		return user;
	}
	
	protected Space getDefaultSpace() {
		return getDefaultSpace(null);
	}
	
	public Space getDefaultSpace(Params params) {
		Space space = new Space();
		space.setId("myspace");
		space.setName("MySpace");
		space.setRoot(DEFAULT_ROOT);
		return space;
	}
	
	public Space getSpace(Params params, String spaceId) {
		return getDefaultSpace(params);
	}
	
	public Space getSpace(PathParams params) {
		return getSpace(params, params.getRoot());
	}
	
	public MetadataEntry getFiles(PathParams params) {
		return getFiles(params, false);
	}
	
	public MetadataEntry getFiles(MetadataParams params) {
		return getFiles((PathParams) params);
	}
	
	public MetadataEntry getFolders(MetadataParams params) {
		return getFiles(params, true);
	}
	
	protected MetadataEntry getFiles(PathParams params, boolean folderOnly) {
		String folderPathStr = params.getPath();
		folderPathStr = validatePath(folderPathStr);
		Path folderPath = Paths.get(folderPathStr);
		
		MetadataEntry folderEntry = new MetadataEntry();
		folderEntry.setPath(folderPathStr);
		folderEntry.setIsDir(true);
		
		Space space = getSpace(params);
		List<MetadataEntry> entries = new ArrayList<MetadataEntry>();
		try {
			DirectoryStream<Path> stream = Files.newDirectoryStream(folderPath);
			for (Path path: stream) {
				MetadataEntry entry = getMetadata(path, folderOnly);
				if (entry != null) {
					entries.add(entry);
				}
			}
			folderEntry.setContents(entries);
			EntryUtil.customEntries(space, folderEntry);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return folderEntry;
	}
	
	public Page<MetadataEntry> getFiles(String folderPathStr, Pageable pageable) {
		folderPathStr = validatePath(folderPathStr);
		Path folderPath = Paths.get(folderPathStr);
		
		Page<MetadataEntry> page = null;
		List<MetadataEntry> entries = new ArrayList<MetadataEntry>();
		try {
			DirectoryStream<Path> stream = Files.newDirectoryStream(folderPath);
			int offset = pageable.getOffset();
			int limit = offset + pageable.getPageSize();
			int i = -1;
			for (Path path: stream) {
				i++;
				if (i < offset) {
					continue;
				}
				if (i < limit) {
					MetadataEntry entry = getMetadata(path, false);
					if (entry != null) {
						entries.add(entry);
					}
				}
			}
			
			i++;
			EntryUtil.customEntries(entries);
			page = new Page<MetadataEntry>(entries);
			page.setPage(pageable.getPageNumber());
			page.setPageSize(pageable.getPageSize());
			page.setTotalRecords(i);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return page;
	}

	protected MetadataEntry getMetadata(Path path, boolean folderOnly) throws IOException {
		BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
		boolean isDir = (attr.isDirectory() ? true : false);
		if (folderOnly && !isDir) {
			return null;
		}
		
		MetadataEntry entry = new MetadataEntry();
		entry.setIsDir(isDir);
		entry.setModified(attr.lastModifiedTime().toString());
		long bytes = attr.size();
		entry.setBytes(bytes);
		
		String pathStr = path.toString();
		entry.setPath(pathStr);
		return entry;
	}
	
	public FileEntry download(EntryParams params) {
		return null;
	}
	
	public InputStream getThumbnail(ThumbnailParams params) {
		return null;
	}
	
	public MetadataEntry upload(UploadParams params) {
		String path = params.getPath();
		path = validatePath(path);
		InputStream is = params.getContent();
		saveFile(is, path, false);
		
		MetadataEntry entry = null;
		try {
			entry = getMetadata(Paths.get(path), false);
			EntryUtil.customEntry(entry);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return entry;
	}
	
	protected long saveFile(InputStream srcStream, String filePathStr, boolean append) {
		long fileSize = 0;
		ReadableByteChannel is = null;
		FileChannel os = null;
		try {
			is = Channels.newChannel(srcStream);
			File destFile = new File(filePathStr);
			File parent = destFile.getParentFile();
			if (!parent.exists()) {
				parent.mkdirs();
			}
			Path destPath = destFile.toPath();
			if (!Files.exists(destPath)) {
				Files.createFile(destPath);
			}
			
			Set<OpenOption> options = new HashSet<OpenOption>();
			if (append) {
				options.add(StandardOpenOption.APPEND);
			} else {
				options.add(StandardOpenOption.CREATE);
			}
			options.add(StandardOpenOption.WRITE);
			
			os = FileChannel.open(destPath, options);
			ByteBuffer buf = ByteBuffer.allocateDirect(64 * 1024);
			while (is.read(buf) != -1) {
				buf.flip();
				os.write(buf);
				buf.clear();
			}
			fileSize = os.size();
		} catch (FileNotFoundException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}
		return fileSize;
	}
	
	protected String validatePath(String path) {
		if (path == null || "".equals(path) || "/".equals(path)) {
			return DEFAULT_ROOT;
		}
		if (!path.startsWith(DEFAULT_ROOT)) {
			return DEFAULT_ROOT + path;
		}
		return path;
	}
	
	public MetadataEntry chunkedUpload(ChunkedUploadParams params) {
		return null;
	}
	
	public DeltaPage<MetadataEntry> delta(DeltaParams params) {
		return null;
	}
	
	public List<MetadataEntry> getRevisions(RevisionParams params) {
		return null;
	}
	
	public MetadataEntry restore(EntryParams params) {
		return null;
	}
	
	public Link link(LinkParams params) {
		return null;
	}
	
	public Link media(PathParams params) {
		return null;
	}
	
	public List<MetadataEntry> search(SearchParams params) {
		return null;
	}
	
	public FileOperationResponse createFolder(CreateParams params) {
		return null;
	}
	
	public List<FileOperationResponse> delete(DeleteParams params) {
		return null;
	}
	
	public List<FileOperationResponse> move(MoveParams params) {
		return null;
	}

	public List<FileOperationResponse> copy(CopyParams params) {
		return null;
	}
}
