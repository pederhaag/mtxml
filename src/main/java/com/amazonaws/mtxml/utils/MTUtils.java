package com.amazonaws.mtxml.utils;

public class MTUtils {
	public static String[] splitLogicalTerminal(String LTaddress) {
		String BIC8 = LTaddress.substring(0, 8);
		String BIC = formatBIC(BIC8 + LTaddress.substring(9, 12));
		String LT = LTaddress.substring(8, 9);
		return new String[] { BIC, LT, BIC8 };
	}

	public static String formatBIC(String BIC) {
		return BIC.endsWith("XXX") ? BIC.substring(0, 8) : BIC;
	}
}
