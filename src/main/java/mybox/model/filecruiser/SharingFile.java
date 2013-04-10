package mybox.model.filecruiser;

import com.google.gson.annotations.SerializedName;

public class SharingFile {

	@SerializedName("file_path")
	private String filePath;
	
	@SerializedName("is_dir")
	protected boolean isDir;
	
	private Permission permission;
	
	@SerializedName("domain_id")
	private String domainId;

	@SerializedName("domain_name")
	private String domainName;
	
	@SerializedName("tenant_id")
	private String tenantId;

	@SerializedName("tenant_name")
	private String tenantName;
	
	@SerializedName("user_id")
	private String userId;

	@SerializedName("user_name")
	private String userName;

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

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public boolean isDir() {
		return isDir;
	}

	public void setDir(boolean isDir) {
		this.isDir = isDir;
	}

	public Permission getPermission() {
		return permission;
	}

	public void setPermission(Permission permission) {
		this.permission = permission;
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
	
	public static class Permission {
		
		private Boolean write;
		
		public static Permission getPermission(Integer code) {
			Permission permission = new Permission();
			if (code != null) {
				if (code == 2) {
					permission.setWrite(true);
					return permission;
				}
			}
			permission.setWrite(false);
			return permission;
		}
		
		public String toString() {
			StringBuilder buf = new StringBuilder();
			buf.append("write=").append(write);
			return buf.toString();
		}

		public Boolean getWrite() {
			return write;
		}

		public void setWrite(Boolean write) {
			this.write = write;
		}
	}
}
