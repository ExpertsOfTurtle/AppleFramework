package com.frc.appleframework.beans;

public class Pagination {
	protected int pageSize;
	protected int pageNumber;
	protected int count;
	public Pagination() {	
	}	
	public Pagination(int pageSize, int pageNumber) {
		super();
		this.pageSize = pageSize;
		this.pageNumber = pageNumber;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
	public int getCount() {
		this.count = (pageNumber - 1) * pageSize;
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
}
