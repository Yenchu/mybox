package mybox.mondo.model;

import java.io.Serializable;
import java.util.Date;

import mybox.common.to.Space;

import com.google.gson.annotations.SerializedName;

public class Group extends Space implements Serializable {

	//{"group_info": [
	//{"email_notification": false, "storage_quota_GB": 0, "group_description": "NA", "group_manager": "NA", "expiration": 1355214061, "group_user_portal_url": "NA", "activation": true, "group_name": "John", "group_uuid": "75845a74874744048d016e901cfc30f3"}, {"email_notification": true, "storage_quota_GB": 10, "group_description": "Cloudena's accounting group.", "group_manager": "John", "expiration": 1355243162, "group_user_portal_url": "www.cloudena.com", "activation": true, "group_name": "Accounting", "group_uuid": "9690633cd84345fb8917306e58e880d6"}, {"email_notification": true, "storage_quota_GB": 10, "group_description": "Cloudena's technical supports.", "group_manager": "John", "expiration": 1355243162, "group_user_portal_url": "www.cloudena.com", "activation": true, "group_name": "Technical supports", "group_uuid": "86fabe8e17344c6abb6d63e552f2d324"}, {"email_notification": true, "storage_quota_GB": 10, "group_description": "Cloudena's discussion group.", "group_manager": "John", "expiration": 1355243162, "group_user_portal_url": "www.cloudena.com", "activation": true, "group_name": "Discussion forum", "group_uuid": "7e549e1f4b6b4878b844f2938e20d022"}, 
	//{"email_notification": true, "storage_quota_GB": 10, "group_description": "Cloudena's Engineering group.", "group_manager": "John", "expiration": 1355243162, "group_user_portal_url": "www.kmt.com", "activation": true, "group_name": "Engineering", "group_uuid": "c4936eff214443779e1fcc6d1f6df180"}], 
	//"referral_link": "www.cloudena.com", "display_name": "charlestt_desktop", "uid": "d5af6a5d682f4d638155e2df3496661f", "country": "Taiwan", "quota_info": {"shared": 100, "quota": 200, "normal": 300}}

	@SerializedName("group_uuid")
	protected String id;
	
	@SerializedName("group_name")
	protected String name;
	
	@SerializedName("group_description")
	private String description;
	
	@SerializedName("group_manager")
	private String manager;
	
	@SerializedName("storage_quota_GB")
	private int quota;
	
	@SerializedName("number_of_user")
	private int numberOfUser;

	private int expiration;

	private boolean activation;

	@SerializedName("email_notification")
	private boolean notification;

	@SerializedName("group_user_portal_url")
	private String portalUrl;
	
	//* custom field not from mondo
	private String expiryDate;

	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("id=").append(id);
		buf.append(", name=").append(name);
		buf.append(", description=").append(description);
		buf.append(", manager=").append(manager);
		buf.append(", quota=").append(quota);
		buf.append(", numberOfUser=").append(numberOfUser);
		buf.append(", expiration=").append(expiration);
		buf.append(", activation=").append(activation);
		buf.append(", notification=").append(notification);
		buf.append(", portalUrl=").append(portalUrl);
		return buf.toString();
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getManager() {
		return manager;
	}

	public void setManager(String manager) {
		this.manager = manager;
	}

	public int getQuota() {
		return quota;
	}

	public void setQuota(int quota) {
		this.quota = quota;
	}

	public int getNumberOfUser() {
		return numberOfUser;
	}

	public void setNumberOfUser(int numberOfUser) {
		this.numberOfUser = numberOfUser;
	}

	public int getExpiration() {
		return expiration;
	}

	public void setExpiration(int expiration) {
		this.expiration = expiration;
	}

	public boolean isActivation() {
		return activation;
	}

	public void setActivation(boolean activation) {
		this.activation = activation;
	}

	public boolean isNotification() {
		return notification;
	}

	public void setNotification(boolean notification) {
		this.notification = notification;
	}

	public String getPortalUrl() {
		return portalUrl;
	}

	public void setPortalUrl(String portalUrl) {
		this.portalUrl = portalUrl;
	}

	public String getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}
}
