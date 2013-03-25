package mybox.json;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class Json {
	
	protected Gson gson = null;
	
	public Json() {
		this("yyyy-MM-dd'T'HH:mm:ss");
	}
	
	public Json(String dateFormate) {
		GsonBuilder builder = new GsonBuilder();
		builder.setDateFormat(dateFormate);
		gson = builder.create();
	}

	protected Gson getGson() {
		return gson;
	}
	
	public String toJson(Object src) {
		return toJson(src, false);
	}

	public String toJson(Object src, boolean withRoot) {
		if (withRoot) {
			return toJson(src, StringUtils.uncapitalize(src.getClass().getSimpleName()));
		} else {
			return getGson().toJson(src);
		}
	}

	public String toJson(Object src, String rootName) {
		JsonElement je = getGson().toJsonTree(src);
		JsonObject jo = new JsonObject();
		jo.add(rootName, je);
		return jo.toString();
	}
	
	public String toJson(Object src, Type type) {
		return getGson().toJson(src, type);
	}

	public <T> T fromJson(String json, Class<T> classOfT) {
		return fromJson(json, classOfT, true);
	}

	public <T> T fromJson(String json, Class<T> clazz,
			boolean jsonHasRoot) {
		if (jsonHasRoot) {
			return fromJson(json, clazz, StringUtils.uncapitalize(clazz.getSimpleName().toLowerCase()));
		} else {
			return getGson().fromJson(json, clazz);
		}
	}
	
	public <T> T fromJson(String json, Class<T> clazz,
			String rootName) {
		JsonParser parser = new JsonParser();
		JsonElement je = parser.parse(json);
		JsonObject jo = je.getAsJsonObject();
		jo = jo.getAsJsonObject(rootName);
		return getGson().fromJson(jo, clazz);
	}
	
	public <T> T fromJson(String json, Type type) {
		return getGson().fromJson(json, type);
	}
	
	public Map<String, String> fromJson(String json) {
		Type type = new TypeToken<LinkedHashMap<String, String>>(){}.getType();
		Map<String, String> map = getGson().fromJson(json, type);
		return map;
	}
}
