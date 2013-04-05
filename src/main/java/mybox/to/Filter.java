package mybox.to;

public class Filter {
	
	private Integer page;
	
	private Integer perPage;

	private String name;
	
	private String domainId;
	
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("name=").append(name);
		buf.append(", domainId=").append(domainId);
		return buf.toString();
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getPerPage() {
		return perPage;
	}

	public void setPerPage(Integer perPage) {
		this.perPage = perPage;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDomainId() {
		return domainId;
	}

	public void setDomainId(String domainId) {
		this.domainId = domainId;
	}
}
