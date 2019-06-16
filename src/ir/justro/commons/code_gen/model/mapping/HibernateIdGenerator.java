package ir.justro.commons.code_gen.model.mapping;

import ir.justro.commons.code_gen.model.exception.AttributeNotSpecifiedException;
import ir.viratech.commons.code_gen.model.util.XmlUtil;

import org.w3c.dom.Node;



public class HibernateIdGenerator extends BaseElement {

	private HibernateClassId parent;
	private String generatorClass;
	
	public HibernateIdGenerator (HibernateClassId parent, Node node) {
		this.parent = parent;
		setNode(node);
		for (Node attNode : XmlUtil.getAttributesIterable(node)) {
			if (attNode.getNodeName().equals("class")) {
				this.generatorClass = attNode.getNodeValue();
			}
		}
		saveMetaData(node);
		if (null == generatorClass) {
			throw new AttributeNotSpecifiedException(node, "class");
		}
	}
	
	/**
	 * @return Returns the generatorClass.
	 */
	public String getGeneratorClass() {
		return generatorClass;
	}

	/**
	 * @return Returns the parent.
	 */
	public HibernateClassId getParent() {
		return parent;
	}

	/**
	 * Return the reserved properties associated with this element
	 */
	protected String[] getReservedProperties() {
		return IP;
	}
	private static final String[] IP = new String[] {"class"};
}