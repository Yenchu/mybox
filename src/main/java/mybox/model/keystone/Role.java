package mybox.model.keystone;

public class Role {

	private String id;
	
	private String name;
	
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("id=").append(id);
		buf.append(", name=").append(name);
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
}
