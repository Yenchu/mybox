package mybox.to;

import java.io.Serializable;

public class CachedObject<T> implements Serializable {

	private static final long serialVersionUID = -3033262137917764082L;

	private T value;
	
	private long cachedTime;
	
	private long timeToLive;

	public CachedObject() {
	}
	
	public CachedObject(long timeToLive) {
		this.timeToLive = timeToLive;
	}
	
	public CachedObject(T value, long timeToLive) {
		this.value = value;
		this.timeToLive = timeToLive;
		this.cachedTime = System.currentTimeMillis();
	}
	
	public boolean hasExpired() {
		// timeToLive < 1 means cache is permanent
		if (timeToLive < 1) {
			return false;
		}
		return (System.currentTimeMillis() - cachedTime > timeToLive);
	}

	public T getValue() {
		if (hasExpired()) {
			value = null;
		}
		return value;
	}

	public void setValue(T value) {
		this.value = value;
		this.cachedTime = System.currentTimeMillis();
	}

	public long getCachedTime() {
		return cachedTime;
	}

	public void setCachedTime(long cachedTime) {
		this.cachedTime = cachedTime;
	}

	public long getTimeToLive() {
		return timeToLive;
	}

	public void setTimeToLive(long timeToLive) {
		this.timeToLive = timeToLive;
	}
}
