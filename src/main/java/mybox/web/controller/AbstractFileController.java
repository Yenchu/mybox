package mybox.web.controller;

public abstract class AbstractFileController extends BaseController {

	protected static final long DOWNLOAD_FILE_SIZE_THRESOLD = 2 * 1024 * 1024 * 1024; // 2G

	protected static final long UPLOAD_FILE_SIZE_LIMIT = 12 * 1024 * 1024; // 12M
	
}
