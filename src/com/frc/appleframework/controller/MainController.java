package com.frc.appleframework.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.frc.appleframework.beans.IRequest;
import com.frc.appleframework.exception.AppleException;
import com.frc.appleframework.common.EnvIdentify;
import com.frc.appleframework.hanlders.AbstractHandler;
import com.frc.appleframework.util.IOUtil;
import com.frc.appleframework.util.StringUtil;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/main")
public class MainController {
	private static Logger logger = LoggerFactory.getLogger(MainController.class);
	private static final String DEFAULT_ERROR_VM = "error.vm";

	@Autowired
	private BeanFactory beanfactory;

	@RequestMapping(value = "/{domain}/{requestType}", produces = "application/json; charset=utf-8")
	public @ResponseBody Object main(@PathVariable String requestType, @PathVariable String domain,
			@RequestBody Object reqObject, HttpServletRequest request) {

		logger.info("domain={}, requestType={}", domain, requestType);

		// Using {requestType} from the url to identify different kind of
		// request
		String confName = "requesttype" + "/" + domain + "/" + requestType + ".json";
		System.out.println(confName);
		byte[] bytes = IOUtil.readBytesFromClasspath(confName);
		String configString = new String(bytes);
		JSONObject json = JSONObject.fromObject(configString);

		// The full class name of the request bean
		String requestBeanType = json.getString("requestBeanType");

		// Some handlers that defined with @Service("{name}")
		List<String> handlerNames = (List<String>) json.get("handlers");

		Map<String, String> resp = new HashMap<String, String>();
		Map classMap = new HashMap<String, Class>();
		Class cl = null;
		try {
			cl = Class.forName(requestBeanType);
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		JSONObject jsonRequest = JSONObject.fromObject(reqObject);
		logger.info("JsonRequest:{}", jsonRequest.toString());

		Object requestBeanMapObj = json.get("requestBeanMap");
		if (requestBeanMapObj != null) {
			resp = json.getJSONObject("requestBeanMap");
			for (Entry<String, String> entry : resp.entrySet()) {
				String key = entry.getKey();
				String val = entry.getValue();
				try {
					Class clz = Class.forName(val);
					classMap.put(key, clz);
				} catch (ClassNotFoundException e) {
					logger.error(e.getMessage());
					e.printStackTrace();
				}
			}
		}
		IRequest apple = (IRequest) JSONObject.toBean(jsonRequest, cl, classMap);

		AbstractHandler.cleanRequestData();
		AbstractHandler.getRequestData().put("requestType", apple.getRequestType());
		AbstractHandler.getRequestData().put("StringUtil", new StringUtil());
		for (String handlerName : handlerNames) {
			try {
				AbstractHandler handler = (AbstractHandler) beanfactory.getBean(handlerName);
				handler.process(apple);
			} catch (AppleException e) {
				logger.error("Handler exception with errorCode={}, errorMessage={}", e.getErrorCode(),
						e.getErrorMessage());

				String errorVm = json.getString("error");
				if (errorVm == null || errorVm.trim().length() == 0) {
					errorVm = DEFAULT_ERROR_VM;
				}
				return gotoErrorPage(errorVm, e.getErrorCode(), e.getErrorMessage());
			} catch (Exception e) {
				logger.error("Generic exception: errorMessage={}", e.getMessage());
				e.printStackTrace();
				String errorVm = json.getString("error");
				if (errorVm == null || errorVm.trim().length() == 0) {
					errorVm = DEFAULT_ERROR_VM;
				}
				return gotoErrorPage(errorVm, "E00000", e.getMessage());
			}
		}

		String rs = "";
		if ("json".equals(apple.getRequestType())) {
			String vm = "vm/json.vm";
			rs = gotoPage(vm);
		} else {
			String vm = json.getString("response");
			Object obj = AbstractHandler.getRequestData().get("responseVM");
			if (obj != null) {
				vm = (String) obj;
				System.out.println(vm);
			}
			System.out.println(vm);
			rs = gotoPage(vm);
		}

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
		ve.setProperty(Velocity.ENCODING_DEFAULT, "GBK");
		ve.setProperty(Velocity.INPUT_ENCODING, "GBK");
		ve.setProperty(Velocity.OUTPUT_ENCODING, "GBK");

		boolean isLocal = EnvIdentify.isLocalDebug;
		if (isLocal && !vmName.endsWith("json.vm")) {
			String rootPath = "E:\\Workspace\\BankSim\\Bronze\\webapp\\";
			ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "file");
			ve.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, rootPath);
		}
		ve.init();

		Template t = ve.getTemplate(vmName);

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
		ve.setProperty(Velocity.ENCODING_DEFAULT, "GBK");
		ve.setProperty(Velocity.INPUT_ENCODING, "GBK");
		ve.setProperty(Velocity.OUTPUT_ENCODING, "GBK");

		boolean isLocal = EnvIdentify.isLocalDebug;
		if (isLocal) {
			String rootPath = "E:\\Project\\Java\\AppleFramework\\webapp\\";
			ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "file");
			ve.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, rootPath);
		}
		ve.init();

		Template t = ve.getTemplate(vmName);

		StringWriter sw = new StringWriter();
		t.merge(ctx, sw);

		return sw.toString();
	}

	@RequestMapping("/download")
	public String downloadFile(@RequestParam("fileName") String fileName, HttpServletRequest request,
			HttpServletResponse response) {
		if (fileName != null) {
//			String realPath = request.getServletContext().getRealPath("WEB-INF/File/");
			String realPath = "/tmp/";
			if (EnvIdentify.isLocalDebug) {
				realPath = "d:\\tmp\\";
			}
			File file = new File(realPath, fileName);
			if (file.exists()) {
				response.setContentType("application/force-download");// 设置强制下载不打开
				response.addHeader("Content-Disposition", "attachment;fileName=" + file.getName());// 设置文件名
				byte[] buffer = new byte[1024];
				FileInputStream fis = null;
				BufferedInputStream bis = null;
				try {
					fis = new FileInputStream(file);
					bis = new BufferedInputStream(fis);
					OutputStream os = response.getOutputStream();
					int i = bis.read(buffer);
					while (i != -1) {
						os.write(buffer, 0, i);
						i = bis.read(buffer);
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				} finally {
					if (bis != null) {
						try {
							bis.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if (fis != null) {
						try {
							fis.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
		return null;
	}
}
