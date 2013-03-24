package mybox.web.to;

public class Notice {
	
	// refer to pnotify NotifyType = {NOTICE:'notice', INFO:'info', SUCCESS:'success', ERROR:'error'}
	public static final String INFO = "info";
	
	public static final String SUCCESS = "success";
	
	public static final String ERROR = "error";
	
	private String type;
	
	private String message;
	
	public Notice() {
	}
	
	public Notice(String type) {
		this.type = type;
	}

	public Notice(String type, String message) {
		this.type = type;
		this.message = message;
	}
	
	public static Notice info() {
		return new Notice(INFO, "");
	}
	
	public static Notice info(String message) {
		return new Notice(INFO, message);
	}
	
	public static Notice error() {
		return new Notice(ERROR, "");
	}
	
	public static Notice error(String message) {
		return new Notice(ERROR, message);
	}
	
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("type=").append(type);
		buf.append(", message=").append(message);
		return buf.toString();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
