package mybox.service;

import mybox.backend.filecruiser.ContentType;
import mybox.backend.filecruiser.Header;
import mybox.config.SystemProp;
import mybox.json.JsonConverter;
import mybox.rest.RestClient;
import mybox.rest.RestResponse;
import mybox.util.HttpUtil;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class AbstractBackendService {
	
	private static final Logger log = LoggerFactory.getLogger(AbstractBackendService.class);
	
	@Autowired
	protected SystemProp systemProp;
	
	@Autowired
	protected RestClient restClient;
	
	protected <T> T get(String url, Class<T> clazz) {
		return get(url, getAdminToken(), clazz, false);
	}
	
	protected <T> T get(String url, String token, Class<T> clazz, boolean hasJsonRoot) {
		String[] headers = getHeaders(token);
		log.debug("getUrl: {}", url);
		
		RestResponse<String> restResponse = restClient.get(url, headers);
		T entity = JsonConverter.fromJson(restResponse.getBody(), clazz, hasJsonRoot);
		return entity;
	}

	protected <T> T post(String url, T entity) {
		return post(url, getAdminToken(), entity, false);
	}
	
	protected <T> T post(String url, String token, T entity, boolean hasJsonRoot) {
		String[] headers = getHeaders(token);
		String body = entity != null ? JsonConverter.toJson(entity, true) : null;
		log.debug("postUrl: {}  body: {}", url, body);
		
		RestResponse<String> restResponse = restClient.post(url, body, headers);
		T rtEntity = null;
		String respBody = restResponse.getBody();
		if (StringUtils.isNotBlank(respBody)) {
			rtEntity = (T) JsonConverter.fromJson(restResponse.getBody(), entity.getClass(), hasJsonRoot);
			log.debug("postResp: {}", rtEntity);
		}
		return rtEntity;
	}

	protected <T> T put(String url, T entity) {
		return put(url, getAdminToken(), entity, false);
	}
	
	protected <T> T put(String url, String token, T entity, boolean hasJsonRoot) {
		String[] headers = getHeaders(token);
		String body = entity != null ? JsonConverter.toJson(entity, true) : null;
		log.debug("putUrl: {}  body: {}", url, body);
		
		RestResponse<String> restResponse = restClient.put(url, body, headers);
		T rtEntity = null;
		String respBody = restResponse.getBody();
		if (StringUtils.isNotBlank(respBody)) {
			rtEntity = (T) JsonConverter.fromJson(restResponse.getBody(), entity.getClass(), hasJsonRoot);
			log.debug("putResp: {}", rtEntity);
		}
		return rtEntity;
	}
	
	protected <T> T patch(String url, T entity) {
		return patch(url, getAdminToken(), entity, false);
	}
	
	protected <T> T patch(String url, String token, T entity, boolean hasJsonRoot) {
		String[] headers = getHeaders(token);
		String body = entity != null ? JsonConverter.toJson(entity, true) : null;
		log.debug("patchUrl: {}  body: {}", url, body);
		
		RestResponse<String> restResponse = restClient.patch(url, body, headers);
		T rtEntity = null;
		String respBody = restResponse.getBody();
		if (StringUtils.isNotBlank(respBody)) {
			rtEntity = (T) JsonConverter.fromJson(restResponse.getBody(), entity.getClass(), hasJsonRoot);
			log.debug("patchResp: {}", rtEntity);
		}
		return rtEntity;
	}

	protected void delete(String url) {
		delete(url, getAdminToken());
	}
	
	protected void delete(String url, String token) {
		String[] headers = getHeaders(token);
		log.debug("deleteUrl: {}", url);
		restClient.delete(url, headers);
	}

	protected String getAdminToken() {
		String token = systemProp.getAdminToken();
		return token;
	}

	protected String[] getHeaders(String token) {
		String[] headers = {Header.X_AUTH_TOKEN, HttpUtil.encodeUrl(token), Header.CONTENT_TYPE, ContentType.JSON};
		return headers;
	}
	
	protected String[] getHeaders4Upload(String token) {
		String[] headers = {Header.X_AUTH_TOKEN, HttpUtil.encodeUrl(token), Header.CONTENT_TYPE, ContentType.JSON, Header.TRRANSFER_ENCODING, "chunked"};
		return headers;
	}
	
	protected String getAdminUrl(String resource, String... qryStr) {
		String url = systemProp.getAdminUrl();
		String version = systemProp.getVersion();
		return getUrl(url, version, resource, qryStr);
	}
	
	protected String getUserUrl(String resource, String... qryStr) {
		String url = systemProp.getUserUrl();
		String version = systemProp.getVersion();
		return getUrl(url, version, resource, qryStr);
	}
	
	protected String getFileUrl(String resource, String... qryStr) {
		String url = systemProp.getFileUrl();
		return getUrl(url, resource, qryStr);
	}

	protected String getUrl(String url, String resource, String... qryStr) {
		StringBuilder buf = new StringBuilder();
		buf.append(url).append("/").append(resource);
		if (qryStr != null && qryStr.length > 0) {
			buf.append("?").append(HttpUtil.encodeQueryString(qryStr));
		}
		return buf.toString();
	}
	
	protected String getUrl(String url, String version, String resource, String... qryStr) {
		StringBuilder buf = new StringBuilder();
		buf.append(url).append("/").append(version).append("/").append(resource);
		if (qryStr != null && qryStr.length > 0) {
			buf.append("?").append(HttpUtil.encodeQueryString(qryStr));
		}
		return buf.toString();
	}
	
	protected String buildPath(String... paths) {
		StringBuilder buf = new StringBuilder();
		for (int i = 0, len = paths.length; i < len; i++) {
			String path = paths[i];
			if (StringUtils.isEmpty(path)) {
				continue;
			}
			if (path.equals("/")) {
				continue;
			}
			if (buf.length() > 0) {
				char c = buf.charAt(buf.length() - 1);
				if (c != '/') {
					if (path.charAt(0) != '/') {
						buf.append("/");
					}
				} else {
					if (path.charAt(0) == '/') {
						path = path.substring(1);
					}
				}
			}
			buf.append(path);
		}
		return buf.toString();
	}
}
