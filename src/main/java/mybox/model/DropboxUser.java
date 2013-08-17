package mybox.model;

public class DropboxUser extends User {
	
	private AccountInfo accountInfo;

	private String accessKey;
	
	private String accessSecret;

	@Override
	public String getId() {
		return accountInfo != null ? accountInfo.getUid().toString() : super.getId();
	}
	
	@Override
	public String getName() {
		return accountInfo != null ? accountInfo.getDisplayName() : super.getName();
	}

	public AccountInfo getAccountInfo() {
		return accountInfo;
	}

	public void setAccountInfo(AccountInfo accountInfo) {
		this.accountInfo = accountInfo;
	}
	
	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getAccessSecret() {
		return accessSecret;
	}

	public void setAccessSecret(String accessSecret) {
		this.accessSecret = accessSecret;
	}
}
