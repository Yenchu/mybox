package mybox.web;

public enum ServiceType {

	MONDO("mondo"), DROPBOX("dropbox"), DISK("disk");
	
	private final String value;
	
	private ServiceType(String value) {
		this.value = value;
	}
	
	public String value() {
		return value;
	}
}
