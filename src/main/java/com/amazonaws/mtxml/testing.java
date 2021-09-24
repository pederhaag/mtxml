package com.amazonaws.mtxml;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class testing {

	public static void main(String[] args) {
		MTComponent test = new AbstractMTComponent() {

			@Override
			public String toXml() {
				return "<Root/>";
			}
			
		};

		System.out.println(test);
		System.out.println(test.toXml());
	}

}
