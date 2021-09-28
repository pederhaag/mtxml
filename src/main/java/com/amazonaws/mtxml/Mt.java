package com.amazonaws.mtxml;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * {@code Mt} class is a container describing a complete SWIFT MT message all
 * the way from block-level down to tag level.
 *
 */
public class Mt extends MTTree {

	/**
	 * Regex describing the format which an MT message much adheere to.
	 */
	private final static String REGEX_PATTERN = "^(?<BasicHeader>\\{1:\\w*\\})(?<ApplicationHeader>\\{2:\\w*\\})?(?<UserHeader>\\{3:(?>\\{\\w*:[\\w.\\/-]*\\})*\\})?(?<TextBlock>\\{4:\\R(?>.*\\R)*-\\})(?<TrailerBlock>\\{5:(?>\\{\\w*:\\w*\\})*\\})$";

	public Mt(String mtContent) throws IOException, UnknownTagException {
		super("SwiftMessaage", null);

		/**
		 * Match the input against expected regex
		 */
		Pattern pattern = Pattern.compile(REGEX_PATTERN);
		Matcher matcher = pattern.matcher(mtContent);

		if (!matcher.find()) {
			throw new MTSyntaxException(REGEX_PATTERN, mtContent);
		}

		/*
		 * Add the underlying blocks
		 */

		// Basic Header
		addComponent("BasicHeader", new BasicHeaderBlock(matcher.group("BasicHeader")));

		// Application Header
		addComponent("ApplicationHeader", new ApplicationHeaderBlock(matcher.group("ApplicationHeader")));

		// User Header
		addComponent("UserHeader", new UserHeaderBlock(matcher.group("UserHeader")));

		// Text block
		addComponent("TextBlock", new TextBlock(matcher.group("TextBlock")));

		// Trailer block
		addComponent("TrailerBlock", new TrailerBlock(matcher.group("TrailerBlock")));

	}

}
