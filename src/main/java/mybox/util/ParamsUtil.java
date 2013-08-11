package mybox.util;

import java.util.ArrayList;
import java.util.List;

import mybox.to.BulkParams;
import mybox.to.CreateParams;
import mybox.to.DeltaParams;
import mybox.to.EntryParams;
import mybox.to.LinkParams;
import mybox.to.MetadataParams;
import mybox.to.MoveParams;
import mybox.to.PathParams;
import mybox.to.RevisionParams;
import mybox.to.SearchParams;
import mybox.to.ThumbnailParams;
import mybox.to.UploadParams;

import org.apache.commons.lang3.StringUtils;

public class ParamsUtil {

	public static List<String> getParamList(PathParams params) {
		String root = params.getRoot();
		String locale = params.getLocale();
		List<String> list = new ArrayList<String>();
		if (StringUtils.isNotEmpty(root)) {
			list.add("root");
			list.add(root);
		}
		if (StringUtils.isNotEmpty(locale)) {
			list.add("locale");
			list.add(locale);
		}
		return list;
	}
	public static String[] getQueryString(PathParams params) {
		List<String> qryStr = getParamList(params);
		return qryStr.toArray(new String[qryStr.size()]);
	}
	
	public static List<String> getParamList(EntryParams params) {
		String rev = params.getRev();
		String root = params.getRoot();
		String locale = params.getLocale();
		
		List<String> list = new ArrayList<String>();
		if (StringUtils.isNotEmpty(rev)) {
			list.add("rev");
			list.add(rev);
		}
		if (StringUtils.isNotEmpty(root)) {
			list.add("root");
			list.add(root);
		}
		if (StringUtils.isNotEmpty(locale)) {
			list.add("locale");
			list.add(locale);
		}
		return list;
	}
	public static String[] getQueryString(EntryParams params) {
		List<String> qryStr = getParamList(params);
		return qryStr.toArray(new String[qryStr.size()]);
	}

	public static String[] getQueryString(MetadataParams params) {
		boolean list = params.isList();
		boolean includeDeleted = params.isIncludeDeleted();
		int fileLimit = params.getFileLimit();
		String hash = params.getHash();
		String rev = params.getRev();
		String root = params.getRoot();
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
		
		if (StringUtils.isNotEmpty(hash)) {
			qryStr.add("hash");
			qryStr.add(hash);
		}
		if (StringUtils.isNotEmpty(rev)) {
			qryStr.add("rev");
			qryStr.add(rev);
		}
		if (StringUtils.isNotEmpty(root)) {
			qryStr.add("root");
			qryStr.add(root);
		}
		if (StringUtils.isNotEmpty(locale)) {
			qryStr.add("locale");
			qryStr.add(locale);
		}
		return qryStr.toArray(new String[qryStr.size()]);
	}

	public static List<String> getParamList(RevisionParams params) {
		String revLimit = params.getRevLimit();
		String root = params.getRoot();
		String locale = params.getLocale();
		
		List<String> list = new ArrayList<String>();
		if (StringUtils.isNotEmpty(revLimit)) {
			list.add("rev_limit");
			list.add(revLimit);
		}
		if (StringUtils.isNotEmpty(root)) {
			list.add("root");
			list.add(root);
		}
		if (StringUtils.isNotEmpty(locale)) {
			list.add("locale");
			list.add(locale);
		}
		return list;
	}

	public static List<String> getParamList(UploadParams params) {
		boolean overwrite = params.isOverwrite();
		String parentRev = params.getParentRev();
		String root = params.getRoot();
		String locale = params.getLocale();
		
		List<String> list = new ArrayList<String>();
		list.add("overwrite");
		list.add(String.valueOf(overwrite));

		if (StringUtils.isNotEmpty(parentRev)) {
			list.add("parent_rev");
			list.add(parentRev);
		}
		if (StringUtils.isNotEmpty(root)) {
			list.add("root");
			list.add(root);
		}
		if (StringUtils.isNotEmpty(locale)) {
			list.add("locale");
			list.add(locale);
		}
		return list;
	}
	public static String[] getQueryString(UploadParams params) {
		List<String> qryStr = getParamList(params);
		return qryStr.toArray(new String[qryStr.size()]);
	}
	
	public static String[] getQueryString(ThumbnailParams params) {
		String format = params.getFormat();
		String size = params.getSize();
		
		List<String> qryStr = new ArrayList<String>();
		if (StringUtils.isNotEmpty(format)) {
			qryStr.add("format");
			qryStr.add(format);
		}
		
		if (StringUtils.isNotEmpty(size)) {
			qryStr.add("size");
			qryStr.add(size);
		}
		return qryStr.toArray(new String[qryStr.size()]);
	}
	
	public static List<String> getParamList(CreateParams params) {
		String path = params.getPath();
		String root = params.getRoot();
		String locale = params.getLocale();
		
		List<String> list = new ArrayList<String>();
		list.add("path");
		list.add(path);
		if (StringUtils.isNotEmpty(root)) {
			list.add("root");
			list.add(root);
		}
		if (StringUtils.isNotEmpty(locale)) {
			list.add("locale");
			list.add(locale);
		}
		return list;
	}
	public static String[] getQueryString(CreateParams params) {
		List<String> qryStr = getParamList(params);
		return qryStr.toArray(new String[qryStr.size()]);
	}
	
	public static List<String> getParamList(DeltaParams params) {
		String cursor = params.getCursor();
		String locale = params.getLocale();
		
		List<String> list = new ArrayList<String>();
		if (StringUtils.isNotEmpty(cursor)) {
			list.add("cursor");
			list.add(cursor);
		}
		
		if (StringUtils.isNotEmpty(locale)) {
			list.add("locale");
			list.add(locale);
		}
		return list;
	}
	public static String[] getQueryString(DeltaParams params) {
		List<String> qryStr = getParamList(params);
		return qryStr.toArray(new String[qryStr.size()]);
	}

	public static List<String> getParamList(SearchParams params) {
		String query = params.getQuery();
		int fileLimit = params.getFileLimit();
		boolean includeDeleted = params.isIncludeDeleted();
		String root = params.getRoot();
		String locale = params.getLocale();
		
		List<String> list = new ArrayList<String>();
		if (StringUtils.isNotEmpty(query)) {
			list.add("query");
			list.add(query);
		}
		
		if (fileLimit <= 0 || fileLimit > SearchParams.SEARCH_DEFAULT_LIMIT) {
			fileLimit = SearchParams.SEARCH_DEFAULT_LIMIT;
		}
		list.add("file_limit");
		list.add(String.valueOf(fileLimit));

		if (includeDeleted) {
			list.add("include_deleted");
			list.add(String.valueOf(includeDeleted));
		}
		if (StringUtils.isNotEmpty(root)) {
			list.add("root");
			list.add(root);
		}
		if (StringUtils.isNotEmpty(locale)) {
			list.add("locale");
			list.add(locale);
		}
		return list;
	}
	public static String[] getQueryString(SearchParams params) {
		List<String> qryStr = getParamList(params);
		return qryStr.toArray(new String[qryStr.size()]);
	}

	public static List<String> getParamList(LinkParams params) {
		boolean shortUrl = params.isShortUrl();
		String root = params.getRoot();
		String locale = params.getLocale();
		String password = params.getPassword();
		String expires = params.getExpires();
		
		List<String> list = new ArrayList<String>();
		list.add("short_url");
		list.add(String.valueOf(shortUrl));
		
		if (StringUtils.isNotEmpty(root)) {
			list.add("root");
			list.add(root);
		}
		if (StringUtils.isNotEmpty(locale)) {
			list.add("locale");
			list.add(locale);
		}
		if (StringUtils.isNotEmpty(password)) {
			list.add("password");
			list.add(password);
		}
		if (StringUtils.isNotEmpty(expires)) {
			list.add("expires");
			list.add(expires);
		}
		return list;
	}
	public static String[] getQueryString(LinkParams params) {
		List<String> qryStr = getParamList(params);
		return qryStr.toArray(new String[qryStr.size()]);
	}

	public static List<List<String>> getParamList(BulkParams params) {
		String[] paths = params.getPaths();
		String root = params.getRoot();
		String locale = params.getLocale();
		
		List<List<String>> list = new ArrayList<List<String>>();
		for (String path: paths) {
			List<String> fields = new ArrayList<String>();
			fields.add("path");
			fields.add(path);
			if (StringUtils.isNotEmpty(root)) {
				fields.add("root");
				fields.add(root);
			}
			if (StringUtils.isNotEmpty(locale)) {
				fields.add("locale");
				fields.add(locale);
			}
			list.add(fields);
		}
		return list;
	}
	
	public static List<List<String>> getParamList(MoveParams params) {
		String[] fromPaths = params.getPaths();
		String[] toPaths = params.getToPaths();
		String root = params.getRoot();
		String locale = params.getLocale();
		
		List<List<String>> list = new ArrayList<List<String>>();
		for (int i = 0, size = fromPaths.length; i < size; i++) {
			String fromPath = fromPaths[i];
			String toPath = toPaths[i];
			
			List<String> fields = new ArrayList<String>();
			fields.add("from_path");
			fields.add(fromPath);
			fields.add("to_path");
			fields.add(toPath);
			if (StringUtils.isNotEmpty(root)) {
				fields.add("root");
				fields.add(root);
			}
			if (StringUtils.isNotEmpty(locale)) {
				fields.add("locale");
				fields.add(locale);
			}
			list.add(fields);
		}
		return list;
	}
}
