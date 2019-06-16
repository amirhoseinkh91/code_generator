package ir.justro.commons.code_gen.model.mapping;

import ir.viratech.commons.code_gen.model.util.HSUtil;
import ir.viratech.commons.code_gen.model.util.XmlUtil;

import org.w3c.dom.Node;


/**
 * @author <a href="mailto: jhudson8@users.sourceforge.net">Joe Hudson</a>
 * 
 * This represents data related to the 'query' node of the hibernate
 * mapping configuration file.
 */
public class HibernateQuery extends BaseElement implements Comparable<HibernateQuery> {

	private String name;
	private HibernateClass parent;

	public HibernateQuery (Node node, HibernateClass parent) {
		this.parent = parent;
		setParentRoot(parent);
		setNode(node);
		for (Node attNode : XmlUtil.getAttributesIterable(node)) {
			if (attNode.getNodeName().equals("name")) {
				name = attNode.getNodeValue();
			}
		}
		saveMetaData(node);
	}

	/**
	 * Return the name of this query
	 */
	public String getName () {
		return name;
	}

	/**
	 * Return the static variable name for this query
	 */
	public String getStaticName () {
		return HSUtil.getStaticName(name);
	}

	/**
	 * Return the parent HibernateClass
	 */
	public HibernateClass getParent() {
		return parent;
	}

	/**
	 * Compare this to another object
	 */
	public int compareTo(HibernateQuery arg0) {
		return getName().compareTo(arg0.getName());
	}
}
