package mybox.rest;

public class RestClientFactory {

	public static RestClient getRestClient() {
		return getRestClient(false);
	}
	
	public static RestClient getRestClient(boolean usingHttps) {
		return getRestClient(usingHttps, 443);
	}
	
	public static RestClient getRestClient(boolean usingHttps, int httpsPort) {
		RestClient restClient = new RestClientImpl(usingHttps, httpsPort);
		return restClient;
	}
}
