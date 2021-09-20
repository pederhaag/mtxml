package com.amazonaws.mtxml;

import java.util.Objects;

/**
 * Class for modelling the individual tags in headers of the form {@code {t_i:v_i}}
 * occurring in {@code TrailerHeaderBlock} and {@code UserHeaderBlock} classes
 */
class HeaderTag {
	final String tag;
	final String value;

	HeaderTag(String tag, String value) {
		Objects.requireNonNull(tag, "Tag cannot be null");
		Objects.requireNonNull(value, "Tagvalue cannot be null");

		if (tag.equals("")) {
			String msg = String.format("Tag (%s) must be non-empty", tag, value);
			throw new IllegalArgumentException(msg);
		}

		this.tag = tag;
		this.value = value;
	}

}
