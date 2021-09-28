package com.amazonaws.mtxml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

abstract class TagBlockTest {
	protected static Map<String, ArrayList<String[]>> validBlocks;
	protected static String[] invalidBlocks;
	protected static String blockIdentifier;

	abstract AbstractBlock createBlock(String content);

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
		assertThrows(MTSyntaxException.class, () -> createBlock(block));
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
			assertEquals(block.getData("Tag" + tag), tagValue);
		}
		assertEquals(block.getData("BlockIdentifier"), blockIdentifier);
	}

	/*
	 * getTag(int index)
	 */
	@ParameterizedTest
	@MethodSource("blocksWithTags")
	void testGetDataInt(String blockString, ArrayList<String[]> tags) {
		AbstractBlock block = createBlock(blockString);
		for (int index = 0; index < tags.size(); index++) {
			String[] tagValuePair = tags.get(index);
			String tagValue = tagValuePair[1];
			assertEquals(block.getData(index), tagValue);
		}
	}

	@Test
	abstract void testToXml(String block, String ctrlXml);

	@ParameterizedTest
	@MethodSource("validBlocks")
	void testGetDataStringNull(String blockContent) {
		AbstractBlock block = createBlock(blockContent);
		assertThrows(NullPointerException.class, () -> block.getData(null));
	}

	@ParameterizedTest
	@MethodSource("validBlocks")
	void testGetDataStringEmpty(String blockContent) {
		AbstractBlock block = createBlock(blockContent);
		assertThrows(IllegalArgumentException.class, () -> block.getData(""));
	}

	@ParameterizedTest
	@MethodSource("validBlocks")
	void testGetDataIntInvalid(String blockContent) {
		AbstractBlock block = createBlock(blockContent);
		assertThrows(IndexOutOfBoundsException.class, () -> block.getData(-3));
	}

}
