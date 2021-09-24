package com.amazonaws.mtxml;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.amazonaws.test.utils.TestCases;

class UserHeaderBlockTest extends TagBlockTest {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		blockIdentifier = "3";
		invalidBlocks = TestCases.getInvalidUserHeaderBlockTestCases();

		// Valid blocks
		validBlocks = TestCases.getValidUserHeaderBlockTestCases();
	}

	@Override
	AbstractBlock createBlock(String content) {
		return new UserHeaderBlock(content);
	}

	@Test
	void testToXml() {
		fail("Not yet implemented.");
	}

}
