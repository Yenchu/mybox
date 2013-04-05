package mybox.model.keystone;

public class Domain extends Entity {

	private String quota;

	private String used;
	
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append(super.toString());
		buf.append(", quota=").append(quota);
		buf.append(", used=").append(used);
		return buf.toString();
	}

	public String getQuota() {
		return quota;
	}

	public void setQuota(String quota) {
		this.quota = quota;
	}

	public String getUsed() {
		return used;
	}

	public void setUsed(String used) {
		this.used = used;
	}
}
