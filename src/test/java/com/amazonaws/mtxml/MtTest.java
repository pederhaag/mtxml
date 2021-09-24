package com.amazonaws.mtxml;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.amazonaws.test.utils.TestCases;
import com.amazonaws.test.utils.TestingUtils;

class MtTest {

	@ParameterizedTest
	@MethodSource("validTextBlocks")
	void testMt(Map<String, String> testCase) throws IOException, UnknownTagException {
		new Mt(testCase.get("MT"));
	}

	@ParameterizedTest
	@MethodSource("validTextBlocks")
	void testToXml(Map<String, String> testCase) throws IOException, UnknownTagException {
		StringBuilder ctrlBuilder = new StringBuilder();
		ctrlBuilder.append(new BasicHeaderBlock(testCase.get("BasicHeaderBlock")).toXml());
		ctrlBuilder.append(new ApplicationHeaderBlock(testCase.get("ApplicationHeaderBlock")).toXml());
		ctrlBuilder.append(new UserHeaderBlock(testCase.get("UserHeaderBlock")).toXml());
		ctrlBuilder.append(new TextBlock(testCase.get("TextBlock")).toXml());
		ctrlBuilder.append(new TrailerBlock(testCase.get("TrailerBlock")).toXml());
		String ctrlXml = ctrlBuilder.toString();

		Mt mt = new Mt(testCase.get("MT"));

		TestingUtils.assertXMLEqualIgnoreFormat(mt.toXml(), ctrlXml);
	}

	private static Stream<Map<String, String>> validTextBlocks() throws Exception {
		return TestCases.getValidMtTestCases().stream();
	}
}
