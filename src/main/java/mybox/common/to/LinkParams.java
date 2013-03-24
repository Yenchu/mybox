package mybox.common.to;

import java.util.ArrayList;
import java.util.List;

public class LinkParams extends PathParams {
	
	private boolean shortUrl = true; //default

	public LinkParams() {
	}
	
	public LinkParams(String root, String path) {
		super(root, path);
	}
	
	public LinkParams(User user, String root, String path) {
		super(user, root, path);
	}
	
	public List<String> getParamList() {
		List<String> params = new ArrayList<String>();
		if (!shortUrl) {
			params.add("short_url");
			params.add("false");
		}
		if (locale != null && !"".equals(locale)) {
			params.add("locale");
			params.add(locale);
		}
		return params;
	}

	public String[] getParamArray() {
		List<String> params = getParamList();
		return params.toArray(new String[params.size()]);
	}

	public boolean isShortUrl() {
		return shortUrl;
	}

	public void setShortUrl(boolean shortUrl) {
		this.shortUrl = shortUrl;
	}
}
