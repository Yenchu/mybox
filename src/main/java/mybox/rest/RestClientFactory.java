package mybox.rest;

public class RestClientFactory {

	public static RestClient getRestClient() {
		RestClient restClient = new RestClientImpl();
		RestConnection restConnection = new RestConnectionImpl();
		restClient.setRestConnection(restConnection);
		return restClient;
	}
	
	public static RestClient getRestClient(RestResponseValidator restResponseValidator) {
		RestClient restClient = getRestClient();
		restClient.setRestResponseValidator(restResponseValidator);
		return restClient;
	}
}
