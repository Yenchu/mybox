package mybox.rest;

public class FaultException extends RuntimeException {

	private static final long serialVersionUID = 6039859323225769379L;
	
	private Fault fault;

	public FaultException() {
	}
	
	public FaultException(int code) {
		this.fault = new Fault(code);
	}
	
	public FaultException(int code, String type) {
		this.fault = new Fault(code, type);
	}
	
	public FaultException(int code, String type, String message) {
		this.fault = new Fault(code, type, message);
	}
	
	public FaultException(Fault fault) {
		this.fault = fault;
	}

	@Override
	public String getMessage() {
		if (fault != null) {
			return fault.getMessage();
		} else {
			return super.getMessage();
		}
	}

	public Fault getFault() {
		return fault;
	}
	
	public void setFault(Fault fault){
		this.fault = fault;
	}
}
