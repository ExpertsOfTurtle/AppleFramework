package com.frc.appleframework.hanlders;

import org.springframework.stereotype.Service;

import com.frc.appleframework.beans.IRequest;

@Service("TestHandler")
public class TestHandler extends AbstractHandler {

	@Override
	public void process(IRequest request) {
		String data = request.getRequestType();
		putRequestData("wf", data);
	}
	
}
