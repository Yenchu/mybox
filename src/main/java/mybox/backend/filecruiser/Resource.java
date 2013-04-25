package mybox.backend.filecruiser;

public interface Resource {

	public static final String TOKENS = "auth/tokens";
	
	public static final String DOMAINS = "domains";
	
	public static final String GROUPS = "groups";
	
	public static final String USERS = "users";
	
	public static final String ROLES = "roles";
	
	public static final String PROJECTS = "projects";
	
	public static final String SERVICES = "services";
	
	public static final String ENDPOINTS = "endpoints";
	
	public static final String METADATA = "metadata";
	
	public static final String FILES = "files";
	
	public static final String CHUNKED_UPLOAD = "chunked_upload";
	
	public static final String COMMIT_CHUNKED_UPLOAD = "commit_chunked_upload";
	
	public static final String CREATE_FOLDER = "fileops/create_folder";
	
	public static final String DELETE_FILE = "fileops/delete";
	
	public static final String MOVE_FILE = "fileops/move";
	
	public static final String COPY_FILE = "fileops/copy";
	
	public static final String SHARE_FILE = "fileops/share_file";
	
	public static final String SHARE_LINK = "shares";
	
	public static final String LINKS = "links";
	
	public static final String SEARCH = "search";
	
	public static final String REVISIONS = "revisions";
	
	public static final String RESTORE = "restore";
	
	public static final String DELTA = "delta";
	
	public static final String LOCK = "lock";
	
}
