package com.frc.appleframework.controller;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.frc.appleframework.beans.IRequest;
import com.frc.appleframework.exception.AppleException;
import com.frc.appleframework.hanlders.AbstractHandler;
import com.frc.appleframework.util.IOUtil;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/main")
public class MainController {
	private static Logger logger  =  LoggerFactory.getLogger(MainController.class);
	private static final String DEFAULT_ERROR_VM = "error.vm";

	@Autowired
	private BeanFactory beanfactory;

	@RequestMapping(value = "/{domain}/{requestType}")
	public @ResponseBody Object test(
			@PathVariable String requestType,
			@PathVariable String domain,
			@RequestBody Object reqObject,
			HttpServletRequest request) {

		logger.info("domain={}, requestType={}", domain, requestType);
		
		// Using {requestType} from the url to identify different kind of
		// request
		String confName = "requesttype" + "/" + domain + "/" + requestType + ".json";
		byte[] bytes = IOUtil.readBytesFromClasspath(confName);
		String configString = new String(bytes);
		JSONObject json = JSONObject.fromObject(configString);

		// The full class name of the request bean
		String requestBeanType = json.getString("requestBeanType");

		// Some handlers that defined with @Service("{name}")
		List<String> handlerNames = (List<String>) json.get("handlers");

		Map resp = new HashMap<String, String>();
		Class cl = null;
		try {
			cl = Class.forName(requestBeanType);
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		IRequest apple = (IRequest) JSONObject.toBean(JSONObject.fromObject(reqObject), cl);

		AbstractHandler.cleanRequestData();
		
		for (String handlerName : handlerNames) {
			try {
				AbstractHandler handler = (AbstractHandler) beanfactory.getBean(handlerName);
				handler.process(apple);	
			} catch (AppleException e) {
				logger.error("Handler exception with errorCode={}, errorMessage={}", 
						e.getErrorCode(), e.getErrorMessage());
				
				String errorVm = json.getString("error");
				if (errorVm == null || errorVm.trim().length() == 0) {
					errorVm = DEFAULT_ERROR_VM;
				}
				return gotoErrorPage(errorVm, e.getErrorCode(), e.getErrorMessage());
			}			
		}

		String vm = json.getString("response");
		String rs = gotoPage(vm);
		return rs;
	}
	
	protected String gotoPage(String vmName) {
		logger.info("got to page:" + vmName);
		VelocityContext ctx = new VelocityContext();
		Map<String, Object> requestData = (Map<String, Object>) AbstractHandler.getRequestData();
		for (Entry<String, Object> entry : requestData.entrySet()) {
			String key = entry.getKey();
			Object obj = entry.getValue();
			ctx.put(key, obj);
		}
		
		VelocityEngine ve = new VelocityEngine();
		ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		ve.init();

		Template t = ve.getTemplate("vm/" + vmName);

		StringWriter sw = new StringWriter();
		t.merge(ctx, sw);
		
		return sw.toString();
	}
	protected String gotoErrorPage(String vmName, String errorCode, String errorMessage) {
		logger.info("got to error page:" + vmName);
		VelocityContext ctx = new VelocityContext();
		Map<String, Object> requestData = (Map<String, Object>) AbstractHandler.getRequestData();
		for (Entry<String, Object> entry : requestData.entrySet()) {
			String key = entry.getKey();
			Object obj = entry.getValue();
			ctx.put(key, obj);
		}
		ctx.put("errorCode", errorCode);
		ctx.put("errorMessage", errorMessage);
		
		VelocityEngine ve = new VelocityEngine();
		ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		ve.init();

		Template t = ve.getTemplate("vm/" + vmName);

		StringWriter sw = new StringWriter();
		t.merge(ctx, sw);
		
		return sw.toString();
	}

}
