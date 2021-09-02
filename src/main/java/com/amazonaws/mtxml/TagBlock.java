package com.amazonaws.mtxml;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class TagBlock implements MTComponent {

//	static String regexPattern;
	private final static String regexPatternTag = "\\{(\\d*?):(.*?)\\}";
	private final static String regexPatternTagSequence = "(?>\\{.*?\\})+";

	TagBlock(String content, String BlockIdentifier) {
		String regexPattern = "\\{(" + BlockIdentifier + "):(.*)\\}";
		
		Pattern blockPattern = Pattern.compile(regexPattern);
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

		initTagCollection();
		while (tagMatcher.find()) {
			addTag(tagMatcher.group(1), tagMatcher.group(2));
		}
	}

	@Override
	public String toXml() {
		// TODO Auto-generated method stub
		return null;
	}

	abstract void addTag(String tag, String value);
	abstract void initTagCollection();

}
