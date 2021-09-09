package com.amazonaws.mtxml;

/*
 * https://www.paiementor.com/swift-mt-message-block-5-trailers-description/
 */
public class TrailerBlock extends TagBlock implements MTComponent {

	TrailerBlock(String content) {
		super(content, "5");
	}
	
	public static void main(String[] args) {
		new TrailerBlock("{5:{CHK:123456789ABC}{PDE:1348120811BANKFRPPAXXX2222123456}{DLM:}}");
	}

}
