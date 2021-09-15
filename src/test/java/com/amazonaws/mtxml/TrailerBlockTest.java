package com.amazonaws.mtxml;

import org.junit.jupiter.api.BeforeAll;

class TrailerBlockTest extends TagBlockTest {
	private static final String[] validBlocksInputs = { "trailerblockinput1.csv", "trailerblockinput2.csv" };

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		blockIdentifier = "5";
		invalidBlocks = new String[] { "{?:{123:321}{999:000}}", "{3:}", "" };

		// Valid blocks
		initValidBlocks(validBlocksInputs);
	}

	@Override
	AbstractBlock createBlock(String content) {
		return new TrailerBlock(content);
	}

}
