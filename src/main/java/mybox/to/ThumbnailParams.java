package mybox.to;

import mybox.model.User;

public class ThumbnailParams extends PathParams {

	private String format = "jpeg"; //jpeg (default) or png
	
	private String size = "xs"; //default: s
	
	public ThumbnailParams() {
	}
	
	public ThumbnailParams(User user, String path) {
		super(user, path);
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
