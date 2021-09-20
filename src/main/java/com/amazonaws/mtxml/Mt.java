package com.amazonaws.mtxml;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mt {
	// TODO: Needs to be implemented, and then tested, naturally...

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Pattern pattern = Pattern.compile("{(\\d{1,3}):([A-Z])(0\\d|\\d{2})([A-Z]{12})(\\d{4})(\\d{6})}");
		Matcher matcher = pattern.matcher("{1:F01BANKFRPPAABC4321123456}");

//	    matcher.group(0);
		if (matcher.find()) {
			System.out.println("Groupcount: " + matcher.groupCount());
			System.out.println("Total match: " + matcher.group(0));
			System.out.println("Found group: " + matcher.group(1));
			System.out.println("Found group: " + matcher.group(2));
		} else {
			System.out.println("NO MATCH");
		}
	}

}
