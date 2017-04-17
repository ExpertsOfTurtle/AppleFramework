package com.frc.appleframework.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.NoSuchElementException;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

public class ClassPathPropertiesUtils {
	private static ResourceLoader resourceLoader = new DefaultResourceLoader();
	private static Properties properties;
	public static final String[] PROP_FILES = { "application.test.properties", "application.local.properties" };

	private static Logger logger = LoggerFactory.getLogger("TEST");

	private static void initPropertiesWithProfile() {
		Properties profileProps = getPropertiesFromProfile(PROP_FILES);

		mergeProperties(profileProps);
	}

	public static void initProperties(String[] propertiesFiles) {
		if (properties == null)
			properties = getPropertiesByFileName(propertiesFiles);
	}

	private static void mergeProperties(Properties props) {
		if (properties != null)
			properties.putAll(props);
	}

	public static String getValue(String key) {
		String systemProperty = System.getProperty(key);
		if (systemProperty != null) {
			return systemProperty;
		}
		String s = properties.getProperty(key);

		return s;
	}

	public static String getProperty(String key) {
		String value = getValue(key);
		if (value == null) {
			throw new NoSuchElementException();
		}
		return value.trim();
	}

	public static boolean hasProperty(String key) {
		String value = getValue(key);

		return (value != null);
	}

	public static String getProperty(String key, String defaultValue) {
		String value = getValue(key);
		return ((value != null) ? value.trim() : defaultValue);
	}

	public static int getInteger(String key) {
		String value = getValue(key);
		if (value == null) {
			throw new NoSuchElementException();
		}
		return Integer.valueOf(value).intValue();
	}

	public static int getInteger(String key, Integer defaultValue) {
		String value = getValue(key);
		return ((value != null) ? Integer.valueOf(value) : defaultValue).intValue();
	}

	public static double getDouble(String key) {
		String value = getValue(key);
		if (value == null) {
			throw new NoSuchElementException();
		}
		return Double.valueOf(value).doubleValue();
	}

	public static double getDouble(String key, Integer defaultValue) {
		String value = getValue(key);
		return defaultValue.intValue();
	}

	public static boolean getBoolean(String key) {
		String value = getValue(key);
		if (value == null) {
			throw new NoSuchElementException();
		}
		return Boolean.valueOf(value).booleanValue();
	}

	public static boolean getBoolean(String key, boolean defaultValue) {
		String value = getValue(key);
		return ((value != null) ? Boolean.valueOf(value).booleanValue() : defaultValue);
	}

	public static Properties getPropertiesByFileName(String[] fileNames) {
		Properties props = new Properties();
		for (String fileName : fileNames) {
			loadProperties(props, new String[] { "classpath:/" + fileName });
		}
		return props;
	}

	public static Properties getProfilePropertiesByFileName(String[] fileNames) {
		Properties props = getPropertiesByFileName(fileNames);

		Properties profileProps = getPropertiesFromProfile(fileNames);
		props.putAll(profileProps);
		return props;
	}

	public static Properties getPropertiesFromProfile(String[] fileNames) {
		Properties props = new Properties();
		String activeProfile = "default";
		for (String fileName : fileNames) {
			String profileFile = "classpath:/profiles/" + activeProfile + "/" + fileName;
			loadProperties(props, new String[] { profileFile });
		}
		return props;
	}

	public static Properties getProperties() {
		return properties;
	}

	private static void loadPropertiesFromResource(Properties props, Resource resource) {
		InputStream is = null;
		try {
			if (!(resource.exists())) {
				return;
			}
			is = resource.getInputStream();
			props.load(new InputStreamReader(is, "UTF-8"));
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			IOUtils.closeQuietly(is);
		}
	}

	private static Properties loadProperties(Properties props, String[] resourcesPaths) {
		for (String location : resourcesPaths) {
			Resource resource = resourceLoader.getResource(location);
			loadPropertiesFromResource(props, resource);
		}
		return props;
	}

	static {
		initProperties(PROP_FILES);
		initPropertiesWithProfile();
	}
}
