package mybox.service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import mybox.rest.RestResponse;
import mybox.rest.RestResponseHandler;


public interface RestService {

	public <T> T get(Class<T> clazz, String url, String... headers);
	
	public <T> T get(Class<T> clazz, String url, Map<String, String> queryStr, String... headers);

	public <T> T post(Class<T> clazz, String url, String... headers);
	
	public <T> T post(Class<T> clazz, String url, String requestBody, String... headers);
	
	public <T> T post(Class<T> clazz, String url, List<String> formParams, String... headers);
	
	public <T> T post(Class<T> clazz, String url, InputStream content, long contentLength, String... headers);

	public <T> T put(Class<T> clazz, String url, String requestBody, String... headers);
	
	public <T> T put(Class<T> clazz, String url, InputStream content, long contentLength, String... headers);
	
	public <T> List<T> list(Class<T> clazz, String url, String... headers);
	
	public <T> T get(RestResponseHandler<T, String> handler, String url, String... headers);

	public <T> T post(RestResponseHandler<T, String> handler, String url, List<String> formParams, String... headers);
	
	public <T> T put(RestResponseHandler<T, String> handler, String url, InputStream content, long contentLength, String... headers);

	public RestResponse<String> get(String url, String... headers);
	
	public RestResponse<String> get(String url, Map<String, String> queryStr, String... headers);
	
	public RestResponse<InputStream> getStream(String url, String... headers);
	
	public RestResponse<String> post(String url, String... headers);
	
	public RestResponse<String> post(String url, String requestBody, String... headers);
	
	public RestResponse<String> post(String url, List<String> formParams, String... headers);
	
	public RestResponse<String> post(String url, InputStream content, long contentLength, String... headers);
	
	public RestResponse<String> put(String url, String requestBody, String... headers);
	
	public RestResponse<String> put(String url, InputStream content, long contentLength, String... headers);
	
}