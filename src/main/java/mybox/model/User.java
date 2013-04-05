package mybox.model;

import java.io.Serializable;

public class User implements Serializable {
	
	protected String id;
	
	protected String name;

	protected String ip;

	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("id=").append(id);
		buf.append(", name=").append(name);
		buf.append(", ip=").append(ip);
		return buf.toString();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
}
