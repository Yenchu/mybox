package mybox.to;

import java.util.Collection;

public class Page<T> {

	private int page;

	private int pageSize;
	
	private Collection<T> records;
	
	private long totalRecords;

	public Page() {
	}
	
	public Page(Collection<T> records) {
		this.records = records;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public Collection<T> getRecords() {
		return records;
	}

	public void setRecords(Collection<T> records) {
		this.records = records;
	}

	public long getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(long totalRecords) {
		this.totalRecords = totalRecords;
	}
}
