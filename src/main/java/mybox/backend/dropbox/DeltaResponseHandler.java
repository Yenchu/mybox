package mybox.backend.dropbox;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mybox.model.MetadataEntry;
import mybox.model.dropbox.DeltaEntry;
import mybox.model.dropbox.DeltaPage;
import mybox.rest.RestResponse;
import mybox.rest.RestResponseConverter;
import mybox.rest.RestResponseValidator;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class DeltaResponseHandler extends RestResponseConverter<String, DeltaPage<MetadataEntry>> {

	private RestResponseValidator restResponseValidator = new RestResponseValidator();
	
	@Override
	public DeltaPage<MetadataEntry> convert(RestResponse<String> restResponse) {
		restResponseValidator.validateResponse(restResponse);

		String content = restResponse.getBody();
		JsonParser parser = new JsonParser();
		JsonElement je = parser.parse(content);
		JsonObject jo = je.getAsJsonObject();

		DeltaPage<MetadataEntry> deltaPage = new DeltaPage<MetadataEntry>();
		JsonElement resetElem = jo.get("reset");
		if (resetElem != null) {
			deltaPage.setReset(resetElem.getAsBoolean());
		}
		JsonElement hasMoreElem = jo.get("has_more");
		if (hasMoreElem != null) {
			deltaPage.setHasMore(hasMoreElem.getAsBoolean());
		}
		JsonElement cursorElem = jo.get("cursor");
		if (cursorElem != null) {
			deltaPage.setCursor(cursorElem.getAsString());
		}

		List<DeltaEntry<MetadataEntry>> entries = new ArrayList<DeltaEntry<MetadataEntry>>();
		deltaPage.setEntries(entries);

		JsonArray entriesArr = jo.getAsJsonArray("entries");
		Gson gson = new Gson();
		Iterator<JsonElement> ite = entriesArr.iterator();
		while (ite.hasNext()) {
			JsonElement elem = ite.next();
			JsonArray arr = elem.getAsJsonArray();

			DeltaEntry<MetadataEntry> deltaEntry = new DeltaEntry<MetadataEntry>();
			JsonElement lcPathElem = arr.get(0);
			deltaEntry.setLcPath(lcPathElem.getAsString());

			JsonElement metadataPathElem = arr.get(1);
			MetadataEntry metadata = gson.fromJson(metadataPathElem, MetadataEntry.class);
			deltaEntry.setMetadata(metadata);
			entries.add(deltaEntry);
		}
		return deltaPage;
	}
}
