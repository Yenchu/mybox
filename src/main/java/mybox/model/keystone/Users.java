package mybox.model.keystone;

import java.util.List;

public class Users {

	// {"users": [], "links": {"self": "http://localhost:5000/v3/users", "previous": null, "next": null}}
	private List<User> users;
	
	private Links links;
	
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("users=").append(users);
		buf.append(", links=").append(links);
		return buf.toString();
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public Links getLinks() {
		return links;
	}

	public void setLinks(Links links) {
		this.links = links;
	}
}
