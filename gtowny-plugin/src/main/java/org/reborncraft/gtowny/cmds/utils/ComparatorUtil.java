package org.reborncraft.gtowny.cmds.utils;

public class ComparatorUtil {

	public static int compareStringIgnoreCase(String str1, String str2) {
		str1 = str1.toLowerCase();
		str2 = str2.toLowerCase();
		return compareString(str1, str2);
	}

	public static int compareString(String str1, String str2) {
		int minLen = str1.length() > str2.length() ? str1.length() : str2.length();
		for (int pos = 0; pos < minLen; pos++) {
			int s = str2.charAt(pos) - str1.charAt(pos);
			if (s != 0) {
				return s;
			}
		}
		return str2.length() - str1.length();
	}
}
