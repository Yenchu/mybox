package mybox.model.keystone;

import com.google.gson.annotations.SerializedName;

public class DomainEntity extends Entity {

	@SerializedName("domain_id")
	protected String domainId;
	
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append(super.toString());
		buf.append(", domainId=").append(domainId);
		return buf.toString();
	}

	public String getDomainId() {
		return domainId;
	}

	public void setDomainId(String domainId) {
		this.domainId = domainId;
	}
}
