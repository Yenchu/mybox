package mybox.web.vo;

public class Notice {
	
	public static final int INFO = 1;
	
	public static final int SUCCESS = 2;
	
	public static final int WARNING = 3;
	
	public static final int ERROR = 4;
	
	private int type;
	
	private String message;
	
	public Notice() {
	}
	
	public Notice(int type) {
		this.type = type;
	}

	public Notice(int type, String message) {
		this.type = type;
		this.message = message;
	}
	
	public static Notice info(String message) {
		return new Notice(INFO, message);
	}
	
	public static Notice success(String message) {
		return new Notice(SUCCESS, message);
	}
	
	public static Notice warning(String message) {
		return new Notice(WARNING, message);
	}
	
	public static Notice error(String message) {
		return new Notice(ERROR, message);
	}
	
	public boolean isInfo() {
		if (type == INFO) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isSuccess() {
		if (type == SUCCESS) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isWarning() {
		if (type == WARNING) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isError() {
		if (type == ERROR) {
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("type=").append(type);
		buf.append(", message=").append(message);
		return buf.toString();
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
