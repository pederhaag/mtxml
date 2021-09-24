package com.amazonaws.mtxml;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.amazonaws.test.utils.TestCases;
import com.amazonaws.test.utils.TestingUtils;

class TextBlockTest {
	private static ArrayList<String[]> validTextBlocks;
	private static TagFactory factory;

	@BeforeAll
	static void setUpBeforeClass() throws IOException {
		factory = new TagFactory();
		validTextBlocks = TestCases.getValidTextBlockTestCases();
	}

	@ParameterizedTest
	@MethodSource("validBlocks")
	final void testTextBlock(String blockContent, String ctrlXmlContent) throws UnknownTagException {
		new TextBlock(blockContent, factory);
	}

	@Test
	public void testTextBlockEmpty() throws UnknownTagException {
		assertThrows(MTSyntaxException.class, () -> new TextBlock("", factory));
	}

	@Test
	public void testTextBlockEmptyContent() throws UnknownTagException {
		new TextBlock("{4:\r\n" + "-}", factory);
	}

	@ParameterizedTest
	@MethodSource("invalidBlocksSyntaxException")
	final void testTextBlockSyntaxException(String invalidBlock) throws UnknownTagException {
		assertThrows(MTSyntaxException.class, () -> new TextBlock(invalidBlock, factory));
	}

	@ParameterizedTest
	@MethodSource("validBlocks")
	final void testToXml(String blockContent, String ctrlXmlContent) throws UnknownTagException {
		TestingUtils.assertXMLEqualIgnoreFormat(new TextBlock(blockContent, factory).toXml(), ctrlXmlContent);
	}

	@Test
	public void testTextBlockNullContent() {
		assertThrows(NullPointerException.class, () -> new TextBlock(null, factory));
		assertThrows(NullPointerException.class, () -> new TextBlock(null));
	}

	@Test
	public void testTextBlockNullFactory() {
		assertThrows(NullPointerException.class, () -> new TextBlock("{4:\n" + ":20C:ABC123" + "\n-}", null));
	}

	private static Stream<Arguments> validBlocks() throws FileNotFoundException {
		Stream.Builder<Arguments> builder = Stream.builder();
		for (String[] testcase : validTextBlocks) {
			builder.add(Arguments.arguments(testcase[0], testcase[1]));
		}
		return builder.build();
	}

	private static Stream<String> invalidBlocksSyntaxException() {
		return TestCases.getInvalidTextBlockTestCases().stream();
	}
}
