package com.frc.appleframework.exception;

public class AppleException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2320964444439835445L;
	protected String errorCode;
	protected String errorMessage;
	
	public AppleException(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
