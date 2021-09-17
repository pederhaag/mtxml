package com.amazonaws.mtxml;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import de.siegmar.fastcsv.reader.CommentStrategy;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;
import de.siegmar.fastcsv.reader.CsvReader.CsvReaderBuilder;

abstract class TagBlockTest {
	protected static Map<String, ArrayList<String[]>> validBlocks;
	protected static String[] invalidBlocks;
	protected static String blockIdentifier;

	private static final String testFolderPath = new File("src/test").getAbsolutePath();
	private static final String testResourcesPath = new File(testFolderPath + "/resources").getAbsolutePath();

	abstract AbstractBlock createBlock(String content);

	protected static void initValidBlocks(String[] validBlocksInputs) throws IOException {
		validBlocks = new HashMap<String, ArrayList<String[]>>();
		// Read blockdata from csv files
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
			block = String.format("{" + blockIdentifier + ":%s}", block);

			validBlocks.put(block, blockTags);
		}
	}

	/*
	 * Valid constructors
	 */
	@ParameterizedTest
	@MethodSource("validBlocks")
	void testBlock(String block) {
		createBlock(block);
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
	void testBlockInvalid(String block) {
		assertThrows(SyntaxException.class, () -> createBlock(block));
	}

	private static Stream<String> invalidBlocks() {
		Stream.Builder<String> sb = Stream.builder();
		for (String block : invalidBlocks) {
			sb.add(block);
		}
		return sb.build();
	}

	private static Stream<Arguments> blocksWithTags() {
		Stream.Builder<Arguments> sb = Stream.builder();
		for (String block : validBlocks.keySet()) {
			sb.add(Arguments.arguments(block, validBlocks.get(block)));
		}
		return sb.build();
	}

	/*
	 * Null constructors
	 */
	@Test
	void testBlockNull() {
		assertThrows(NullPointerException.class, () -> createBlock(null));
	}

	/*
	 * getTag(String tag)
	 */
	@ParameterizedTest
	@MethodSource("blocksWithTags")
	void testGetTagString(String blockString, ArrayList<String[]> tags) {
		AbstractBlock block = createBlock(blockString);
		for (String[] tagValuePair : tags) {
			String tag = tagValuePair[0];
			String tagValue = tagValuePair[1];
			assertEquals(block.getTag(tag), tagValue);
		}
		assertEquals(block.getTag("BlockIdentifier"), blockIdentifier);
	}

	/*
	 * getTag(int index)
	 */
	@ParameterizedTest
	@MethodSource("blocksWithTags")
	void testGetTagInt(String blockString, ArrayList<String[]> tags) {
		AbstractBlock block = createBlock(blockString);
		for (int index = 0; index < tags.size(); index++) {
			String[] tagValuePair = tags.get(index);
			String tagValue = tagValuePair[1];
			assertEquals(block.getTag(index), tagValue);
		}
	}

	@Test
	void testToXml() {
		fail("Needs to be implemented in subclass.");
	}

	@ParameterizedTest
	@MethodSource("validBlocks")
	void testGetTagStringNull(String blockContent) {
		AbstractBlock block = createBlock(blockContent);
		assertThrows(NullPointerException.class, () -> block.getTag(null));
	}

	@ParameterizedTest
	@MethodSource("validBlocks")
	void testGetTagStringEmpty(String blockContent) {
		AbstractBlock block = createBlock(blockContent);
		assertThrows(IllegalArgumentException.class, () -> block.getTag(""));
	}

	@ParameterizedTest
	@MethodSource("validBlocks")
	void testGetTagIntInvalid(String blockContent) {
		AbstractBlock block = createBlock(blockContent);
		assertThrows(IndexOutOfBoundsException.class, () -> block.getTag(-3));
	}

}
