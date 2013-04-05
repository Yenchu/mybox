package mybox.model.keystone;

import java.util.ArrayList;
import java.util.List;

public class Auth {
	
	private Identity identity;
	
	public Auth(String domainName, String userName, String userPwd) {
		identity = new Identity();
		
		Domain domain = new Domain();
		domain.setName(domainName);
		User user = new User();
		user.setName(userName);
		user.setPassword(userPwd);
		user.setDomain(domain);
		
		Password password = new Password();
		password.setUser(user);
		identity.setPassword(password);
		
		List<String> methods = new ArrayList<String>();
		methods.add(Method.PASSWORD);
		identity.setMethods(methods);
	}
	
	public Identity getIdentity() {
		return identity;
	}

	public void setIdentity(Identity identity) {
		this.identity = identity;
	}

	public static class Identity {
		
		private List<String> methods;

		private Password password;

		public List<String> getMethods() {
			return methods;
		}

		public void setMethods(List<String> methods) {
			this.methods = methods;
		}

		public Password getPassword() {
			return password;
		}

		public void setPassword(Password password) {
			this.password = password;
		}
	}
	
	public static class Password {
		
		private User user;

		public User getUser() {
			return user;
		}

		public void setUser(User user) {
			this.user = user;
		}
	}

	public static class User {
		
		private String name;
		
		private String password;
		
		private Domain domain;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
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
}
