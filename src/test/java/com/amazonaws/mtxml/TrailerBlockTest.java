package com.amazonaws.mtxml;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.amazonaws.test.utils.TestCases;

class TrailerBlockTest extends TagBlockTest {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		blockIdentifier = "5";
		invalidBlocks = TestCases.getInvalidTrailerBlockTestCases();

		// Valid blocks
		validBlocks = TestCases.getValidTrailerBlockTestCases();
	}

	@Override
	AbstractBlock createBlock(String content) {
		return new TrailerBlock(content);
	}

	@Test
	void testToXml() {
		fail("Not yet implemented.");
	}

}
