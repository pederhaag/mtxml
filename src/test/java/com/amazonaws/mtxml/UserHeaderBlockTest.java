package com.amazonaws.mtxml;

import java.util.ArrayList;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.amazonaws.mtxml.utils.XmlFactory;
import com.amazonaws.test.utils.TestCases;
import com.amazonaws.test.utils.TestingUtils;

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

	@ParameterizedTest
	@MethodSource("blocksWithXml")
	void testToXml(String block, String ctrlXml) {
		TestingUtils.assertXMLEqualIgnoreFormat(new UserHeaderBlock(block).toXml(), ctrlXml);
		;
	}

	private static Stream<Arguments> blocksWithXml() {
		Stream.Builder<Arguments> sb = Stream.builder();
		for (String block : validBlocks.keySet()) {
			sb.add(Arguments.arguments(block, expectedXml(validBlocks.get(block))));
		}
		return sb.build();
	}

	private static String expectedXml(ArrayList<String[]> tags) {
		StringBuilder sb = new StringBuilder();
		sb.append(XmlFactory.openNode("UserHeader"));
		for (String[] tag : tags) {
			sb.append(XmlFactory.openNode("UserTag"));
			sb.append(XmlFactory.writeNode("Tag", tag[0]));
			sb.append(XmlFactory.writeNode("Contents", tag[1]));
			sb.append(XmlFactory.closeNode("UserTag"));
		}
		sb.append(XmlFactory.closeNode("UserHeader"));
		return sb.toString();
	}

}
