package com.amazonaws.mtxml;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import de.siegmar.fastcsv.reader.CommentStrategy;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvReader.CsvReaderBuilder;
import de.siegmar.fastcsv.reader.CsvRow;

class UserHeaderBlockTest {

	private static Map<String, ArrayList<String[]>> validBlocks = new HashMap<String, ArrayList<String[]>>();
//	private static String[] validBlocksArray;
	private static final String[] validBlocksInputs = { "userheaderblockinput1.csv", "userheaderblockinput2.csv" };

	private static final String invalidBlockSyntax1 = "{2:{103:TGT}{108:OPTUSERREF16CHAR}}";
	private static final String invalidBlockSyntax2 = "{3:{111:}{22222222:123-12333}{321:/FPO}{2:SZ2}}";
	private static final String invalidBlockSyntax3 = "{3:}";
	private static final String[] invalidBlocksSyntax = { invalidBlockSyntax1, invalidBlockSyntax2,
			invalidBlockSyntax3 };

	private static final String testFolderPath = new File("src/test").getAbsolutePath();
	private static final String testResourcesPath = new File(testFolderPath + "/resources").getAbsolutePath();

	@BeforeAll
	static void setUpBeforeClass() throws IOException {
		int numTestCases = validBlocksInputs.length;

		// Valid blocks
		for (String testcase : validBlocksInputs) {
			String filepath = testResourcesPath + "/" + testcase;

			CsvReaderBuilder builder = CsvReader.builder().commentStrategy(CommentStrategy.SKIP);
			CsvReader csv = builder.build(new File(filepath).toPath(), Charset.defaultCharset());

			String block = "";
			ArrayList<String[]> blockTags = new ArrayList<String[]>();
			for (CsvRow row : csv) {
				String[] tagPair = row.getFields().stream().toArray(String[]::new);

				blockTags.add(tagPair);
				block += String.format("{%s:%s}", tagPair[0], tagPair[1]);
			}
			block = String.format("{3:%s}", block);

			validBlocks.put(block, blockTags);
//			validBlocksArray = validBlocks.keySet().toArray(String[]::new);
		}
	}

	/*
	 * Valid constructors
	 */
	@ParameterizedTest
	@MethodSource("validBlocks")
	void testUserHeaderBlock(String block) {
		new UserHeaderBlock(block);
	}

	private static Stream<String> validBlocks() {
		Stream.Builder<String> sb = Stream.builder();
		for (String block : validBlocks.keySet()) {
			sb.add(block);
		}
		return sb.build();
	}

	/*
	 * Invalid constructors
	 */
	@ParameterizedTest
	@MethodSource("invalidBlocks")
	void testUserHeaderBlockInvalid(String block) {
		assertThrows(SyntaxException.class, () -> new UserHeaderBlock(block));
	}

	private static Stream<String> invalidBlocks() {
		Stream.Builder<String> sb = Stream.builder();
		sb.add("{1:{123:321}{999:000}}");
		sb.add("{3:}");
		sb.add("");
		return sb.build();
	}

	/*
	 * Null constructors
	 */
	@Test
	void testUserHeaderBlockNull() {
		assertThrows(NullPointerException.class, () -> new UserHeaderBlock(null));
	}

	@Test
	void testToXml() {
		fail("Not yet implemented");
	}

	/*
	 * getTag(String tag)
	 */
	@ParameterizedTest
	@MethodSource("blocksWithTags")
	void testGetTagString(String blockString, ArrayList<String[]> tags) {
		UserHeaderBlock block = new UserHeaderBlock(blockString);
		for (String[] tagValuePair : tags) {
			String tag = tagValuePair[0];
			String tagValue = tagValuePair[1];
			assertEquals(block.getTag(tag), tagValue);
		}
		assertEquals(block.getTag("BlockIdentifier"), "3");
	}

	/*
	 * getTag(int index)
	 */
	@ParameterizedTest
	@MethodSource("blocksWithTags")
	void testGetTagInt(String blockString, ArrayList<String[]> tags) {
		UserHeaderBlock block = new UserHeaderBlock(blockString);
		for (int index = 0; index < tags.size(); index++) {
			String[] tagValuePair = tags.get(index);
			String tagValue = tagValuePair[1];
			assertEquals(block.getTag(index), tagValue);
		}
	}

	private static Stream<Arguments> blocksWithTags() {
		Stream.Builder<Arguments> sb = Stream.builder();
		for (String block : validBlocks.keySet()) {
			sb.add(Arguments.arguments(block, validBlocks.get(block)));
		}
		return sb.build();
	}

	@Test
	void testGetTagStringNull() {
		UserHeaderBlock block = new UserHeaderBlock("{3:{123:456}}");
		assertThrows(NullPointerException.class, () -> block.getTag(null));
	}

	@Test
	void testGetTagStringEmpty() {
		UserHeaderBlock block = new UserHeaderBlock("{3:{123:456}}");
		assertThrows(IllegalArgumentException.class, () -> block.getTag(""));
	}

	@Test
	void testGetTagIntInvalid() {
		UserHeaderBlock block = new UserHeaderBlock("{3:{123:456}}");
		assertThrows(IndexOutOfBoundsException.class, () -> block.getTag(-3));
	}
}
