package com.amazonaws.mtxml;

/*
 * https://www.paiementor.com/swift-mt-message-block-3-user-header-description/
 */
public class UserHeaderBlock extends AbstractBlock implements MTComponent {

	UserHeaderBlock(String content) {
		super(content, "3");
	}

	@Override
	String getXmlNodeName() {
		return "UserHeaderBlock";
	}
}
