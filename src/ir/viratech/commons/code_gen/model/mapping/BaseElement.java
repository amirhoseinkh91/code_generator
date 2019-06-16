package ir.viratech.commons.code_gen.model.mapping;

import ir.viratech.commons.code_gen.model.util.HSUtil;
import ir.viratech.commons.code_gen.model.util.XmlUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang3.ArrayUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class BaseElement {

	private Map<String, String> properties = new HashMap<>();
	private Map<String, String> allProperties = new HashMap<>();
	private Node node;
	private HibernateClass rootClass;

	/**
	 * Set the property using the key and value given
	 * @param key
	 * @param value
	 */
	public void set (String key, String value) {
		if (null != key && null != value) {
			String[] invalidProps = getReservedProperties();
			if (!ArrayUtils.contains(invalidProps, key)) {
				if(value.indexOf('\"') < 0 && value.indexOf('\n') < 0) {
					properties.put(HSUtil.getJavaNameCap(key), value);
				}
			}
			allProperties.put(HSUtil.getJavaNameCap(key), value);
		}
	}

	/**
	 * Clear the cached property for the given key
	 * @param key
	 */
	public void clear (String key) {
		properties.remove(HSUtil.getJavaNameCap(key));
		allProperties.remove(HSUtil.getJavaNameCap(key));
	}

	/**
	 * Return the save-able property for the given key
	 * @param key
	 */
	public String get (String key) {
		if (null == key) return null;
		else {
			return (String) properties.get(HSUtil.getJavaNameCap(key));
		}
	}

	/**
	 * Return the property for the given key.
	 * If force is true, return the property match even if the property is marked as not save-able
	 * @param key
	 * @param force
	 * @return
	 */
	public String get (String key, boolean force) {
		if (!force) return get(key);
		else {
			if (null == key) return null;
			else {
				return (String) allProperties.get(HSUtil.getJavaNameCap(key));
			}
		}
	}

	/**
	 * Return Javadoc style comments for the given String
	 * @param key
	 */
	public String getJavaDoc (String key) {
		return getJavaDoc(key, " ");
	}

	/**
	 * Return Javadoc style comments for the given String using the indentation given
	 * @param key
	 * @param indent
	 */
	public String getJavaDoc (String key, String indent) {
		String content = get(key, true);
		if (null == content) return null;
		else {
			StringBuffer sb = new StringBuffer();
			StringTokenizer st = new StringTokenizer(content, "\n");
			while (st.hasMoreTokens()) {
				if (sb.length() > 0) sb.append('\n');
				sb.append(indent);
				sb.append("* ");
				sb.append(st.nextToken().trim());
			}
			return sb.toString();
		}
	}

	/**
	 * Return all custom properties for this element
	 */
	public Map<String, String> getCustomProperties () {
		return properties;
	}

	/**
	 * Return an array of properties that should not be considered custom properties
	 */
	protected String[] getReservedProperties () {
		return null;
	}

	/**
	 * Return the node associated with this element
	 */
	public Node getNode() {
		return node;
	}

	/**
	 * Helper method to return the contents of a given node
	 * @param node
	 */
	protected String getNodeText (Node node) {
		for (int i=0; i<node.getChildNodes().getLength(); i++) {
			if (node.getChildNodes().item(i).getNodeType() == Node.TEXT_NODE) {
				String text = node.getChildNodes().item(i).getNodeValue().trim();
				if (text.length() == 0) return null;
				else return text;
			}
		}
		return null;
	}

	/**
	 * Set the custom properties associated with the meta-data for this element
	 * @param node the element node
	 */
	protected void saveMetaData (Node node) {
		for (final Node child : XmlUtil.getChildrenIterable(node)) {
			if (child.getNodeName().equals("meta")) {
				String key = XmlUtil.getAttributeValue(child, "attribute");
				String value = getNodeText(child);
				if (null != key && null != value) {
					set(key, value);
				}
			}
		}
	}

	/**
	 * Set the node related to this element
	 * @param node the element node
	 */
	public void setNode(Node node) {
		this.node = node;
	}

	/**
	 * Return the class root for this element or self if this element is a HibernateClass that
	 * is the parent class
	 */
	public HibernateClass getParentRoot() {
		return rootClass;
	}

	/**
	 * Set the class root for this element
	 * @param rootClass
	 */
	public void setParentRoot(HibernateClass rootClass) {
		this.rootClass = rootClass;
	}

	/**
	 * Return a list of meta-data nodes associated with this element
	 * @return a List of Node objects
	 */
	public List<Node> getMetaData () {
		if (null == node) return null;
		List<Node> nodes = null;
		NodeList nl = node.getChildNodes();
		for (int i=0; i<nl.getLength(); i++) {
			Node item = nl.item(i);
			if (item.getNodeName().equals("meta")) {
				if (null == nodes) nodes = new ArrayList<>();
				nodes.add(item);
			}
		}
		return nodes;
	}
}