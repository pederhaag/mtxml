package com.amazonaws.mtxml;

/**
 * 
 * {@code MTSyntaxException} is an exception indicating that an input does not
 * confirm to the format set by the MT Standard and is therefore invalid as
 * input.
 * 
 */
public class MTSyntaxException extends IllegalArgumentException {
	private static final long serialVersionUID = 1L;

	MTSyntaxException(String pattern, String tag) {
		super(String.format("Syntax error! Content in %s needs to match %s", tag, pattern));
	}

	MTSyntaxException(String message) {
		super(String.format("Syntax error! %s", message));
	}

}
