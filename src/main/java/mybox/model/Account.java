package mybox.model;

public class Account {

	private long uid;
	
	private String displayName;
	
	private String country;
	
	private long quota;

	private long quotaNormal;

	private long quotaShared;

	private String referralLink;
	
	private int[] role;

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public long getQuota() {
		return quota;
	}

	public void setQuota(long quota) {
		this.quota = quota;
	}

	public long getQuotaNormal() {
		return quotaNormal;
	}

	public void setQuotaNormal(long quotaNormal) {
		this.quotaNormal = quotaNormal;
	}

	public long getQuotaShared() {
		return quotaShared;
	}

	public void setQuotaShared(long quotaShared) {
		this.quotaShared = quotaShared;
	}

	public String getReferralLink() {
		return referralLink;
	}

	public void setReferralLink(String referralLink) {
		this.referralLink = referralLink;
	}

	public int[] getRole() {
		return role;
	}

	public void setRole(int[] role) {
		this.role = role;
	}
}
