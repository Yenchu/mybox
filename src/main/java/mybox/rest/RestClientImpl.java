package mybox.rest;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import mybox.json.JsonConverter;

public class RestClientImpl implements RestClient {
	
	protected RestConnection restConnection;
	
	protected RestResponseValidator restResponseValidator;

	public RestResponse<String> get(String url, String... headers) {
		RestResponse<String> restResponse = getRestConnection().get(url, headers);
		getRestResponseValidator().validateResponse(restResponse);
		return restResponse;
	}
	
	public RestResponse<String> get(String url, Map<String, String> queryStr, String... headers) {
		RestResponse<String> restResponse = getRestConnection().get(url, queryStr, headers);
		getRestResponseValidator().validateResponse(restResponse);
		return restResponse;
	}
	
	public RestResponse<InputStream> getStream(String url, String... headers) {
		RestResponse<InputStream> restResponse = getRestConnection().getStream(url, headers);
		getRestResponseValidator().validateResponse(restResponse);
		return restResponse;
	}
	
	public RestResponse<String> post(String url, String... headers) {
		RestResponse<String> restResponse = getRestConnection().post(url, "", headers);
		getRestResponseValidator().validateResponse(restResponse);
		return restResponse;
	}
	
	public RestResponse<String> post(String url, String body, String... headers) {
		RestResponse<String> restResponse = getRestConnection().post(url, body, headers);
		getRestResponseValidator().validateResponse(restResponse);
		return restResponse;
	}
	
	public RestResponse<String> post(String url, List<String> formParams, String... headers) {
		RestResponse<String> restResponse = getRestConnection().post(url, formParams, headers);
		getRestResponseValidator().validateResponse(restResponse);
		return restResponse;
	}
	
	public RestResponse<String> post(String url, InputStream content, long contentLength, String... headers) {
		RestResponse<String> restResponse = getRestConnection().post(url, content, contentLength, headers);
		getRestResponseValidator().validateResponse(restResponse);
		return restResponse;
	}
	
	public RestResponse<String> put(String url, String body, String... headers) {
		RestResponse<String> restResponse = getRestConnection().put(url, body, headers);
		getRestResponseValidator().validateResponse(restResponse);
		return restResponse;
	}
	
	public RestResponse<String> put(String url, InputStream content, long contentLength, String... headers) {
		RestResponse<String> restResponse = getRestConnection().put(url, content, contentLength, headers);
		getRestResponseValidator().validateResponse(restResponse);
		return restResponse;
	}
	
	public RestResponse<String> patch(String url, String body, String... headers) {
		RestResponse<String> restResponse = getRestConnection().patch(url, body, headers);
		getRestResponseValidator().validateResponse(restResponse);
		return restResponse;
	}
	
	public RestResponse<String> delete(String url, String... headers) {
		RestResponse<String> restResponse = getRestConnection().delete(url, headers);
		getRestResponseValidator().validateResponse(restResponse);
		return restResponse;
	}

	public <T> T get(RestResponseConverter<String, T> converter, String url, String... headers) {
		RestResponse<String> restResponse = getRestConnection().get(url, headers);
		T entity = converter.convert(restResponse);
		return entity;
	}
	
	public <T> T post(RestResponseConverter<String, T> converter, String url, List<String> formParams, String... headers) {
		RestResponse<String> restResponse = getRestConnection().post(url, formParams, headers);
		T entity = converter.convert(restResponse);
		return entity;
	}
	
	public <T> T put(RestResponseConverter<String, T> converter, String url, InputStream content, long contentLength, String... headers) {
		RestResponse<String> restResponse = getRestConnection().put(url, content, contentLength, headers);
		T entity = converter.convert(restResponse);
		return entity;
	}
	
	public <T> T get(Class<T> clazz, String url, String... headers) {
		RestResponse<String> restResponse = get(url, headers);
		T entity = JsonConverter.fromJson(restResponse.getBody(), clazz);
		return entity;
	}
	
	public <T> T get(Class<T> clazz, String url, Map<String, String> queryStr, String... headers) {
		RestResponse<String> restResponse = get(url, queryStr, headers);
		T entity = JsonConverter.fromJson(restResponse.getBody(), clazz);
		return entity;
	}
	
	public <T> T post(Class<T> clazz, String url, String... headers) {
		RestResponse<String> restResponse = post(url, headers);
		T entity = JsonConverter.fromJson(restResponse.getBody(), clazz);
		return entity;
	}
	
	public <T> T post(Class<T> clazz, String url, String body, String... headers) {
		RestResponse<String> restResponse = post(url, body, headers);
		T entity = JsonConverter.fromJson(restResponse.getBody(), clazz);
		return entity;
	}
	
	public <T> T post(Class<T> clazz, String url, List<String> formParams, String... headers) {
		RestResponse<String> restResponse = post(url, formParams, headers);
		T entity = JsonConverter.fromJson(restResponse.getBody(), clazz);
		return entity;
	}
	
	public <T> T post(Class<T> clazz, String url, InputStream content, long contentLength, String... headers) {
		RestResponse<String> restResponse = post(url, content, contentLength, headers);
		T entity = JsonConverter.fromJson(restResponse.getBody(), clazz);
		return entity;
	}
	
	public <T> T put(Class<T> clazz, String url, String body, String... headers) {
		RestResponse<String> restResponse = put(url, body, headers);
		T entity = JsonConverter.fromJson(restResponse.getBody(), clazz);
		return entity;
	}
	
	public <T> T put(Class<T> clazz, String url, InputStream content, long contentLength, String... headers) {
		RestResponse<String> restResponse = put(url, content, contentLength, headers);
		T entity = JsonConverter.fromJson(restResponse.getBody(), clazz);
		return entity;
	}
	
	public <T> T patch(Class<T> clazz, String url, String body, String... headers) {
		RestResponse<String> restResponse = patch(url, body, headers);
		T entity = JsonConverter.fromJson(restResponse.getBody(), clazz);
		return entity;
	}
	
	protected RestConnection getRestConnection() {
		if (restConnection == null) {
			restConnection = new RestConnectionImpl();
		}
		return restConnection;
	}
	
	public void setRestConnection(RestConnection restConnection) {
		this.restConnection = restConnection;
	}
	
	protected RestResponseValidator getRestResponseValidator() {
		if (restResponseValidator == null) {
			restResponseValidator = new RestResponseValidator();
		}
		return restResponseValidator;
	}

	public void setRestResponseValidator(RestResponseValidator restResponseValidator) {
		this.restResponseValidator = restResponseValidator;
	}
}
