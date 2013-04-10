package mybox.to;

import java.util.ArrayList;
import java.util.List;

import mybox.model.User;

public class ThumbnailParams extends PathParams {

	private String format = "jpeg"; //jpeg (default) or png
	
	private String size = "xs"; //default: s
	
	public ThumbnailParams() {
	}
	
	public ThumbnailParams(String root, String path) {
		super(root, path);
	}
	
	public ThumbnailParams(User user, String root, String path) {
		super(user, root, path);
	}
	
	public List<String> getParamList() {
		List<String> params = new ArrayList<String>();
		if (format != null && !"".equals(format)) {
			params.add("format");
			params.add(format);
		}
		if (size != null && !"".equals(size)) {
			params.add("size");
			params.add(size);
		}
		return params;
	}

	public String[] getParamArray() {
		List<String> params = getParamList();
		return params.toArray(new String[params.size()]);
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}
}
