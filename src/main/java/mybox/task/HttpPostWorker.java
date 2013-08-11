package mybox.task;

import java.util.List;
import java.util.concurrent.Future;

import mybox.rest.RestClient;
import mybox.rest.RestClientFactory;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

@Component
public class HttpPostWorker {
	
	private RestClient restClient= RestClientFactory.getRestClient();
	
	@Async
	public <T> Future<T> work(Class<T> clazz, String url, List<String> formParams, String... headers) {
		T entry = restClient.post(clazz, url, formParams, headers);
		return new AsyncResult<T>(entry);
	}
}
