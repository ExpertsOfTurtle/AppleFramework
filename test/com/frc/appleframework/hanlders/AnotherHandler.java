package com.frc.appleframework.hanlders;

import org.springframework.stereotype.Service;

import com.frc.appleframework.beans.IRequest;
import com.frc.appleframework.exception.AppleException;

@Service("AnotherHandler")
public class AnotherHandler extends AbstractHandler {

	@Override
	public void process(IRequest request) throws AppleException {
		String data = request.getRequestType();
		putRequestData("count", Math.random() * 100);
		int x = (int)(Math.random() * 100);
		if (x % 2 == 0) {
			throw new AppleException("E1234", "I'm the random exception... " + x);
		}
	}
	
}
