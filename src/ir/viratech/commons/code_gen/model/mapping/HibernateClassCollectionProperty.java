package ir.viratech.commons.code_gen.model.mapping;


import ir.viratech.commons.code_gen.model.exception.AttributeNotSpecifiedException;
import ir.viratech.commons.code_gen.model.util.HSUtil;
import ir.viratech.commons.code_gen.model.util.XmlUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Node;


/**
 * @author <a href="mailto: jhudson8@users.sourceforge.net">Joe Hudson</a>
 * 
 * This represents data related to the 'set', 'bag', 'list', 'map', and 'array'
 * nodes of the hibernate mapping configuration file.
 */
public class HibernateClassCollectionProperty extends HibernateClassProperty {

	public static final String TYPE_SET = "set";
	public static final String TYPE_BAG = "bag";
	public static final String TYPE_LIST = "list";
	public static final String TYPE_MAP = "map";
	public static final String TYPE_ARRAY = "array";
	public static final String TYPE_PRIMITIVE_ARRAY = "primitive-array";

	private String propType;
	private String implementation;
	private String absoluteChildClassName;
	private String childTableName;
	private String mapKeyType;
	public String getMapKeyType() {
		return mapKeyType;
	}
	private String mapValueType;
	public String getMapValueType() {
		return mapValueType;
	}
	
	public String getAbsoluteChildClassName() {
		return absoluteChildClassName;
	}
	
	private List<HibernateComponentClass> compositeList = new ArrayList<>();

	
	private boolean oneToMany = false;
	public boolean isOneToMany() {
		return oneToMany;
	}
	
	private boolean manyToMany = false;
	public boolean isManyToMany() {
		return manyToMany;
	}
	
	
	/**
	 * @param parent
	 * @param node
	 * @param isManyToOne
	 */
	public HibernateClassCollectionProperty (
		HibernateClass parent,
		Node node,
		String propType,
		String packageName) {
		super(parent, node, TYPE_PROPERTY, packageName, false, false);
		this.propType = propType;
		{
			String tableAttribute = XmlUtil.getAttributeValue(node, "table");
			if (null != tableAttribute) {
				childTableName = tableAttribute;
				
				String childTableSchemaName = this.getParent().getDocument().getSchemaName();
				String schemaAttribute = XmlUtil.getAttributeValue(node, "schema");
				if (schemaAttribute != null)
					childTableSchemaName = schemaAttribute;
				childTableName = HSUtil.addSchemaName(childTableSchemaName, tableAttribute);
			}
		}
		if (isSet()) {
			super.type = Set.class.getName();
			this.implementation = HashSet.class.getName();
			setChildClass(node);
		}
		else if (isBag()) {
			super.type = List.class.getName();
			this.implementation = ArrayList.class.getName();
			setChildClass(node);
		}
		else if (isList()) {
			super.type = List.class.getName();
			this.implementation = ArrayList.class.getName();
			setChildClass(node);
		}
		else if (isMap()) {
			super.type = Map.class.getName();
			this.implementation = HashMap.class.getName();
			setChildClass(node);
			for (final Node child : XmlUtil.getChildrenIterable(node)) {
				String chileNodeName = child.getNodeName();
				if (chileNodeName.equals("map-key")) {
					String attrVal = XmlUtil.getAttributeValue(child, "type");
					if (null != attrVal)
						mapKeyType = getTypeByName(attrVal);
				}
				else if (chileNodeName.equals("map-key-many-to-many") || chileNodeName.equals("composite-map-key")) {
					String attrVal = XmlUtil.getAttributeValue(child, "class");
					if (null != attrVal)
						mapKeyType = getFullClassByName(attrVal);
				}
				else if (chileNodeName.equals("element")) {
					String attrVal = XmlUtil.getAttributeValue(child, "type");
					if (null != attrVal)
						mapValueType = getTypeByName(attrVal);
				}
				else if (chileNodeName.equals("many-to-many") || chileNodeName.equals("one-to-many") || chileNodeName.equals("composite-element")) {
					String attrVal = XmlUtil.getAttributeValue(child, "class");
					if (null != attrVal)
						mapValueType = getFullClassByName(attrVal);
				}
				//*
				else if (chileNodeName.equals("many-to-any")) {
					String attrVal = XmlUtil.getAttributeValue(child, "id-type");
					if (null != attrVal)
						mapValueType = getFullClassByName(attrVal);
				}
				//*/
			}
		}
		else if (isArray()) {
			for (final Node child : XmlUtil.getChildrenIterable(node)) {
				if (child.getNodeName().equals("many-to-many") || child.getNodeName().equals("one-to-many") || child.getNodeName().equals("many-to-any")) {
					String attrVal = XmlUtil.getAttributeValue(child, "class");
					if (null != attrVal) {
						String className = attrVal;
						if (null != packageName && className.indexOf(".") < 0) {
							className = packageName + "." + className;
						}
						super.type = className + "[]";
						this.implementation = className;
					}
				}
				else if (child.getNodeName().equals("element")) {
					String attrVal = XmlUtil.getAttributeValue(child, "type");
					if (null != attrVal) {
						String className = attrVal;
						if (!propType.equals(TYPE_PRIMITIVE_ARRAY)) {
							String s = (String) HibernateClassProperty.typeMap.get(className);
							if (null != s) className = s;
							if (null != packageName && className.indexOf(".") < 0) {
								className = packageName + "." + className;
							}
						}
						super.type = className + "[]";
						this.implementation = className;
					}
				}
			}
			setChildClass(node);
		}
		for (final Node child : XmlUtil.getChildrenIterable(node)) {
			if (child.getNodeName().equals("composite-element") || child.getNodeName().equals("nested-composite-element")) {
				compositeList.add(new HibernateComponentClass(child, packageName, parent, false, parent.getDocument()));
			}
			if ("one-to-many".equals(child.getNodeName()))
				this.oneToMany = true;
			if ("many-to-many".equals(child.getNodeName()))
				this.manyToMany = true;
		}
		
		if (null == getName() || getName().length() == 0) {
			throw new AttributeNotSpecifiedException(node, "name");
		}
		String signatureClass = get("SignatureClass");
		if (null != signatureClass) {
			super.type = signatureClass;
			clear("SignatureClass");
		}
		String implementationClass = get("ImplementationClass");
		if (null != implementationClass) {
			this.implementation = implementationClass;
			clear("ImplementationClass");
		}
	}

	private String getFullClassByName(String className) {
		if (null != parent.getDocument().getPackageName() && className.indexOf(".") < 0)
			className = parent.getDocument().getPackageName() + "." + className;
		return className;
	}
	
	private String findChildClass(Node node) {
		for (final Node child : XmlUtil.getChildrenIterable(node)) {
			String chileNodeName = child.getNodeName();
			if (chileNodeName.equals("many-to-many") || chileNodeName.equals("one-to-many") || chileNodeName.equals("composite-element")) {
				String attrVal = XmlUtil.getAttributeValue(child, "class");
				if (null != attrVal)
					return getFullClassByName(attrVal);
			}
			else if (chileNodeName.equals("element")) {
				String attrVal = XmlUtil.getAttributeValue(child, "type");
				if (null != attrVal)
					return getTypeByName(attrVal);
			}
			else if (chileNodeName.equals("many-to-any")) {
				String attrVal = XmlUtil.getAttributeValue(child, "id-type");
				if (null != attrVal)
					return getFullClassByName(attrVal);
			}
		}
		return null;
	}
	private void setChildClass (Node node) {
		String classAttribute = XmlUtil.getAttributeValue(node, "class");
		if (null != classAttribute) {
			this.absoluteChildClassName = getFullClassByName(classAttribute);
		} else {
			this.absoluteChildClassName = findChildClass(node);
		}
	}
	
	public String getGenericMarker () {
		if (isMap()) {
			if (null != mapKeyType && null != mapValueType)
				return "<" + mapKeyType + ", " + mapValueType + ">";
			else
				return "";
		}
		else {
			return "<" + absoluteChildClassName + ">";
		}
	}

	/**
	 * @return Returns the list of composite-element class associate with this collection
	 * @return a List of HibernateComponentClass objects
	 */
	public List<HibernateComponentClass> getCompositeList() {
		return compositeList;
	}

	/**
	 * Return true if this collection represents a Set and false otherwise
	 */
	public boolean isSet() {
		return TYPE_SET.equals(propType);
	}

	/**
	 * Return true if this collection represents a Bag and false otherwise
	 */
	public boolean isBag() {
		return TYPE_BAG.equals(propType);
	}

	/**
	 * Return true if this collection represents a List and false otherwise
	 */
	public boolean isList() {
		return TYPE_LIST.equals(propType);
	}

	/**
	 * @return true if this is a collection. 
	 */
	public boolean isCollection() {
		return isList() || isSet() || isBag();
	}
	
	/**
	 * @return true if this is a collection or map. 
	 */
	public boolean isCollectionOrMap() {
		return isCollection() || isMap();
	}
	
	public boolean hasChildClassTypes() {
		if (isMap())
			return (getMapKeyType() != null) && (getMapValueType()!=null);
		HibernateClass childClass = getChildClass();
		if (childClass != null)
			return childClass.getAbsoluteValueObjectSignatureClassName() != null;
		return this.getAbsoluteChildClassName() != null;
	}
	
	public String getChildClassAbsoluteName() {
		HibernateClass childClass = getChildClass();
		if (childClass != null)
			return childClass.getAbsoluteValueObjectSignatureClassName();
		return this.getAbsoluteChildClassName();
	}
	public String getChildClassVariableName() {
		HibernateClass childClass = getChildClass();
		if (childClass != null)
			return childClass.getVarName();
		String cname = this.getAbsoluteChildClassName();
		return (cname.indexOf('.') < 0) ? "x" :
			HibernateClassProperty.getVarName(cname.substring(cname.lastIndexOf('.')+1));
	}
	
	public String getCreatedGetterName() {
		return "getCreated"+getName_firstLetterUpper();
	}
	
	/**
	 * Return true if this collection represents a Map and false otherwise
	 */
	public boolean isMap() {
		return TYPE_MAP.equals(propType);
	}

	/**
	 * Return true if this collection represents an array and false otherwise
	 */
	public boolean isArray() {
		return TYPE_ARRAY.equals(propType) || TYPE_PRIMITIVE_ARRAY.equals(propType);
	}

	/**
	 * Return the static variable definition name based on this property
	 */
	public String getStaticName () {
		return HSUtil.getStaticName(name);
	}

	/**
	 * Return the plural representation of the property name
	 * @return
	 */
	public HibernateClass getChildClass () {
		if (null != absoluteChildClassName)
			return HibernateMappingManager.getInstance().getHibernateClass(absoluteChildClassName);
		else if (null != childTableName)
			return HibernateMappingManager.getInstance().getHibernateClassByTableName(childTableName);
		else return null;
	}
	
	@Override
	public String getChildTableName() {
		return childTableName;
	}
	
	
	@Override
	protected HibernateClass getReverseClass() {
		return this.getChildClass();
	}

	/**
	 * Return the fully qualified implementation class based on the type of
	 * collection this represents.
	 */
	public String getAbsoluteImplementationClassName() {
		return implementation;
	}

	/**
	 * Return the reserved properties associated with this element
	 */
	private static final String[] IP = new String[] {"name", "table", "schema", "lazy", "inverse", "cascade", ATTRIBUTE_INITIALIZATION, ATTRIBUTE_INITIALIZATION_ORDER, "sort", "order-by", "where", "outer-join", "batch-size", "access", "inverse", ATTRIBUTE_REVERSE};
	protected String[] getReservedProperties() {
		return IP;
	}

}