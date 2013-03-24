package mybox.rest;

import java.util.LinkedHashMap;
import java.util.Map;

public class RestResponse<T> {

	private Map<String, String> headers;
	
	private int statusCode;
	
	private T body;
	
	public RestResponse() {
	}
	
	public RestResponse(int statusCode, T entity) {
		super();
		this.statusCode = statusCode;
		this.body = entity;
	}

	public String toString() {
		StringBuilder buf = new StringBuilder();
		if (headers != null && headers.size() > 0) {
			for (Map.Entry<String, String> entry: headers.entrySet()) {
				buf.append("\n").append(entry.getKey()).append("=").append(entry.getValue());
			}
		}
		buf.append("\nstatusCode=").append(statusCode);
		buf.append("\nbody=").append(body.toString());
		return buf.toString();
	}
	
	public void addHeader(String name, String value) {
		if (headers == null) {
			headers = new LinkedHashMap<String, String>();
		}
		headers.put(name, value);
	}

	public String getHeader(String name) {
		String value = null;
		if (headers != null) {
			value = headers.get(name);
		}
		return value;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}
	
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}
	
	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public T getBody() {
		return body;
	}

	public void setBody(T body) {
		this.body = body;
	}
}
