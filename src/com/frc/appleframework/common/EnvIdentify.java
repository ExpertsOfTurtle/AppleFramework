package com.frc.appleframework.common;

import java.io.File;


public class EnvIdentify {
	/*
	 * 判断是否本地调试
	 * */
	public static boolean isLocalDebug;
	
	static {
		File file = new File("c:\\turtle");
		if (file.exists() && file.isFile()) {
			isLocalDebug = true;
		} else {
			isLocalDebug = false;
		}
	}
}
