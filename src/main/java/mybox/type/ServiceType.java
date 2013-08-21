package mybox.type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum ServiceType {
	
	DROPBOX("db"), EXTENSION("ex"), ;

	private static final Logger log = LoggerFactory.getLogger(ServiceType.class);

	private final String value;
	
	private ServiceType(String value) {
		this.value = value;
	}
	
	public String value() {
		return value;
	}
	
	public ServiceType getServiceType(String value) {
		if (DROPBOX.value().equalsIgnoreCase(value)) {
			return DROPBOX;
		} else if (EXTENSION.value().equalsIgnoreCase(value)) {
			return EXTENSION;
		} else {
			log.error("Can not find service type of {}", value);;
			return null;
		}
	}
}