package mybox.rest;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface RestClient {

	public RestResponse<String> get(String url, String... headers);
	
	public RestResponse<String> get(String url, Map<String, String> queryStr, String... headers);
	
	public RestResponse<InputStream> getStream(String url, String... headers);
	
	public RestResponse<String> post(String url, String... headers);
	
	public RestResponse<String> post(String url, String body, String... headers);
	
	public RestResponse<String> post(String url, List<String> formParams, String... headers);
	
	public RestResponse<String> post(String url, InputStream content, long contentLength, String... headers);
	
	public RestResponse<String> put(String url, String body, String... headers);
	
	public RestResponse<String> put(String url, InputStream content, long contentLength, String... headers);
	
	public RestResponse<String> patch(String url, String body, String... headers);

	public RestResponse<String> delete(String url, String... headers);

	public <T> T get(RestResponseConverter<String, T> converter, String url, String... headers);

	public <T> T post(RestResponseConverter<String, T> converter, String url, List<String> formParams, String... headers);
	
	public <T> T put(RestResponseConverter<String, T> converter, String url, InputStream content, long contentLength, String... headers);

	public <T> T get(Class<T> clazz, String url, String... headers);
	
	public <T> T get(Class<T> clazz, String url, Map<String, String> queryStr, String... headers);

	public <T> T post(Class<T> clazz, String url, String... headers);
	
	public <T> T post(Class<T> clazz, String url, String body, String... headers);
	
	public <T> T post(Class<T> clazz, String url, List<String> formParams, String... headers);
	
	public <T> T post(Class<T> clazz, String url, InputStream content, long contentLength, String... headers);

	public <T> T put(Class<T> clazz, String url, String body, String... headers);
	
	public <T> T put(Class<T> clazz, String url, InputStream content, long contentLength, String... headers);
	
	public <T> T patch(Class<T> clazz, String url, String body, String... headers);
	
}