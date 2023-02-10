package com.increff.pos.util;

public class StringUtil {

	public static boolean isEmpty(String s) {
		return s=="" || s == null || s.trim().length() == 0;
	}

	public static String toLowerCase(String s) {
		return s == null ? null : s.trim().toLowerCase();
	}

}
