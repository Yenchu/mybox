package mybox.json;

import java.lang.reflect.Type;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mybox.exception.Error;
import mybox.exception.ErrorException;

public class JsonConverter {

	private static final Logger log = LoggerFactory.getLogger(JsonConverter.class);
	
	private static final Json JSON = new Json();
	
	public static <T> String toJson(T entity) {
		return toJson(entity, false);
	}

	public static <T> String toJson(T entity, boolean jsonHasRoot) {
		String json;
		try {
			json = JSON.toJson(entity, jsonHasRoot);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ErrorException(Error.formatError(e.getMessage()));
		}
		return json;
	}

	public static <T> String toJson(T entity, String rootName) {
		String json;
		try {
			json = JSON.toJson(entity, rootName);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ErrorException(Error.formatError(e.getMessage()));
		}
		return json;
	}
	
	public static <T> String toJson(T entity, Type type) {
		String json;
		try {
			json = JSON.toJson(entity, type);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ErrorException(Error.formatError(e.getMessage()));
		}
		return json;
	}

	public static <T> T fromJson(String json, Class<T> clazz) {
		return fromJson(json, clazz, false);
	}
	
	public static <T> T fromJson(String json, Class<T> clazz, boolean jsonHasRoot) {
		if (json == null || json.equals("")) {
			return null;
		}
		
		T entity = null;
		try {
			entity = JSON.fromJson(json, clazz, jsonHasRoot);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ErrorException(Error.formatError(e.getMessage()));
		}
		return entity;
	}

	public static <T> T fromJson(String json, Class<T> clazz, String rootName) {
		if (json == null || json.equals("")) {
			return null;
		}
		
		T entity = null;
		try {
			entity = JSON.fromJson(json, clazz, rootName);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ErrorException(Error.formatError(e.getMessage()));
		}
		return entity;
	}

	public static <T> T fromJson(String json, Type type) {
		if (json == null || json.equals("")) {
			return null;
		}
		
		T entity = null;
		try {
			entity = JSON.fromJson(json, type);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ErrorException(Error.formatError(e.getMessage()));
		}
		return entity;
	}

	public static Map<String, String> fromJson(String json) {
		if (json == null || json.equals("")) {
			return null;
		}
		
		Map<String, String> entity = null;
		try {
			entity = JSON.fromJson(json);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ErrorException(Error.formatError(e.getMessage()));
		}
		return entity;
	}
}
