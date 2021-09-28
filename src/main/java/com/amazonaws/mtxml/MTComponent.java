package com.amazonaws.mtxml;

/**
 * 
 * {@code MTComponent} describes a set of methods needed in every component in a
 * MT-message. This includes blocks, tags and tagblocks.
 *
 */
interface MTComponent {
	/**
	 * Describe the object in a XML-syntax. Not nessecarily propperly beautified
	 * with indents.
	 * 
	 * @return A {@code String} containing a XML-representation of the object and
	 *         its subcomponents
	 */
	public String toXml();

	/**
	 * Get the value of the component
	 * 
	 * @return A {@code String} describing the value of the component.
	 */
	public String getValue();
}
