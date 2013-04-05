package mybox.backend.mondo;

public enum Header {

	AUTH_USER("Auth-user"), AUTH_PASSWD("Auth-passwd"), AUTH_TOKEN("Auth-token"), GROUP_ID("uuid")
	, CONTENT_LENGTH("Content-Length");
	
	private final String value;
	
	private Header(String value) {
		this.value = value;
	}
	
	public String value() {
		return value;
	}
}
