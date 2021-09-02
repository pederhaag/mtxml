package com.amazonaws.mtxml;

public class UserHeaderTag {
	final String tag;
	final String value;

	UserHeaderTag(String tag, String value) {
		this.tag = tag;
		this.value = value;

	}

	public static void main(String[] args) {
		UserHeaderTag x = new UserHeaderTag("103", "TGT");

	}

}
