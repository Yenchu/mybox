package mybox.common.to;

import java.io.Serializable;

public class User implements Serializable {

	protected String name;

	protected String address;

	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("name=").append(name);
		buf.append(", address=").append(address);
		return buf.toString();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
