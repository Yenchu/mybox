package mybox.model.keystone;

public class Service {
	//, {"id": "4f775abe26244e0c8920204c8d0c41f1", "type": "user_portal", "name": "user_portal", "links": {"self": "http://localhost:5000/v3/services/4f775abe26244e0c8920204c8d0c41f1"}, "description": null}
	
	private String id;
	
	private String name;

	private String description;
	
	private Links links;
	
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("id=").append(id);
		buf.append(", name=").append(name);
		buf.append(", description=").append(description);
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

	public Links getLinks() {
		return links;
	}

	public void setLinks(Links links) {
		this.links = links;
	}
}
