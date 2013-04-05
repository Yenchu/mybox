package mybox.model.keystone;

public class Entity {

	protected String id;
	
	protected String name;

	protected String description;
	
	protected Boolean enabled;
	
	private Links links;
	
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("id=").append(id);
		buf.append(", name=").append(name);
		buf.append(", description=").append(description);
		buf.append(", enabled=").append(enabled);
		buf.append(", links=").append(links);
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Links getLinks() {
		return links;
	}

	public void setLinks(Links links) {
		this.links = links;
	}
}
