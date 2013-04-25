package mybox.backend;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mybox.model.MetadataEntry;
import mybox.rest.RestResponse;
import mybox.rest.RestResponseConverter;
import mybox.rest.RestResponseValidator;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class MetadataListResponseHandler extends RestResponseConverter<String, List<MetadataEntry>> {
	
	private RestResponseValidator restResponseValidator;
	
	public MetadataListResponseHandler() {
		this(new RestResponseValidator());
	}
	
	public MetadataListResponseHandler(RestResponseValidator restResponseValidator) {
		this.restResponseValidator = restResponseValidator;
	}

	@Override
	public List<MetadataEntry> convert(RestResponse<String> restResponse) {
		restResponseValidator.validateResponse(restResponse);

		String content = restResponse.getBody();
		JsonParser parser = new JsonParser();
		JsonElement je = parser.parse(content);
		JsonArray entriesArr = je.getAsJsonArray();

		Gson gson = new Gson();
		List<MetadataEntry> entries = new ArrayList<MetadataEntry>();
		Iterator<JsonElement> ite = entriesArr.iterator();
		while (ite.hasNext()) {
			JsonElement elem = ite.next();
			MetadataEntry entry = gson.fromJson(elem, MetadataEntry.class);
			entries.add(entry);
		}
		return entries;
	}
}
