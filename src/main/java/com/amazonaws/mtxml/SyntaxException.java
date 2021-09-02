package com.amazonaws.mtxml;

public class SyntaxException extends IllegalArgumentException {
	private static final long serialVersionUID = 1L;

	SyntaxException(String pattern, String where) {
		super(String.format("Syntax error! Content in %s needs to match %s", where, pattern));
	}

	SyntaxException(String message) {
		super(String.format("Syntax error! %s", message));
	}

}
