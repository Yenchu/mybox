package mybox.model.keystone;

import java.util.List;

public class Roles {

	private List<Role> roles;
	
	private Links links;

	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("roles=").append(roles);
		buf.append(", links=").append(links);
		return buf.toString();
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public Links getLinks() {
		return links;
	}

	public void setLinks(Links links) {
		this.links = links;
	}
}
