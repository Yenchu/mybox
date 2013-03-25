package mybox.exception;

public class ErrorException extends RuntimeException {

	private Error error;

	public ErrorException() {
	}
	
	public ErrorException(int code) {
		this.error = new Error(code);
	}
	
	public ErrorException(int code, String title) {
		this.error = new Error(code, title);
	}
	
	public ErrorException(int code, String title, String message) {
		this.error = new Error(code, title, message);
	}
	
	public ErrorException(Error error) {
		this.error = error;
	}

	@Override
	public String getMessage() {
		if (error != null) {
			return error.getMessage();
		} else {
			return super.getMessage();
		}
	}

	public Error getError() {
		return error;
	}
	
	public void setError(Error error) {
		this.error = error;
	}
}
