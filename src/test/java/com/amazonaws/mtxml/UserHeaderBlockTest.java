package com.amazonaws.mtxml;

import org.junit.jupiter.api.BeforeAll;

class UserHeaderBlockTest extends TagBlockTest {
	private static final String[] validBlocksInputs = { "userheaderblockinput1.csv", "userheaderblockinput2.csv" };

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		blockIdentifier = "3";
		invalidBlocks = new String[] {"{1:{123:321}{999:000}}", "{3:}", ""};
		
		// Valid blocks
		initValidBlocks(validBlocksInputs);
	}

	@Override
	AbstractBlock createBlock(String content) {
		return new UserHeaderBlock(content);
	}

}
