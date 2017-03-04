package com.frc.appleframework.beans;

public class AppleRequest implements IRequest {
	protected String requestType;

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}
}
