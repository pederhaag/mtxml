package com.amazonaws.mtxml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * This class models the Textblock of a MT message
 *
 */
public class TextBlock extends MTTree {
	/**
	 * Regex for matching the textblock content
	 */
	private final static String REGEX_PATTERN_TEXTBLOCK = "\\{4:\\r?\\n(?<TagContent>(?>.|\\r|\\n)*)?\\n?-\\}$";

	/**
	 * Regex for matching the different tags and their contents
	 */
	private final static String REGEX_PATTERN_TAGS = ":(?<Tag>[\\dA-Z]+):(?<Content>(?>:)?(?>.|\\R)*?(?=\\R:|\\R-\\}))";

	TextBlock(String content) throws IOException, UnknownTagException {
		this(content, new TagFactory());
	}

	TextBlock(String content, TagFactory factory) throws UnknownTagException {
		super("TextBlock", null);

		// Get the block content
		Objects.requireNonNull(content, "Block content cannot be null");
		Pattern pattern = Pattern.compile(REGEX_PATTERN_TEXTBLOCK, Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(content);

		// Make sure that the syntax is correct
		if (!matcher.find()) {
			throw new MTSyntaxException(REGEX_PATTERN_TEXTBLOCK, content);
		}

		// Identify tags and blocks
		Pattern patternTags = Pattern.compile(REGEX_PATTERN_TAGS, Pattern.MULTILINE);
		Matcher matcherTags = patternTags.matcher(content);

		// This container is used see at which dept the loop is currently working on.
		// The first entry will be this object.
		ArrayList<MTTree> openBlocks = new ArrayList<MTTree>();
		openBlocks.add(this);

		// Used in ensuring unique keys for block-components
		int numBlocks = 0;

		// Loop through the tags/blocks
		while (matcherTags.find()) {
			String tagName = matcherTags.group("Tag");
			String tagContent = matcherTags.group("Content");

			if (tagName.equals("16R")) {
				// Start of a new block
				TagBlock newBlock = new TagBlock(tagContent);
				openBlocks.get(openBlocks.size() - 1).addComponent("TagBlock" + numBlocks++, newBlock);
				openBlocks.add(newBlock);

			} else if (tagName.equals("16S")) {
				// Remove last block in list
				openBlocks.remove(openBlocks.size() - 1);

			} else if (!tagName.startsWith("16")) {
				// Regular tag: Add the tag to the block the parser is currently processing
				openBlocks.get(openBlocks.size() - 1).addComponent("TagBlock" + numBlocks++,
						factory.createTag(tagName, tagContent));
			} else {
				throw new UnknownTagException(tagName);
			}
		}

	}

}
