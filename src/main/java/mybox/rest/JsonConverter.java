package mybox.rest;

import java.lang.reflect.Type;
import java.util.Map;

import mybox.json.Json;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JsonConverter {

	private static final Logger log = LoggerFactory.getLogger(JsonConverter.class);

	public static <T> T fromJson(String json, Class<T> clazz) {
		return fromJson(json, clazz, false);
	}
	
	public static <T> T fromJson(String json, Class<T> clazz, boolean withRoot) {
		T entity = null;
		try {
			entity = Json.fromJson(json, clazz, withRoot);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new FaultException(Fault.formatError(e.getMessage()));
		}
		return entity;
	}

	public static <T> T fromJson(String json, Class<T> clazz, String rootName) {
		T entity = null;
		try {
			entity = Json.fromJson(json, clazz, rootName);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new FaultException(Fault.formatError(e.getMessage()));
		}
		return entity;
	}

	public static <T> T fromJson(String json, Type type) {
		T entity = null;
		try {
			entity = Json.fromJson(json, type);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new FaultException(Fault.formatError(e.getMessage()));
		}
		return entity;
	}

	public static Map<String, String> fromJson(String json) {
		Map<String, String> entity = null;
		try {
			entity = Json.fromJson(json);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new FaultException(Fault.formatError(e.getMessage()));
		}
		return entity;
	}
}
