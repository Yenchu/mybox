package mybox.task;

import java.util.List;
import java.util.concurrent.Future;

import mybox.dropbox.model.MetadataEntry;
import mybox.service.RestService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;


@Component
public class HttpPostWorker {

	@Autowired
	private RestService restService;
	
	@Async
	public Future<MetadataEntry> work(String url, List<String> fields, String... headers) {
		MetadataEntry entry = restService.post(MetadataEntry.class, url, fields, headers);
		return new AsyncResult<MetadataEntry>(entry);
	}
}
