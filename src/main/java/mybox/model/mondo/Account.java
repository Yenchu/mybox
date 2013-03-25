package mybox.model.mondo;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Account implements Serializable {

	private String uid;
	
	@SerializedName("display_name")
	private String displayName;
	
	private String country;
	
	@SerializedName("referral_link")
	private String referralLink;

	@SerializedName("quota_info")
	private QuotaInfo quotaInfo;
	
	@SerializedName("group_info")
	private List<Group> groups;
	
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("uid=").append(uid);
		buf.append(", displayName=").append(displayName);
		buf.append(", country=").append(country);
		buf.append(", referralLink=").append(referralLink);
		buf.append(", quotaInfo={").append(quotaInfo).append("}");
		buf.append(", groups=").append(groups);
		return buf.toString();
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
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

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}
}
