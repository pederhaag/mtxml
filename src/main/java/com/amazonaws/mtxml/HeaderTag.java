package com.amazonaws.mtxml;

public class HeaderTag {
	final String tag;
	final String value;

	HeaderTag(String tag, String value) {
		if (tag == null || value == null) {
			String msg = String.format("Tag (%s) and value (%s) must be non-null", tag, value);
			throw new NullPointerException(msg);
		}
		if (tag.equals("") || value.equals("")) {
			String msg = String.format("Tag (%s) and value (%s) must be non-empty", tag, value);
			throw new IllegalArgumentException(msg);
		}

		this.tag = tag;
		this.value = value;

	}

}
