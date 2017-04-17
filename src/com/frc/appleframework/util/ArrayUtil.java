package com.frc.appleframework.util;

public class ArrayUtil {
	public static double getMax(double [] arr) {
		if (arr == null || arr.length == 0) {
			return 0;
		}
		double m = arr[0];
		for (int i = 1; i < arr.length; i++) {
			m = m > arr[i] ? m : arr[i];
		}
		return m;
	}	
	public static double getMin(double [] arr) {
		if (arr == null || arr.length == 0) {
			return 0;
		}
		double m = arr[0];
		for (int i = 1; i < arr.length; i++) {
			m = m < arr[i] ? m : arr[i];
		}
		return m;
	}
	public static double getSum(double [] arr) {
		if (arr == null || arr.length == 0) {
			return 0;
		}
		double m = 0.0;
		for (int i = 0; i < arr.length; i++) {
			m += arr[i];
		}
		return m;
	}
	public static int getMax(int [] arr) {
		if (arr == null || arr.length == 0) {
			return 0;
		}
		int m = arr[0];
		for (int i = 1; i < arr.length; i++) {
			m = m > arr[i] ? m : arr[i];
		}
		return m;
	}	
	public static int getMin(int [] arr) {
		if (arr == null || arr.length == 0) {
			return 0;
		}
		int m = arr[0];
		for (int i = 1; i < arr.length; i++) {
			m = m < arr[i] ? m : arr[i];
		}
		return m;
	}
	public static int getSum(int [] arr) {
		if (arr == null || arr.length == 0) {
			return 0;
		}
		int m = 0;
		for (int i = 0; i < arr.length; i++) {
			m += arr[i];
		}
		return m;
	}
}
