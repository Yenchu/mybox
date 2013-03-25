package mybox.to;

import java.util.Collection;

public class Page<T> {

	private int page;
	
	private int totalPages;
	
	private int pageSize;
	
	private Collection<T> rows;
	
	private long totalRecords;

	public Page() {
	}
	
	public Page(Collection<T> records) {
		this.rows = records;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public Collection<T> getRows() {
		return rows;
	}

	public void setRows(Collection<T> records) {
		this.rows = records;
	}

	public long getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(long totalRecords) {
		this.totalRecords = totalRecords;
	}
}
