package com.amazonaws.mtxml;

/**
 * Exception used to indicate that a method is tasked with working with a tag
 * that it does not know - i.e. not part of the MT standard
 */
public class UnknownTagException extends Exception {
	private static final long serialVersionUID = 1L;

	UnknownTagException(String inputTag) {
		super(String.format("Unknown tag %s!", inputTag));
	}
}
