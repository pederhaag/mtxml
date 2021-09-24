package com.amazonaws.mtxml;

import java.util.ArrayList;

import com.amazonaws.mtxml.utils.XmlFactory;

/**
 * Class for modelling a block of tags and sub-blocks in the textblock of a MT
 * message
 */
public class TagBlock implements MTComponent {
	/**
	 * Container for sub-components
	 */
	private ArrayList<MTComponent> components = new ArrayList<MTComponent>();

	private final String qualifier;

	TagBlock(String qualififer) {
		this.qualifier = qualififer;
	}

	/**
	 * Add a component of the block
	 */
	void addComponent(MTComponent c) {
		components.add(c);
	}

	@Override
	public String toXml() {
		String xmlOpening = XmlFactory.openNode("Block16R", "Qualifier", qualifier);

		String xmlChildren = "";
		for (MTComponent c : components) {
			xmlChildren += c.toXml();
		}

		String xmlClosing = XmlFactory.closeNode("Block16R");

		return xmlOpening + xmlChildren + xmlClosing;
	}

}
