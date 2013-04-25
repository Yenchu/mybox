package mybox.task;

import java.util.concurrent.Future;

import mybox.backend.filecruiser.FileCruiserRestResponseValidator;
import mybox.model.MetadataEntry;
import mybox.rest.RestClient;
import mybox.rest.RestClientFactory;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

@Component
public class FileCruiserHttpPostWorker {

	protected RestClient restClient;
	
	protected FileCruiserRestResponseValidator restResponseValidator;
	
	public FileCruiserHttpPostWorker() {
		restResponseValidator = new FileCruiserRestResponseValidator();
		restClient = RestClientFactory.getRestClient(restResponseValidator);
	}
	
	@Async
	public Future<MetadataEntry> work(String url, String... headers) {
		MetadataEntry entry = restClient.post(MetadataEntry.class, url, "", headers);
		return new AsyncResult<MetadataEntry>(entry);
	}
}
