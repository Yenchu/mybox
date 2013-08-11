package mybox.type;

public enum ServiceType {

	// default service type is empty
	DISK("dk"), DROPBOX("db");
	
	private final String value;
	
	private ServiceType(String value) {
		this.value = value;
	}
	
	public String value() {
		return value;
	}
}