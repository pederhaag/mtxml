package com.amazonaws.mtxml;

public class SyntaxException extends IllegalArgumentException {
	private static final long serialVersionUID = 1L;

	SyntaxException(String pattern, String tag) {
		super(String.format("Syntax error! Content in %s needs to match %s", tag, pattern));
	}

	SyntaxException(String message) {
		super(String.format("Syntax error! %s", message));
	}

}
