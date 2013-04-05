package mybox.model.keystone;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Token {

	@SerializedName("expires_at")
	private String expiresAt;
	
	@SerializedName("issue_at")
	private String issuedAt;
	
	private List<String> methods;

	private User user;
	
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("expiresAt=").append(expiresAt);
		buf.append(", issuedAt=").append(issuedAt);
		buf.append(", methods=").append(methods);
		buf.append(", user=").append(user);
		return buf.toString();
	}
	
	public String getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(String expiresAt) {
		this.expiresAt = expiresAt;
	}

	public String getIssuedAt() {
		return issuedAt;
	}

	public void setIssuedAt(String issuedAt) {
		this.issuedAt = issuedAt;
	}

	public List<String> getMethods() {
		return methods;
	}

	public void setMethods(List<String> methods) {
		this.methods = methods;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public static class User {
		
		private String id;
		
		private String name;
		
		private Links links;
		
		private Domain domain;
		
		public String toString() {
			StringBuilder buf = new StringBuilder();
			buf.append("id=").append(id);
			buf.append(", name=").append(name);
			buf.append(", links=").append(links);
			buf.append(", domain=").append(domain);
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

		public Links getLinks() {
			return links;
		}

		public void setLinks(Links links) {
			this.links = links;
		}

		public Domain getDomain() {
			return domain;
		}

		public void setDomain(Domain domain) {
			this.domain = domain;
		}
	}

	public static class Domain {
		
		private String id;
		
		private String name;
		
		private Links links;
		
		public String toString() {
			StringBuilder buf = new StringBuilder();
			buf.append("id=").append(id);
			buf.append(", name=").append(name);
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

		public Links getLinks() {
			return links;
		}

		public void setLinks(Links links) {
			this.links = links;
		}
	}
}

