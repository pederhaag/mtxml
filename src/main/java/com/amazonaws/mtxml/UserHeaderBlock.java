package com.amazonaws.mtxml;

import java.util.ArrayList;

/*
 * https://www.paiementor.com/swift-mt-message-block-3-user-header-description/
 */
public class UserHeaderBlock extends TagBlock implements MTComponent {
	static String BlockIdentifier = "3";

	private ArrayList<UserHeaderTag> tags;

	UserHeaderBlock(String content) {
		super(content, BlockIdentifier);
	}

	public static void main(String[] args) {
		UserHeaderBlock block = new UserHeaderBlock("{3:{103:TGT}{108:OPTUSERREF16CHAR}}");
	}

	@Override
	public String toXml() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	void addTag(String tag, String value) {
		tags.add(new UserHeaderTag(tag, value));

	}

	@Override
	void initTagCollection() {
		tags = new ArrayList<UserHeaderTag>();
	}

}
