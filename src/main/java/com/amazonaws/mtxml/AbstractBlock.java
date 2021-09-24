package com.amazonaws.mtxml;

import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amazonaws.mtxml.utils.XmlFactory;

/**
 * 
 * {@code AbstractBlock} models blocks on the for
 * {@code {a:{t_1:v_1}{t_2:v_2}...}} for some blockidentifier {@code a} and
 * sequence of tag-value pairs of the form {@code {t_i:v_i}}.
 *
 */
public abstract class AbstractBlock implements MTComponent {
	/**
	 * Regex pattern for identifying the tag-value pairs of the form {@code
	 * {t_i:v_i}}
	 */
	// (?<UserHeader>\\{3:(?>\\{\\w*:[\\w\\/-]*\\})*\\})?
	private final static String REGEX_PATTERN_TAG = "\\{(.*?):(.*?)\\}";

	/**
	 * Regex pattern for identyfying the sequence of tag-value pairs
	 */
	private final static String REGEX_PATTERN_TAG_SEQUENCE = "(?>\\{.*?\\})+$";

	private final String blockIdentifier;

	/**
	 * Container for the tags
	 */
	private ArrayList<HeaderTag> tags;

	AbstractBlock(String content, String BlockIdentifier) {
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

		Pattern tagPattern = Pattern.compile(REGEX_PATTERN_TAG);
		Matcher tagMatcher = tagPattern.matcher(tagString);

		// Add found tags
		tags = new ArrayList<HeaderTag>();
		while (tagMatcher.find()) {
			addTag(tagMatcher.group(1), tagMatcher.group(2));
		}
	}

	public String toXml() {
		String xml = XmlFactory.openNode(getXmlNodeName());
		String nodeXmlContent;
		for (HeaderTag t : tags) {
			nodeXmlContent = XmlFactory.writeNode("Tag", t.tag) + XmlFactory.writeNode("Contents", t.value);
			xml += XmlFactory.writeNode(getXmlTagNodeName(t.tag), nodeXmlContent);
		}
		xml += XmlFactory.closeNode(getXmlNodeName());
		return xml;
	}

	/**
	 * Get the name of the root node to be used in a XML representation
	 */
	abstract String getXmlNodeName();
	abstract String getXmlTagNodeName(String tag);

	public String getBlockIdentifier() {
		return blockIdentifier;
	}

	private void addTag(String tag, String value) {
		tags.add(new HeaderTag(tag, value));
	}

	String getTag(String tag) {
		Objects.requireNonNull(tag, "Tag cannot be null");

		if (tag.equals("")) {
			throw new IllegalArgumentException("Tag must be a non-empty string.");
		}

		if (tag.equals("BlockIdentifier")) {
			return getBlockIdentifier();
		}
		for (HeaderTag t : tags) {
			if (t.tag.equals(tag)) {
				return t.value;
			}
		}
		return null;
	}

	String getTag(int index) {
		return tags.get(index).value;
	}

}
