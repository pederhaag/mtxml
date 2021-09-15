package com.amazonaws.mtxml;

import java.util.ArrayList;

public class TagBlock implements MTComponent {
	private ArrayList<MTComponent> components = new ArrayList<MTComponent>();
	private final String qualifier;

	TagBlock(String qualififer) {
		this.qualifier = qualififer;
	}

	void addComponent(MTComponent c) {
		components.add(c);
	}

	@Override
	public String toXml() {
		String opening = XmlFactory.openNode("Block16R", "Qualifier", qualifier);

		String content = "";
		for (MTComponent c : components) {
			content += c.toXml();
		}

		String closing = XmlFactory.closeNode("Block16R");

		return opening + content + closing;
	}

}
