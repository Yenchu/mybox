package mybox.rest;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface RestClient {

	// wrap RestConnection methods with RestResponseValidator
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

	// provide custom RestResponseConverter
	public <T> T get(RestResponseConverter<String, T> converter, String url, String... headers);

	public <T> T post(RestResponseConverter<String, T> converter, String url, List<String> formParams, String... headers);
	
	public <T> T put(RestResponseConverter<String, T> converter, String url, InputStream content, long contentLength, String... headers);

	// provide converting RestResponse to object
	public <T> T get(Class<T> clazz, String url, String... headers);
	
	public <T> T get(Class<T> clazz, String url, Map<String, String> queryStr, String... headers);

	public <T> T post(Class<T> clazz, String url, String... headers);
	
	public <T> T post(Class<T> clazz, String url, String body, String... headers);
	
	public <T> T post(Class<T> clazz, String url, List<String> formParams, String... headers);
	
	public <T> T post(Class<T> clazz, String url, InputStream content, long contentLength, String... headers);

	public <T> T put(Class<T> clazz, String url, String body, String... headers);
	
	public <T> T put(Class<T> clazz, String url, InputStream content, long contentLength, String... headers);
	
	public <T> T patch(Class<T> clazz, String url, String body, String... headers);
	
	/*public <T> T get(String url, String[] headers, Class<T> clazz, boolean hasJsonRoot);
	
	public <T, E> E post(String url, String[] headers, T entity, Class<E> clazz, boolean hasJsonRoot);
	
	public <T, E> E put(String url, String[] headers, T entity, Class<E> clazz, boolean hasJsonRoot);
	
	public <T, E> E patch(String url, String[] headers, T entity, Class<E> clazz, boolean hasJsonRoot);*/
	
	public void setRestConnection(RestConnection restConnection);
	
	public void setRestResponseValidator(RestResponseValidator restResponseValidator);
	
}