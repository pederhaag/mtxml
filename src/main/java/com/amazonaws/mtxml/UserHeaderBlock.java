package com.amazonaws.mtxml;

/**
 * Modelling of the userheader block in a MT-message
 */
public class UserHeaderBlock extends AbstractBlock implements MTComponent {

	UserHeaderBlock(String content) {
		super(content, "3");
	}

	@Override
	String getXmlNodeName() {
		return "UserHeaderBlock";
	}
}
