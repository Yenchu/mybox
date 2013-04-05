package mybox.model.keystone;

import java.util.List;

public class Groups {

	private List<Group> groups;
	
	private Links links;

	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("groups=").append(groups);
		buf.append(", links=").append(links);
		return buf.toString();
	}
	
	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public Links getLinks() {
		return links;
	}

	public void setLinks(Links links) {
		this.links = links;
	}
}
