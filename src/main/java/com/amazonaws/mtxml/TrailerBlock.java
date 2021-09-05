package com.amazonaws.mtxml;

/*
 * https://www.paiementor.com/swift-mt-message-block-5-trailers-description/
 */
public class TrailerBlock extends TagBlock implements MTComponent {

	TrailerBlock(String content) {
		super(content, "5");
	}

}
