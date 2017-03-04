package com.frc.appleframework.hanlders;

import java.util.HashMap;
import java.util.Map;

import com.frc.appleframework.beans.IRequest;
import com.frc.appleframework.exception.AppleException;

abstract public class AbstractHandler {
	private static final Object lockObj = new Object();
	private static ThreadLocal<HashMap> workcontext = new ThreadLocal<HashMap>();
	
	private static final String REQUEST_DATA = "RequestData";
	
	abstract public void process(IRequest request) throws AppleException;
	
	public static Map getRequestData() {
		Map map = getWorkContext();
		Map requestData = (Map)map.get(REQUEST_DATA);
		if (requestData == null) {
			requestData = new HashMap<String, Object>();
			map.put(REQUEST_DATA, requestData);
		}
		return requestData;
	}
	public static void cleanRequestData() {
		Map requestData = getRequestData();
		requestData.clear();
	}
		
	protected static Object getRequestData(String key) {
		Map requestData = getRequestData();
		return requestData.get(key);
	}
	protected static void putRequestData(String key, Object data) {
		HashMap map = getWorkContext();
		Map requestData = (Map)map.get(REQUEST_DATA);
		if (requestData == null) {
			requestData = new HashMap<String, Object>();
			map.put(REQUEST_DATA, requestData);
		}
		requestData.put(key, data);
	}
	protected static HashMap getWorkContext() {
		HashMap result = workcontext.get();
		if (result == null) {
			synchronized (lockObj) {
				result = workcontext.get();
				if (result == null) {
					result = new HashMap<String, Object>();
					workcontext.set(result);
				}
			}
		}
		return result;
	}
}
