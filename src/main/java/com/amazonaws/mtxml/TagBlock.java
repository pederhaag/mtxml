package com.amazonaws.mtxml;

import java.util.LinkedHashMap;

/**
 * Class for modelling a block of tags and sub-blocks in the textblock of a MT
 * message
 */
public class TagBlock extends MTTree {

	TagBlock(String qualififer) {
		super("Block16R", qualififer);
	}

	TagBlock(String qualififer, LinkedHashMap<String, MTComponent> components) {
		super("Block16R", qualififer, components);
	}

}
