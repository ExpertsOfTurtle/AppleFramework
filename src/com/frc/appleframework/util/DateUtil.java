package com.frc.appleframework.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DateUtil {
	private static final Object lockObj = new Object();
	
	public static final String DEFAULT_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	public static final String[] PATTER_LIST = new String[]{
		"yyyy-MM-dd",
		"yyyy-MM-dd HH:mm:ss",
		"yyyyMMdd",
		"yyyyMMddHHmmss"
	};

	/** 存放不同的日期模板格式的sdf的Map */
	private static Map<String, ThreadLocal<SimpleDateFormat>> sdfMap = new HashMap<String, ThreadLocal<SimpleDateFormat>>();

	public static String getDateTime(String pattern) {
		return getSdf(pattern).format(new Date());
	}
	public static String getDateTime() {
		return getSdf(DEFAULT_DATETIME_PATTERN).format(new Date());
	}
	
	private static SimpleDateFormat getSdf(final String pattern) {
		ThreadLocal<SimpleDateFormat> tl = sdfMap.get(pattern);

		// 此处的双重判断和同步是为了防止sdfMap这个单例被多次put重复的sdf
		if (tl == null) {
			synchronized (lockObj) {
				tl = sdfMap.get(pattern);
				if (tl == null) {
					// 只有Map中还没有这个pattern的sdf才会生成新的sdf并放入map
					System.out.println("put new sdf of pattern " + pattern + " to map");

					// 这里是关键,使用ThreadLocal<SimpleDateFormat>替代原来直接new
					// SimpleDateFormat
					tl = new ThreadLocal<SimpleDateFormat>() {

						@Override
						protected SimpleDateFormat initialValue() {
							System.out.println("thread: " + Thread.currentThread() + " init pattern: " + pattern);
							return new SimpleDateFormat(pattern);
						}
					};
					sdfMap.put(pattern, tl);
				}
			}
		}

		return tl.get();
	}

	public static String format(Date date, String pattern) {
		return getSdf(pattern).format(date);
	}

	public static Date parse(String dateStr, String pattern) throws ParseException {
		return getSdf(pattern).parse(dateStr);
	}
	public static Date parse(String dateStr) {
		for (String pattern : PATTER_LIST) {
			if (dateStr.trim().length() == pattern.length()) {
				try {
					Date date = getSdf(pattern).parse(dateStr);
					return date;
				} catch (ParseException e) {
				}
			}
		}
		return null;
	}
}
