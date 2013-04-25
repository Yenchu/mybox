package mybox.rest;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface RestConnection {
	
	public RestResponse<String> get(String url, String... headers);
	
	public RestResponse<String> get(String url, Map<String, String> queryStr, String... headers);
	
	public RestResponse<InputStream> getStream(String url, String... headers);
	
	public RestResponse<InputStream> getStream(String url, Map<String, String> queryStr, String... headers);

	public RestResponse<String> delete(String url, String... headers);
	
	public RestResponse<String> delete(String url, Map<String, String> queryStr, String... headers);
	
	public RestResponse<String> post(String url, String body, String... headers);
	
	public RestResponse<String> post(String uri, List<String> formParams, String... headers);
	
	public RestResponse<String> post(String url, InputStream content, long contentLength, String... headers);
	
	public RestResponse<String> put(String url, String body, String... headers);
	
	public RestResponse<String> put(String uri, List<String> formParams, String... headers);

	public RestResponse<String> put(String url, InputStream content, long contentLength, String... headers);
	
	public RestResponse<String> patch(String url, String body, String... headers);
	
	public RestResponse<String> patch(String uri, List<String> formParams, String... headers);

	public RestResponse<String> patch(String url, InputStream content, long contentLength, String... headers);
	
	public boolean isHttps();
	
	public void setHttps(boolean isHttps);
	
	public int getHttpsPort();

	public void setHttpsPort(int httpsPort);
	
}