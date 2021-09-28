package com.amazonaws.mtxml;

/**
 * Modelling of the trailerblock part of a MT message
 */
public class TrailerBlock extends AbstractBlock {

	TrailerBlock(String content) {
		super(content, "TrailerBlock", "5");
	}

	String getXmlTagNodeName() {
		return "Trailer";
	}

	String getXmlTagCodeNodeName() {
		return "Code";
	}

	String getXmlTagValueNodeName() {
		return "TrailerInformation";
	}

}
