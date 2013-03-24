package mybox.json;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class Json {
	
	protected static Gson gson = null;

	protected static Gson getGson() {
		if (gson == null) {
			GsonBuilder builder = new GsonBuilder();
			builder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			gson = builder.create();
		}
		return gson;
	}
	
	public static String toJson(Object src) {
		return toJson(src, false);
	}

	/**
	 * Convert Object to Json string. 
	 * Using Object class name (lower-case) as root node's name if withRoot=true.
	 * 
	 * @param src
	 * @return
	 */
	public static String toJson(Object src, boolean withRoot) {
		if (withRoot) {
			return toJson(src, src.getClass().getSimpleName().toLowerCase());
		} else {
			return getGson().toJson(src);
		}
	}

	public static String toJson(Object src, String rootName) {
		JsonElement je = getGson().toJsonTree(src);
		JsonObject jo = new JsonObject();
		jo.add(rootName, je);
		return jo.toString();
	}
	
	public static String toJson(Object src, Type type) {
		return getGson().toJson(src, type);
	}

	public static <T> T fromJson(String json, Class<T> classOfT) {
		return fromJson(json, classOfT, true);
	}

	public static <T> T fromJson(String json, Class<T> clazz,
			boolean withRoot) {
		if (withRoot) {
			return fromJson(json, clazz, clazz.getSimpleName().toLowerCase());
		} else {
			return getGson().fromJson(json, clazz);
		}
	}
	
	public static <T> T fromJson(String json, Class<T> clazz,
			String rootName) {
		JsonParser parser = new JsonParser();
		JsonElement je = parser.parse(json);
		JsonObject jo = je.getAsJsonObject();
		jo = jo.getAsJsonObject(rootName);
		return getGson().fromJson(jo, clazz);
	}
	
	public static <T> T fromJson(String json, Type type) {
		return getGson().fromJson(json, type);
	}
	
	public static Map<String, String> fromJson(String json) {
		Type type = new TypeToken<LinkedHashMap<String, String>>(){}.getType();
		Map<String, String> map = getGson().fromJson(json, type);
		return map;
	}
}
