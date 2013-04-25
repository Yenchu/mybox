package mybox.model.filecruiser;

import mybox.model.Permission;

import com.google.gson.annotations.SerializedName;

public class SharingFile {
	
	protected Permission permission;
	
	@SerializedName("file_path")
	protected String filePath;
	
	@SerializedName("is_dir")
	protected boolean isDir;
	
	@SerializedName("domain_id")
	protected String domainId;

	@SerializedName("domain_name")
	protected String domainName;
	
	@SerializedName("tenant_id")
	protected String tenantId;

	@SerializedName("tenant_name")
	protected String tenantName;
	
	@SerializedName("user_id")
	protected String userId;

	@SerializedName("user_name")
	protected String userName;

	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("filePath=").append(filePath);
		buf.append(", isDir=").append(isDir);
		buf.append(", permission=").append(permission);
		buf.append(", domainId=").append(domainId);
		buf.append(", domainName=").append(domainName);
		buf.append(", tenantId=").append(tenantId);
		buf.append(", tenantName=").append(tenantName);
		buf.append(", userId=").append(userId);
		buf.append(", userName=").append(userName);
		return buf.toString();
	}
	
	public Permission getPermission() {
		return permission;
	}

	public void setPermission(Permission permission) {
		this.permission = permission;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public boolean getIsDir() {
		return isDir;
	}

	public void setIsDir(boolean isDir) {
		this.isDir = isDir;
	}

	public String getDomainId() {
		return domainId;
	}

	public void setDomainId(String domainId) {
		this.domainId = domainId;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getTenantName() {
		return tenantName;
	}

	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}
