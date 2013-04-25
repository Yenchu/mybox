package mybox.to;

import mybox.model.Permission;
import mybox.model.filecruiser.SharingFile;

public class SharingFileTo extends SharingFile {

	private Integer notation;

	private SharingFile sharingFile;

	public Integer getNotation() {
		return notation;
	}

	public void setNotation(Integer notation) {
		this.notation = notation;
	}

	public SharingFile getSharingFile() {
		return sharingFile;
	}

	public void setSharingFile(SharingFile sharingFile) {
		this.sharingFile = sharingFile;
	}

	@Override
	public Permission getPermission() {
		return sharingFile.getPermission();
	}

	@Override
	public void setPermission(Permission permission) {
		sharingFile.setPermission(permission);
	}

	@Override
	public String getFilePath() {
		return sharingFile.getFilePath();
	}

	@Override
	public void setFilePath(String filePath) {
		sharingFile.setFilePath(filePath);
	}

	@Override
	public boolean getIsDir() {
		return sharingFile.getIsDir();
	}

	@Override
	public void setIsDir(boolean isDir) {
		sharingFile.setIsDir(isDir);
	}

	@Override
	public String getDomainId() {
		return sharingFile.getDomainId();
	}

	@Override
	public void setDomainId(String domainId) {
		sharingFile.setDomainId(domainId);
	}

	@Override
	public String getDomainName() {
		return sharingFile.getDomainName();
	}

	@Override
	public void setDomainName(String domainName) {
		sharingFile.setDomainName(domainName);
	}

	@Override
	public String getTenantId() {
		return sharingFile.getTenantId();
	}

	@Override
	public void setTenantId(String tenantId) {
		sharingFile.setTenantId(tenantId);
	}

	@Override
	public String getTenantName() {
		return sharingFile.getTenantName();
	}

	@Override
	public void setTenantName(String tenantName) {
		sharingFile.setTenantName(tenantName);
	}

	@Override
	public String getUserId() {
		return sharingFile.getUserId();
	}

	@Override
	public void setUserId(String userId) {
		sharingFile.setUserId(userId);
	}

	@Override
	public String getUserName() {
		return sharingFile.getUserName();
	}

	@Override
	public void setUserName(String userName) {
		sharingFile.setUserName(userName);
	}
}
