package mybox.model;

public class Permission {
	
	public static final Integer WRITE = 2;

	public static final Integer READ = 4;
	
	private Boolean write;
	
	public static Permission getPermission(Integer notation) {
		Permission permission = new Permission();
		if (notation != null) {
			if (Permission.WRITE == notation) {
				permission.setWrite(true);
				return permission;
			}
		}
		permission.setWrite(false);
		return permission;
	}
	
	public Integer getNotation() {
		if (write) {
			return Permission.WRITE;
		} else {
			return Permission.READ;
		}
	}
	
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("write=").append(write);
		return buf.toString();
	}

	public Boolean getWrite() {
		return write;
	}

	public void setWrite(Boolean write) {
		this.write = write;
	}
}
