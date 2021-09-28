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

	@ParameterizedTest
	@MethodSource("blocksWithXml")
	void testToXml(String block, String ctrlXml) {
		TestingUtils.assertXMLEqualIgnoreFormat(new TrailerBlock(block).toXml(), ctrlXml);
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
		sb.append(XmlFactory.openNode("TrailerInformation"));
		for (String[] tag : tags) {
			sb.append(XmlFactory.openNode("Trailer"));
			sb.append(XmlFactory.writeNode("Code", tag[0]));
			sb.append(XmlFactory.writeNode("TrailerInformation", tag[1]));
			sb.append(XmlFactory.closeNode("Trailer"));
		}
		sb.append(XmlFactory.closeNode("TrailerInformation"));
		return sb.toString();
	}

}
