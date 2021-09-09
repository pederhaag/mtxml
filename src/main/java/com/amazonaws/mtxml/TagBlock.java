package com.amazonaws.mtxml;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class TagBlock implements MTComponent {
	private final static String regexPatternTag = "\\{(.*?):(.*?)\\}";
	private final static String regexPatternTagSequence = "(?>\\{.*?\\})+$";
	private String blockIdentifier;

	private ArrayList<HeaderTag> tags;

	TagBlock(String content, String BlockIdentifier) {
		this.blockIdentifier = BlockIdentifier;
		String regexPattern = "\\{(" + BlockIdentifier + "):(.*)\\}$";

		Pattern blockPattern = Pattern.compile(regexPattern, Pattern.MULTILINE);
		Matcher blockmatcher = blockPattern.matcher(content);

		if (!blockmatcher.find()) {
			throw new SyntaxException(regexPattern, content);
		}
		BlockIdentifier = blockmatcher.group(1);
		String tagString = blockmatcher.group(2);

		Pattern tagListPattern = Pattern.compile(regexPatternTagSequence);
		Matcher tagListMatcher = tagListPattern.matcher(tagString);
		if (!tagListMatcher.matches()) {
			throw new SyntaxException(regexPatternTagSequence, tagString);
		}

		Pattern tagPattern = Pattern.compile(regexPatternTag);
		Matcher tagMatcher = tagPattern.matcher(tagString);

		tags = new ArrayList<HeaderTag>();
		while (tagMatcher.find()) {
			addTag(tagMatcher.group(1), tagMatcher.group(2));
		}
	}

	@Override
	public String toXml() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getBlockIdentifier() {
		return blockIdentifier;
	}

	void addTag(String tag, String value) {
		tags.add(new HeaderTag(tag, value));
	}

	String getTag(String tag) {
		if (tag == null) {
			throw new NullPointerException("Tag cannot be null.");
		}
		if (tag.equals("")) {
			throw new IllegalArgumentException("Tag must be a non-empty string.");
		}

		if (tag.equals("BlockIdentifier")) {
			return blockIdentifier;
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
