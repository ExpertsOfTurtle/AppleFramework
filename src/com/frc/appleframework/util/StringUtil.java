package com.frc.appleframework.util;

public class StringUtil {
	public static boolean isEmpty(String str) {
		return str == null || str.trim().length() == 0;
	}
	public static String getString(String str) {
		return isEmpty(str) ? "" : str;
	}
	public static String left(String str, int n) {
		if (isEmpty(str) || str.length() < n) {
			return str;
		}
		return str.substring(0, n);
	}
	public static String right(String str, int n) {
		if (isEmpty(str) || str.length() < n) {
			return str;
		}
		return str.substring(str.length() - n);
	}
}
