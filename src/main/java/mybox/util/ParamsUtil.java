package mybox.util;

import java.util.ArrayList;
import java.util.List;

import mybox.to.CreateParams;
import mybox.to.EntryParams;
import mybox.to.MetadataParams;
import mybox.to.UploadParams;

public class ParamsUtil {

	public static String[] getQueryString(MetadataParams params) {
		boolean list = params.isList();
		boolean includeDeleted = params.isIncludeDeleted();
		int fileLimit = params.getFileLimit();
		String hash = params.getHash();
		String rev = params.getRev();
		String locale = params.getLocale();
		
		List<String> qryStr = new ArrayList<String>();
		qryStr.add("list");
		qryStr.add(String.valueOf(list));
		if (list) {
			qryStr.add("include_deleted");
			qryStr.add(String.valueOf(includeDeleted));
		}
		
		if (fileLimit <= 0 || fileLimit > MetadataParams.METADATA_DEFAULT_LIMIT) {
			fileLimit = MetadataParams.METADATA_DEFAULT_LIMIT;
		}
		qryStr.add("file_limit");
		qryStr.add(String.valueOf(fileLimit));
		
		if (hash != null && !"".equals(hash)) {
			qryStr.add("hash");
			qryStr.add(hash);
		}
		if (rev != null && !"".equals(rev)) {
			qryStr.add("rev");
			qryStr.add(rev);
		}
		if (locale != null && !"".equals(locale)) {
			qryStr.add("locale");
			qryStr.add(locale);
		}
		return qryStr.toArray(new String[qryStr.size()]);
	}
	
	public static String[] getQueryString(EntryParams params) {
		String rev = params.getRev();
		String locale = params.getLocale();
		
		List<String> qryStr = new ArrayList<String>();
		if (rev != null && !"".equals(rev)) {
			qryStr.add("rev");
			qryStr.add(rev);
		}
		
		if (locale != null && !"".equals(locale)) {
			qryStr.add("locale");
			qryStr.add(locale);
		}
		return qryStr.toArray(new String[qryStr.size()]);
	}
	
	public static String[] getQueryString(UploadParams params) {
		boolean overwrite = params.isOverwrite();
		String parentRev = params.getParentRev();
		String locale = params.getLocale();
		
		List<String> qryStr = new ArrayList<String>();
		if (!overwrite) {
			qryStr.add("overwrite");
			qryStr.add("false");
		}

		if (parentRev != null && !"".equals(parentRev)) {
			qryStr.add("parent_rev");
			qryStr.add(parentRev);
		}
		
		if (locale != null && !"".equals(locale)) {
			qryStr.add("locale");
			qryStr.add(locale);
		}
		return qryStr.toArray(new String[qryStr.size()]);
	}
	
	public static String[] getQueryString(CreateParams params) {
		String path = params.getPath();
		String root = params.getRoot();
		String locale = params.getLocale();
		
		List<String> qryStr = new ArrayList<String>();
		qryStr.add("path");
		qryStr.add(path);

		if (root != null && !"".equals(root)) {
			qryStr.add("root");
			qryStr.add(root);
		}
		
		if (locale != null && !"".equals(locale)) {
			qryStr.add("locale");
			qryStr.add(locale);
		}
		return qryStr.toArray(new String[qryStr.size()]);
	}
}
