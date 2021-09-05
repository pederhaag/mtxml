package com.amazonaws.mtxml;

/*
 * https://www.paiementor.com/swift-mt-message-block-3-user-header-description/
 */
public class UserHeaderBlock extends TagBlock implements MTComponent {

	UserHeaderBlock(String content) {
		super(content, "3");
	}

}
