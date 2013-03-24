package mybox.mondo.model;

import java.io.Serializable;

public class QuotaInfo implements Serializable {

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
