package com.amazonaws.mtxml;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * {@code AbstractBlock} models blocks on the for
 * {@code {a:{t_1:v_1}{t_2:v_2}...}} for some blockidentifier {@code a} and
 * sequence of tag-value pairs of the form {@code {t_i:v_i}}.
 *
 */
public abstract class AbstractBlock extends MTTree {
	/**
	 * Regex pattern for identifying the tag-value pairs of the form
	 * {@code {t_i:v_i}}
	 */
	private final static String REGEX_PATTERN_TAG = "\\{(.*?):(.*?)\\}";

	/**
	 * Regex pattern for identyfying the sequence of tag-value pairs
	 */
	private final static String REGEX_PATTERN_TAG_SEQUENCE = "(?>\\{.*?\\})+$";

	private final String blockIdentifier;

	AbstractBlock(String content, String blockName, String BlockIdentifier) {
		super(blockName, null);
		this.blockIdentifier = BlockIdentifier;

		// Regex pattern for splitting out the blockidentifier and the contents
		String regexPattern = "\\{(" + BlockIdentifier + "):(.*)\\}$";

		// Match the block
		Pattern blockPattern = Pattern.compile(regexPattern, Pattern.MULTILINE);
		Matcher blockmatcher = blockPattern.matcher(content);

		if (!blockmatcher.find()) {
			throw new MTSyntaxException(regexPattern, content);
		}

		String tagString = blockmatcher.group(2);

		// Matching each tag
		Pattern tagListPattern = Pattern.compile(REGEX_PATTERN_TAG_SEQUENCE);
		Matcher tagListMatcher = tagListPattern.matcher(tagString);
		if (!tagListMatcher.matches()) {
			throw new MTSyntaxException(REGEX_PATTERN_TAG_SEQUENCE, tagString);
		}

		// Search for tags
		Pattern tagPattern = Pattern.compile(REGEX_PATTERN_TAG);
		Matcher tagMatcher = tagPattern.matcher(tagString);

		// Add found tags
		String xmlNodeName = getXmlTagNodeName();
		String xmlNodeCodeName = getXmlTagCodeNodeName();
		String xmlNodeValueName = getXmlTagValueNodeName();
		while (tagMatcher.find()) {
			String tag = tagMatcher.group(1);
			String value = tagMatcher.group(2);
			addComponent("Tag" + tag, new HeaderTag(tag, value, xmlNodeName, xmlNodeCodeName, xmlNodeValueName));
		}
	}

	/**
	 * Method needed for constructing naming xml-nodes: The root node of each tag
	 * xml
	 */
	abstract String getXmlTagNodeName();

	/**
	 * Method needed for constructing naming xml-nodes: The node containing the code
	 * of the tag
	 */
	abstract String getXmlTagCodeNodeName();

	/**
	 * Method needed for constructing naming xml-nodes: The node containing the
	 * value of the tag
	 */
	abstract String getXmlTagValueNodeName();

	/**
	 * Simple getter for fields in block. {@code AbstractBlock} implementation returns
	 * the blockidentifier, else calls parent method.
	 */
	@Override
	public String getData(String fieldName) {
		if (fieldName.equals(""))
			throw new IllegalArgumentException("Fieldname cannot be an empty string");

		if (fieldName.equals("BlockIdentifier")) {
			return blockIdentifier;
		}
		return super.getData(fieldName);
	}

}
