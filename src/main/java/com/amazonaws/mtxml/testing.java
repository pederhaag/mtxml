package com.amazonaws.mtxml;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class testing {

	public static void main(String[] args) {
		String regex = "(?<Qualifier>(?>:(?>[A-Z0-9]){0,4}\\/\\/))(?<NameAndAddress>(?>[a-zA-Z0-9\\/\\-?:().,'+ ]{0,35}(?>\\n[a-zA-Z0-9\\/\\-?:().,'+ ]{0,35}){0,9}))";
		String input = ":ABCD//min tekst";
		
		matchTest(input, regex);
		
	}

	public static void matchTest(String input, String regex) {

		Pattern fieldDescriptionPattern = Pattern.compile(regex, Pattern.MULTILINE);
		Matcher fieldDescriptionMatcher = fieldDescriptionPattern.matcher(input);

				
		System.out.println("Input: " + input);
		System.out.println("Regex: " + regex);
		System.out.println("\nMatch: " + fieldDescriptionMatcher.find());

	}

}
