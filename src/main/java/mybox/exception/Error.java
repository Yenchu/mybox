package mybox.exception;

public class Error {
	
	//{"error": {"message": "The resource could not be found.", "code": 404, "title": "Not Found"}}
	protected int code;
	
	protected String title;
	
	protected String message;
	
	public Error() {
	}
	
	public Error(int code) {
		this.code = code;
	}
	
	public Error(int code, String title) {
		this.code = code;
		this.title = title;
	}

	public Error(int code, String title, String message) {
		this.code = code;
		this.title = title;
		this.message = message;
	}
	
	public static Error operationFailed() {
		return new Error(400, "OperationFailed");
	}
	
	public static Error operationFailed(String message) {
		return new Error(400, "OperationFailed", message);
	}
	
	public static Error badRequest() {
		return new Error(400, "BadRequest");
	}
	
	public static Error badRequest(String message) {
		return new Error(400, "BadRequest", message);
	}
	
	public static Error unauthorized() {
		return new Error(401, "Unauthorized");
	}
	
	public static Error forbidden() {
		return new Error(403, "Forbidden");
	}
	
	public static Error formatError() {
		return new Error(500, "FormatError");
	}
	
	public static Error formatError(String message) {
		return new Error(500, "FormatError", message);
	}
	
	public static Error internalServerError() {
		return new Error(500, "InternalServerError");
	}
	
	public static Error internalServerError(String message) {
		return new Error(500, "InternalServerError", message);
	}
	
	public static Error serviceUnavailable() {
		return new Error(503, "ServiceUnavailable");
	}
	
	public static Error serviceUnavailable(String message) {
		return new Error(503, "ServiceUnavailable", message);
	}
	
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("code=").append(code);
		buf.append(", title=").append(title);
		buf.append(", message=").append(message);
		return buf.toString();
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
