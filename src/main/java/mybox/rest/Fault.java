package mybox.rest;

public class Fault {
	
	protected int code;
	
	protected String type;
	
	protected String message;
	
	protected String details;
	
	protected String error; //* just for Dropbox error response
	
	public Fault() {
	}
	
	public Fault(int code) {
		this.code = code;
	}
	
	public Fault(int code, String type) {
		this.code = code;
		this.type = type;
	}

	public Fault(int code, String type, String message) {
		this.code = code;
		this.type = type;
		this.message = message;
	}
	
	public static Fault internalServerError() {
		return new Fault(500, "InternalServerError");
	}
	
	public static Fault internalServerError(String message) {
		return new Fault(500, "InternalServerError", message);
	}
	
	public static Fault serviceUnavailable() {
		return new Fault(503, "ServiceUnavailable");
	}
	
	public static Fault badRequest() {
		return new Fault(400, "BadRequest");
	}
	
	public static Fault badRequest(String message) {
		return new Fault(400, "BadRequest", message);
	}
	
	public static Fault operationFailed() {
		return new Fault(400, "OperationFailed");
	}
	
	public static Fault unauthorized() {
		return new Fault(401, "Unauthorized");
	}

	public static Fault sessionNotFound() {
		return new Fault(401, "SessionNotFound");
	}
	
	public static Fault userDisabled() {
		return new Fault(403, "UserDisabled");
	}
	
	public static Fault formatError() {
		return new Fault(500, "FormatError");
	}
	
	public static Fault formatError(String message) {
		return new Fault(500, "FormatError", message);
	}
	
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("code=").append(code);
		buf.append(", type=").append(type);
		buf.append(", message=").append(message);
		buf.append(", details=").append(details);
		return buf.toString();
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMessage() {
		if (message == null) {
			return error;
		}
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getError() {
		if (error == null) {
			return message;
		}
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
}
