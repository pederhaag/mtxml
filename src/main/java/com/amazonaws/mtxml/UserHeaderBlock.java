package com.amazonaws.mtxml;

/**
 * Modelling of the userheader block in a MT-message
 */
public class UserHeaderBlock extends AbstractBlock {

	UserHeaderBlock(String content) {
		super(content, "UserHeader", "3");
	}

	String getXmlTagNodeName() {
		return "UserTag";
	}

	String getXmlTagCodeNodeName() {
		return "Tag";
	}

	String getXmlTagValueNodeName() {
		return "Contents";
	}

}
