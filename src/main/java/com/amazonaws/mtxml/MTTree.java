package com.amazonaws.mtxml;

import java.util.LinkedHashMap;

import com.amazonaws.mtxml.utils.XmlFactory;

/**
 * 
 * The {@code MTTree} class models a node which is comprised of one or more
 * underlying components.
 *
 */
public class MTTree implements MTComponent {
	/**
	 * Container for the underlying components
	 */
	private LinkedHashMap<String, MTComponent> components = new LinkedHashMap<String, MTComponent>();

	/**
	 * Name of the resulting XML-node
	 */
	private String nodeName;

	/**
	 * Qualifier which will end up as an attribute of the root node in the XML
	 * structure
	 */
	private String qualifier;

	MTTree(String nodeName, String qualifier) {
		this.nodeName = nodeName;
		this.qualifier = qualifier;
	}

	MTTree(String nodeName, String qualifier, LinkedHashMap<String, MTComponent> components) {
		this.nodeName = nodeName;
		this.qualifier = qualifier;
		this.components = components;
	}

	/**
	 * Add a new {@code MTComponent} as a subcomponent to this object
	 * 
	 * @param name Key value for the component. This is field will correspond to the
	 *             parameter in {@link #getData(String)}
	 * @param c    Component to be added
	 */
	void addComponent(String name, MTComponent c) {
		components.put(name, c);
	}

	/**
	 * Get the component with the given key
	 * 
	 * @param name Name of component to get
	 * @return Said component
	 */
	private MTComponent getComponent(String name) {
		return components.get(name);
	}

	/**
	 * Generalized getter for the different fields.
	 * 
	 * @param fieldName Field to get
	 * @return Value of said field
	 * @throws IllegalArgumentException if parameter fieldName is wrongfully
	 *                                  formatted, i.e. '.somefield'
	 */
	String getData(String fieldName) throws IllegalArgumentException {
		int sepIndex = fieldName.indexOf(".");
		Boolean isSubField = sepIndex > 0;

		// If field is a subfield, i.e. contains a '.'..
		if (isSubField) {
			// .. identify parent
			String parentComponent = fieldName.substring(0, sepIndex);
			String childField = fieldName.substring(sepIndex + 1, fieldName.length());
			MTComponent childNode = getComponent(parentComponent);

			if (childNode instanceof MTEndNode) {
				// If the parent is an end-node return its value
				return childNode.getValue();
			} else {
				// If the parent is a tree-node then recursively call its getData method
				MTTree parentNode = (MTTree) getComponent(parentComponent);
				return parentNode.getData(childField);
			}
		}

		if (fieldName.equals("Qualifier")) {
			// Qualifier is contained in its own field
			return qualifier;

		} else if (components.containsKey(fieldName))
			// Retrieve the correct component and its value
			return getComponent(fieldName).getValue();

		else
			// Unknown field will return null
			return null;
	}

	/**
	 * Get a field by its index
	 * 
	 * @param ix Index of the field to retrieve
	 * @return The value of the field
	 * @throws IndexOutOfBoundsException if supplied parameter {@code ix} is less
	 *                                   than 0 or larger than the number of
	 *                                   components minus one.
	 */
	public String getData(int ix) throws IndexOutOfBoundsException {
		if ((ix < 0) || (ix > components.size() - 1))
			throw new IndexOutOfBoundsException();
		int number = 0;
		for (String key : components.keySet()) {
			if (number == ix)
				return components.get(key).getValue();
			else
				number++;
		}
		return null;
	}

	/**
	 * Return a list field/components contained in this object
	 */
	public String[] listFields() {
		return (String[]) components.keySet().toArray();
	}

	@Override
	public String toXml() {
		StringBuilder sb = new StringBuilder();

		if (qualifier == null) {
			sb.append(XmlFactory.openNode(nodeName));
		} else {
			sb.append(XmlFactory.openNode(nodeName, "Qualifier", qualifier));
		}

		for (MTComponent c : components.values()) {
			sb.append(c.toXml());
		}

		sb.append(XmlFactory.closeNode(nodeName));

		return sb.toString();
	}

	/**
	 * The value of a tree node itself is null. Values are contained in end-nodes.
	 */
	@Override
	public String getValue() {
		return null;
	}

}
