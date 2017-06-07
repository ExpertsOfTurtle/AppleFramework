package com.frc.appleframework.beans;

public class AppleRequest implements IRequest {
	protected String requestType;
	protected Pagination Pagination;

	public String getRequestType() {
		return requestType;
	}
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}
	public Pagination getPagination() {
		return Pagination;
	}
	public void setPagination(Pagination pagination) {
		Pagination = pagination;
	}
}
