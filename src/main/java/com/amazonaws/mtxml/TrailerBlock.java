package com.amazonaws.mtxml;

/*
 * https://www.paiementor.com/swift-mt-message-block-5-trailers-description/
 */
public class TrailerBlock extends AbstractBlock implements MTComponent {

	TrailerBlock(String content) {
		super(content, "5");
	}

	@Override
	String getXmlNodeName() {
		return "TrailerBlock";
	}

}
