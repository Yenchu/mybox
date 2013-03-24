package mybox.mondo;

public enum QueryString {

	LIMIT_SIZE("limit_size"), OFFSET("offset"), PATH("path"), LOCALE("locale");
	
	private final String value;
	
	private QueryString(String value) {
		this.value = value;
	}
	
	public String value() {
		return value;
	}
}
