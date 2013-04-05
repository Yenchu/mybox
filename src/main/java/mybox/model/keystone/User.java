package mybox.model.keystone;

import com.google.gson.annotations.SerializedName;

public class User extends DomainEntity {

	// {"user": {"links": {"self": "http://localhost:5000/v3/users/4f3a84f3941b4336aeb8681fba7d858f"}, "description": "", "name": "tester", "id": "4f3a84f3941b4336aeb8681fba7d858f", "enabled": false, "domain_id": "default", "email": "tester@promise.com"}}

	protected String password;
	
	protected String email;
	
	@SerializedName("default_project_id")
	protected String defaultProjectId;
	
	protected String quota;

	protected String used;
	
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append(super.toString());
		buf.append(", password=").append(password);
		buf.append(", email=").append(email);
		buf.append(", defaultProjectId=").append(defaultProjectId);
		buf.append(", quota=").append(quota);
		buf.append(", used=").append(used);
		return buf.toString();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDefaultProjectId() {
		return defaultProjectId;
	}

	public void setDefaultProjectId(String defaultProjectId) {
		this.defaultProjectId = defaultProjectId;
	}
	
	public String getQuota() {
		return quota;
	}

	public void setQuota(String quota) {
		this.quota = quota;
	}

	public String getUsed() {
		return used;
	}

	public void setUsed(String used) {
		this.used = used;
	}
}
