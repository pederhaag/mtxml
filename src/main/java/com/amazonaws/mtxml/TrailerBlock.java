package com.amazonaws.mtxml;

/**
 * Modelling of the trailerblock part of a MT message
 */
public class TrailerBlock extends AbstractBlock implements MTComponent {

	TrailerBlock(String content) {
		super(content, "5");
	}

	@Override
	String getXmlNodeName() {
		return "TrailerBlock";
	}

}
