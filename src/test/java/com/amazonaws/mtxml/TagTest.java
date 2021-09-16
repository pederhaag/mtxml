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
	private final static String[] numberFields = new String[] { "Amount", "Quantity", "Rate", "Price", "Balance" };

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
//			String tag
			tagData.put("RawContent", rawData.substring(tag.length() + 2));
			tagData.put("Tag", tag);
			for (CsvRow fieldRow : fieldReader) {

				// Get values of subfields
				for (String valuePair : fieldRow.getFields()) {
					String fieldName = valuePair.substring(0, valuePair.indexOf('='));
//					String tagName = fieldName.split("Field")[1];
					String fieldValue = valuePair.substring(valuePair.indexOf('=') + 1);
//					System.out.println(valuePair);
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
	void testTag(Map<String, String> tagData) throws UnknownTagException, MTException {
		String rawTagContent = tagData.get("RawContent");
		Tag tag = factory.createTag(tagData.get("Tag"), rawTagContent.replace("\\n", "\n"));
	}

	private static Stream<Map<String, String>> validTagsWithoutQualifier() {
		return validTagsWithoutQualifier.stream();
	}

	private static Stream<Map<String, String>> validTagsWithQualifier() {
		return validTagsWithQualifier.stream();
	}

	private static Stream<Map<String, String>> validTags() {
		return validTags.stream();
	}

	private static boolean isNumberField(String fieldName) {
		for (String numberField : numberFields) {
			if (fieldName.equals(numberField))
				return true;
		}
		return false;
	}

	private static Stream<Arguments> validTagsWithNumbers() {
		Stream.Builder<Arguments> builder = Stream.builder();
//        int tagNo = 0;
		for (int tagNo = 0; tagNo < validTags.size(); tagNo++) {
			builder.add(Arguments.of(tagNo, validTags.get(tagNo)));
		}
//        for (Map<String, String> tagData : validTags) {
//        	
//        }
		return builder.build();
	}

	@ParameterizedTest
	@MethodSource("validTags")
	void testGetFieldValue(Map<String, String> tagData) throws UnknownTagException, MTException {
		String rawTagContent = tagData.get("RawContent");
		Tag tag = factory.createTag(tagData.get("Tag"), rawTagContent.replace("\\n", "\n"));

		for (String field : tagData.keySet()) {
			if (!field.equals("RawContent")) {
				String ExpectedValue = tagData.get(field).replace("\\n", "\n");
				if (isNumberField(field)) {
					ExpectedValue = ExpectedValue.replace('.', ',');
				}
				assertEquals(ExpectedValue, tag.getFieldValue(field),
						String.format("Expected field '%s' to be equal to %s but got %s instead", field, ExpectedValue,
								tag.getFieldValue(field)));
			}
		}

	}

	@Test
	void testToXml() {
		fail("Not yet implemented"); // TODO
	}

}
