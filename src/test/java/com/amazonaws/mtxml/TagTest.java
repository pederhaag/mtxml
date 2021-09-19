package com.amazonaws.mtxml;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.xmlunit.assertj3.XmlAssert;

import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;
import de.siegmar.fastcsv.reader.CsvReader.CsvReaderBuilder;

class TagTest {
	private static TagFactory factory;

	private static final String testFolderPath = new File("src/test").getAbsolutePath();
	private static final String testResourcesPath = new File(testFolderPath + "/resources").getAbsolutePath();

	private final static String validTagFile = new File(testResourcesPath + "/tags/validTags.txt").getAbsolutePath();
	static ArrayList<Map<String, String>> validTagsWithQualifier = new ArrayList<Map<String, String>>();
	static ArrayList<Map<String, String>> validTagsWithoutQualifier = new ArrayList<Map<String, String>>();
	static ArrayList<Map<String, String>> validTags = new ArrayList<Map<String, String>>();

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		factory = new TagFactory();
		CsvReaderBuilder builder = CsvReader.builder().fieldSeparator(';');
		CsvReader reader = builder.build(new File(validTagFile).toPath(), Charset.defaultCharset());

		CsvReaderBuilder fieldBuilder = CsvReader.builder().fieldSeparator('|');

		for (CsvRow row : reader) {
			String rawData = row.getField(0);
			String tag = row.getField(1).split("Field")[1];
			String fieldValues = row.getField(2);
			CsvReader fieldReader = fieldBuilder.build(fieldValues);
			boolean hasQualifier;

			Map<String, String> tagData = new HashMap<String, String>();
			tagData.put("RawContent", rawData.substring(tag.length() + 2));
			tagData.put("Tag", tag);
			for (CsvRow fieldRow : fieldReader) {

				// Get values of subfields
				for (String valuePair : fieldRow.getFields()) {
					String fieldName = valuePair.substring(0, valuePair.indexOf('='));
					String fieldValue = valuePair.substring(valuePair.indexOf('=') + 1);
					tagData.put(fieldName, fieldValue);
				}
				hasQualifier = tagData.containsKey("Qualifier");
				if (hasQualifier) {
					validTagsWithQualifier.add(tagData);
				} else {

					validTagsWithoutQualifier.add(tagData);
				}
			}
		}
		validTags.addAll(validTagsWithQualifier);
		validTags.addAll(validTagsWithoutQualifier);

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

		XmlAssert.assertThat(testXml).and(controlXml).ignoreChildNodesOrder().areIdentical();
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

		return builder.build();
	}

}
