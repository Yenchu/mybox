package mybox.task;

import java.util.List;
import java.util.concurrent.Future;

import mybox.model.MetadataEntry;
import mybox.rest.RestClient;
import mybox.rest.RestClientFactory;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

@Component
public class HttpPostWorker {

	private RestClient restClient= RestClientFactory.getRestClient();
	
	@Async
	public Future<MetadataEntry> work(String url, List<String> fields, String... headers) {
		MetadataEntry entry = restClient.post(MetadataEntry.class, url, fields, headers);
		return new AsyncResult<MetadataEntry>(entry);
	}
}
