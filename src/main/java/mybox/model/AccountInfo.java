package mybox.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class AccountInfo implements Serializable {

	private Long uid;
	
	@SerializedName("display_name")
	private String displayName;

	private String country;

	@SerializedName("referral_link")
	private String referralLink;
	
	@SerializedName("quota_info")
	private QuotaInfo quotaInfo;

    public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("uid=").append(uid);
		buf.append(", displayName=").append(displayName);
		buf.append(", country=").append(country);
		buf.append(", referralLink=").append(referralLink);
		buf.append(", quotaInfo={").append(quotaInfo).append("}");
		return buf.toString();
	}

	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
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

	public String getReferralLink() {
		return referralLink;
	}

	public void setReferralLink(String referralLink) {
		this.referralLink = referralLink;
	}

	public QuotaInfo getQuotaInfo() {
		return quotaInfo;
	}

	public void setQuotaInfo(QuotaInfo quotaInfo) {
		this.quotaInfo = quotaInfo;
	}

	public static class QuotaInfo implements Serializable {

		private long quota;

		private long normal;

		private long shared;
		
		public String toString() {
			StringBuilder buf = new StringBuilder();
			buf.append("quota=").append(quota);
			buf.append(", normal=").append(normal);
			buf.append(", shared=").append(shared);
			return buf.toString();
		}
		
		public long getQuota() {
			return quota;
		}

		public void setQuota(long quota) {
			this.quota = quota;
		}

		public long getNormal() {
			return normal;
		}

		public void setNormal(long normal) {
			this.normal = normal;
		}

		public long getShared() {
			return shared;
		}

		public void setShared(long shared) {
			this.shared = shared;
		}
	}
}
