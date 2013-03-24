package mybox.service;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import mybox.rest.JsonConverter;
import mybox.rest.RestClient;
import mybox.rest.RestClientFactory;
import mybox.rest.RestResponse;
import mybox.rest.RestResponseHandler;

import org.springframework.stereotype.Service;

import com.google.gson.reflect.TypeToken;

@Service
public class RestServiceImpl implements RestService {
	
	protected RestClient restClient;

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
	
	public <T> T post(Class<T> clazz, String url, String requestBody, String... headers) {
		RestResponse<String> restResponse = post(url, requestBody, headers);
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
	
	public <T> T put(Class<T> clazz, String url, String requestBody, String... headers) {
		RestResponse<String> restResponse = put(url, requestBody, headers);
		T entity = JsonConverter.fromJson(restResponse.getBody(), clazz);
		return entity;
	}
	
	public <T> T put(Class<T> clazz, String url, InputStream content, long contentLength, String... headers) {
		RestResponse<String> restResponse = put(url, content, contentLength, headers);
		T entity = JsonConverter.fromJson(restResponse.getBody(), clazz);
		return entity;
	}

	public <T> List<T> list(Class<T> clazz, String url, String... headers) {
		RestResponse<String> restResponse = getRestClient().post(url, headers);
		Type type = new TypeToken<List<T>>(){}.getType();
		List<T> entities = convertResponse(restResponse, type);
		return entities;
	}
	
	public <T> T get(RestResponseHandler<T, String> handler, String url, String... headers) {
		RestResponse<String> restResponse = getRestClient().get(url, headers);
		T entity = handler.handle(restResponse);
		return entity;
	}
	
	public <T> T post(RestResponseHandler<T, String> handler, String url, List<String> formParams, String... headers) {
		RestResponse<String> restResponse = getRestClient().post(url, formParams, headers);
		T entity = handler.handle(restResponse);
		return entity;
	}
	
	public <T> T put(RestResponseHandler<T, String> handler, String url, InputStream content, long contentLength, String... headers) {
		RestResponse<String> restResponse = getRestClient().put(url, content, contentLength, headers);
		T entity = handler.handle(restResponse);
		return entity;
	}
	
	public RestResponse<String> get(String url, String... headers) {
		RestResponse<String> restResponse = getRestClient().get(url, headers);
		RestResponseHandler.checkResponse(restResponse);
		return restResponse;
	}
	
	public RestResponse<String> get(String url, Map<String, String> queryStr, String... headers) {
		RestResponse<String> restResponse = getRestClient().get(url, queryStr, headers);
		RestResponseHandler.checkResponse(restResponse);
		return restResponse;
	}
	
	public RestResponse<InputStream> getStream(String url, String... headers) {
		RestResponse<InputStream> restResponse = getRestClient().getStream(url, headers);
		RestResponseHandler.checkResponse(restResponse);
		return restResponse;
	}
	
	public RestResponse<String> post(String url, String... headers) {
		RestResponse<String> restResponse = getRestClient().post(url, headers);
		RestResponseHandler.checkResponse(restResponse);
		return restResponse;
	}
	
	public RestResponse<String> post(String url, String requestBody, String... headers) {
		RestResponse<String> restResponse = getRestClient().post(url, requestBody, headers);
		RestResponseHandler.checkResponse(restResponse);
		return restResponse;
	}
	
	public RestResponse<String> post(String url, List<String> formParams, String... headers) {
		RestResponse<String> restResponse = getRestClient().post(url, formParams, headers);
		RestResponseHandler.checkResponse(restResponse);
		return restResponse;
	}
	
	public RestResponse<String> post(String url, InputStream content, long contentLength, String... headers) {
		RestResponse<String> restResponse = getRestClient().post(url, content, contentLength, headers);
		RestResponseHandler.checkResponse(restResponse);
		return restResponse;
	}
	
	public RestResponse<String> put(String url, String requestBody, String... headers) {
		RestResponse<String> restResponse = getRestClient().put(url, requestBody, headers);
		RestResponseHandler.checkResponse(restResponse);
		return restResponse;
	}
	
	public RestResponse<String> put(String url, InputStream content, long contentLength, String... headers) {
		RestResponse<String> restResponse = getRestClient().put(url, content, contentLength, headers);
		RestResponseHandler.checkResponse(restResponse);
		return restResponse;
	}
	
	protected <T> T convertResponse(RestResponse<String> restResponse, Type type) {
		RestResponseHandler.checkResponse(restResponse);
		T entity = JsonConverter.fromJson(restResponse.getBody(), type);
		return entity;
	}
	
	protected RestClient getRestClient() {
		if (restClient == null) {
			restClient = RestClientFactory.getRestClient();
		}
		return restClient;
	}
}
