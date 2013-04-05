package mybox.model.keystone;

public class Links {

	private String self;
	
	private String previous;
	
	private String next;
	
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("self=").append(self);
		buf.append(", previous=").append(previous);
		buf.append(", next=").append(next);
		return buf.toString();
	}

	public String getSelf() {
		return self;
	}

	public void setSelf(String self) {
		this.self = self;
	}

	public String getPrevious() {
		return previous;
	}

	public void setPrevious(String previous) {
		this.previous = previous;
	}

	public String getNext() {
		return next;
	}

	public void setNext(String next) {
		this.next = next;
	}
}
