package mybox.model.filecruiser;

import mybox.model.Permission;

import com.google.gson.annotations.SerializedName;

public class SharedFile {

	private String id;

	private Integer notation;
	
	private Permission permission;

	@SerializedName("file_path")
	private String filePath;
	
	@SerializedName("is_dir")
	protected boolean isDir;

	@SerializedName("shared_path")
	private String sharedPath;
	
	@SerializedName("updated_time")
	private String updatedTime;
	
	@SerializedName("from_domain_id")
	private String fromDomainId;

	@SerializedName("from_domain_name")
	private String fromDomainName;
	
	@SerializedName("from_tenant_id")
	private String fromTenantId;

	@SerializedName("from_tenant_name")
	private String fromTenantName;
	
	@SerializedName("from_user_id")
	private String fromUserId;

	@SerializedName("from_user_name")
	private String fromUserName;
	
	@SerializedName("to_domain_id")
	private String toDomainId;

	@SerializedName("to_domain_name")
	private String toDomainName;
	
	@SerializedName("to_tenant_id")
	private String toTenantId;

	@SerializedName("to_tenant_name")
	private String toTenantName;
	
	@SerializedName("to_user_id")
	private String toUserId;

	@SerializedName("to_user_name")
	private String toUserName;
	
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("id=").append(id);
		buf.append(", filePath=").append(filePath);
		buf.append(", isDir=").append(isDir);
		buf.append(", sharedPath=").append(sharedPath);
		buf.append(", permission=").append(permission);
		buf.append(", updatedTime=").append(updatedTime);
		buf.append(", fromDomainId=").append(fromDomainId);
		buf.append(", fromDomainName=").append(fromDomainName);
		buf.append(", fromTenantId=").append(fromTenantId);
		buf.append(", fromTenantName=").append(fromTenantName);
		buf.append(", fromUserId=").append(fromUserId);
		buf.append(", fromUserName=").append(fromUserName);
		buf.append(", toDomainId=").append(toDomainId);
		buf.append(", toDomainName=").append(toDomainName);
		buf.append(", toTenantId=").append(toTenantId);
		buf.append(", toTenantName=").append(toTenantName);
		buf.append(", toUserId=").append(toUserId);
		buf.append(", toUserName=").append(toUserName);
		return buf.toString();
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getNotation() {
		return notation;
	}

	public void setNotation(Integer notation) {
		this.notation = notation;
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

	public String getSharedPath() {
		return sharedPath;
	}

	public void setSharedPath(String sharedPath) {
		this.sharedPath = sharedPath;
	}

	public String getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(String updatedTime) {
		this.updatedTime = updatedTime;
	}

	public String getFromDomainId() {
		return fromDomainId;
	}

	public void setFromDomainId(String fromDomainId) {
		this.fromDomainId = fromDomainId;
	}

	public String getFromDomainName() {
		return fromDomainName;
	}

	public void setFromDomainName(String fromDomainName) {
		this.fromDomainName = fromDomainName;
	}

	public String getFromTenantId() {
		return fromTenantId;
	}

	public void setFromTenantId(String fromTenantId) {
		this.fromTenantId = fromTenantId;
	}

	public String getFromTenantName() {
		return fromTenantName;
	}

	public void setFromTenantName(String fromTenantName) {
		this.fromTenantName = fromTenantName;
	}

	public String getFromUserId() {
		return fromUserId;
	}

	public void setFromUserId(String fromUserId) {
		this.fromUserId = fromUserId;
	}

	public String getFromUserName() {
		return fromUserName;
	}

	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}

	public String getToDomainId() {
		return toDomainId;
	}

	public void setToDomainId(String toDomainId) {
		this.toDomainId = toDomainId;
	}

	public String getToDomainName() {
		return toDomainName;
	}

	public void setToDomainName(String toDomainName) {
		this.toDomainName = toDomainName;
	}

	public String getToTenantId() {
		return toTenantId;
	}

	public void setToTenantId(String toTenantId) {
		this.toTenantId = toTenantId;
	}

	public String getToTenantName() {
		return toTenantName;
	}

	public void setToTenantName(String toTenantName) {
		this.toTenantName = toTenantName;
	}

	public String getToUserId() {
		return toUserId;
	}

	public void setToUserId(String toUserId) {
		this.toUserId = toUserId;
	}

	public String getToUserName() {
		return toUserName;
	}

	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}
}
