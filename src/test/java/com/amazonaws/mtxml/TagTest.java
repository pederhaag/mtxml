package com.amazonaws.mtxml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.amazonaws.mtxml.utils.XmlFactory;
import com.amazonaws.test.utils.TestCases;
import com.amazonaws.test.utils.TestingUtils;

import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvReader.CsvReaderBuilder;
import de.siegmar.fastcsv.reader.CsvRow;

class TagTest {
	private static TagFactory factory;

	private static ArrayList<Map<String, String>> validTags;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		factory = new TagFactory();
		validTags = TestCases.getValidTags();
	}

	@ParameterizedTest
	@MethodSource("validTags")
	void testTag(Map<String, String> tagData) throws UnknownTagException {
		String rawTagContent = tagData.get("RawContent");
		factory.createTag(tagData.get("Tag"), rawTagContent.replace("\\n", "\n"));
	}

	@ParameterizedTest
	@MethodSource("SyntaxExceptions")
	void testTagSyntaxExceptions(String tag, String content) throws UnknownTagException {
		assertThrows(MTSyntaxException.class, () -> factory.createTag(tag, content));
	}

	@ParameterizedTest
	@MethodSource("UnknownTags")
	void testTagSyntaxExceptions(String tag) throws UnknownTagException {
		assertThrows(UnknownTagException.class, () -> factory.createTag(tag, "Somethingsomething"));
	}

	@ParameterizedTest
	@MethodSource("validTags")
	void testGetFieldValue(Map<String, String> tagData) throws UnknownTagException {
		String rawTagContent = tagData.get("RawContent");
		Tag tag = factory.createTag(tagData.get("Tag"), rawTagContent.replace("\\n", "\n"));

		for (String field : tagData.keySet()) {
			if (!field.equals("RawContent")) {
				String ExpectedValue = tagData.get(field).replace("\\n", "\n");
				assertEquals(ExpectedValue, tag.getFieldValue(field),
						String.format("Expected field '%s' to be equal to %s but got %s instead", field, ExpectedValue,
								tag.getFieldValue(field)));
			}
		}

	}

	@ParameterizedTest
	@MethodSource("validTags")
	void testToXml(Map<String, String> tagData) throws UnknownTagException {
		String rawTagContent = tagData.get("RawContent");
		String tagName = tagData.get("Tag");
		Tag tag = factory.createTag(tagName, rawTagContent.replace("\\n", "\n"));
		String testXml = tag.toXml();
		String controlXml;

		if (tagData.containsKey("Qualifier")) {

			controlXml = XmlFactory.openNode("Tag" + tagName, "Qualifier", tagData.get("Qualifier"));
		} else {
			controlXml = XmlFactory.openNode("Tag" + tagName);
		}
		for (String field : tagData.keySet()) {

			if (!field.equals("RawContent") && !field.equals("Qualifier") && !field.equals("Tag")) {
				String fieldContent = tagData.get(field);
				if (fieldContent.contains("\\n"))
					fieldContent = MultilineToXml(fieldContent);
				controlXml += XmlFactory.writeNode(field, fieldContent);
			}
		}
		controlXml += XmlFactory.closeNode("Tag" + tagName);

		TestingUtils.assertXMLEqual(testXml, controlXml);
	}

	/*
	 * Helper methods
	 */
	private static String MultilineToXml(String input) {
		String output = "";
		for (String line : input.split("\\\\n")) {
			output += XmlFactory.writeNode("Line", line);
		}
		return output;
	}

	/*
	 * Argument providers
	 */
	private static Stream<Map<String, String>> validTags() {
		return validTags.stream();
	}

	private static Stream<Arguments> SyntaxExceptions() {
		Stream.Builder<Arguments> builder = Stream.builder();

		builder.add(Arguments.arguments("11A", "123"));
		builder.add(Arguments.arguments("32E", "ED"));
		builder.add(Arguments.arguments("32E", "ED"));

		// Invalid numeric fields
		builder.add(Arguments.arguments("34B", "NOK123."));
		builder.add(Arguments.arguments("34B", "NOK123,,"));
		builder.add(Arguments.arguments("34B", "NOK12,3,"));
		builder.add(Arguments.arguments("34B", "NOK,"));

		return builder.build();
	}

	private static Stream<String> UnknownTags() {
		Stream.Builder<String> builder = Stream.builder();

		builder.add("99C");
		builder.add("91F");
		builder.add("1X");

		return builder.build();
	}

}
