package mybox.to;

import java.util.ArrayList;
import java.util.List;

public class DeltaParams extends Params {

	private String cursor;
	
	public List<String> getParamList() {
		List<String> params = new ArrayList<String>();

		if (cursor != null && !"".equals(cursor)) {
			params.add("cursor");
			params.add(cursor);
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
	
	public String getCursor() {
		return cursor;
	}

	public void setCursor(String cursor) {
		this.cursor = cursor;
	}
}
