package ir.viratech.commons.code_gen.model.mapping;


import ir.viratech.commons.code_gen.model.exception.AttributeNotSpecifiedException;
import ir.viratech.commons.code_gen.model.exception.TransientPropertyException;
import ir.viratech.commons.code_gen.model.util.HSUtil;
import ir.viratech.commons.code_gen.model.util.XmlUtil;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.w3c.dom.Node;



/**
 * @author <a href="mailto: jhudson8@users.sourceforge.net">Joe Hudson</a>
 * 
 * This represents data related to the 'property' node of the 'class' node in the hibernate
 * mapping configuration file.
 */
public class HibernateClassProperty extends BaseElement implements Comparable<HibernateClassProperty>, IHibernateClassProperty {

	public static final String ATTRIBUTE_REVERSE = "reverse";
	public static final String ATTRIBUTE_INITIALIZATION = "init";
	public static final String ATTRIBUTE_INITIALIZATION_ORDER = "init-order";
	public static final int TYPE_PROPERTY = 1;
	public static final int TYPE_MANY_TO_ONE = 2;
	public static final int TYPE_ONE_TO_ONE = 3;
	public static final int TYPE_COLLECTION = 4;
	
	static final Map<String, String> typeMap = new HashMap<>();
	static final Map<String, String> primitiveMap = new HashMap<>();
	static final Map<String, Boolean> reservedVarNames = new HashMap<>();
	
	protected HibernateClass parent;
	protected String name;
	protected String type;
	protected String column;
	protected boolean notNull;
	protected boolean unique;
	protected boolean alternateKey;
	
	protected String initializationExpression = null;
	protected Double initializationOrder = null;
	
	protected boolean primaryKey;
	protected Integer length;
	//private String propertyType;
	private String scopeGet = "public";
	private String scopeSet = "public";
	private String scopeField = "private";
	private String finderMethod;
	
	private int refType;
	
	
	private String reversePropertyName = null;
	private IHibernateClassProperty reverseProperty = null;
	
	public IHibernateClassProperty getReverseProperty() {
		return reverseProperty;
	}
	

	static {
		// these are all the known properties
		typeMap.put("string", String.class.getName());
		typeMap.put("binary", "byte[]");
		typeMap.put("int", "int");
		typeMap.put("float", "float");
		typeMap.put("long", "long");
		typeMap.put("double", "double");
		typeMap.put("char", "char");
		typeMap.put("yes_no", "boolean");
		typeMap.put("true_false", "boolean");
		typeMap.put("byte", "byte");
		typeMap.put("integer", Integer.class.getName());
		typeMap.put("currency", "java.util.Currency");
		typeMap.put("big_decimal", BigDecimal.class.getName());
		typeMap.put("character", Character.class.getName());
		typeMap.put("calendar", Calendar.class.getName());
		typeMap.put("calendar_date", Calendar.class.getName());
		typeMap.put("date", Date.class.getName());
		typeMap.put(Timestamp.class.getName(), Date.class.getName());
		typeMap.put("timestamp", Date.class.getName());
		typeMap.put("time", Date.class.getName());
		typeMap.put("locale", Locale.class.getName());
		typeMap.put("timezone", TimeZone.class.getName());
		typeMap.put("class", Class.class.getName());
		typeMap.put("serializable", Serializable.class.getName());
		typeMap.put("object", Object.class.getName());
		typeMap.put("blob", Blob.class.getName());
		typeMap.put("clob", Clob.class.getName());
		typeMap.put("text", String.class.getName());

		// these are all the known primitives
		primitiveMap.put("int", Integer.class.getName());
		primitiveMap.put("short", Integer.class.getName());
		primitiveMap.put("float", Float.class.getName());
		primitiveMap.put("long", Long.class.getName());
		primitiveMap.put("double", Double.class.getName());
		primitiveMap.put("char", Character.class.getName());
		primitiveMap.put("boolean", Boolean.class.getName());
		primitiveMap.put("byte", Byte.class.getName());
		
		// all known reserved variable names
		reservedVarNames.put("abstract", Boolean.TRUE);
		reservedVarNames.put("boolean", Boolean.TRUE);
		reservedVarNames.put("break", Boolean.TRUE);
		reservedVarNames.put("byte", Boolean.TRUE);
		reservedVarNames.put("case", Boolean.TRUE);
		reservedVarNames.put("catch", Boolean.TRUE);
		reservedVarNames.put("char", Boolean.TRUE);
		reservedVarNames.put("class", Boolean.TRUE);
		reservedVarNames.put("const", Boolean.TRUE);
		reservedVarNames.put("continue", Boolean.TRUE);
		reservedVarNames.put("default", Boolean.TRUE);
		reservedVarNames.put("do", Boolean.TRUE);
		reservedVarNames.put("double", Boolean.TRUE);
		reservedVarNames.put("else", Boolean.TRUE);
		reservedVarNames.put("extends", Boolean.TRUE);
		reservedVarNames.put("false", Boolean.TRUE);
		reservedVarNames.put("final", Boolean.TRUE);
		reservedVarNames.put("finally", Boolean.TRUE);
		reservedVarNames.put("float", Boolean.TRUE);
		reservedVarNames.put("for", Boolean.TRUE);
		reservedVarNames.put("goto", Boolean.TRUE);
		reservedVarNames.put("assert", Boolean.TRUE);
		reservedVarNames.put("if", Boolean.TRUE);
		reservedVarNames.put("implements", Boolean.TRUE);
		reservedVarNames.put("import", Boolean.TRUE);
		reservedVarNames.put("instanceof", Boolean.TRUE);
		reservedVarNames.put("int", Boolean.TRUE);
		reservedVarNames.put("interface", Boolean.TRUE);
		reservedVarNames.put("long", Boolean.TRUE);
		reservedVarNames.put("native", Boolean.TRUE);
		reservedVarNames.put("new", Boolean.TRUE);
		reservedVarNames.put("null", Boolean.TRUE);
		reservedVarNames.put("package", Boolean.TRUE);
		reservedVarNames.put("private", Boolean.TRUE);
		reservedVarNames.put("protected", Boolean.TRUE);
		reservedVarNames.put("public", Boolean.TRUE);
		reservedVarNames.put("return", Boolean.TRUE);
		reservedVarNames.put("short", Boolean.TRUE);
		reservedVarNames.put("static", Boolean.TRUE);
		reservedVarNames.put("strictfp", Boolean.TRUE);
		reservedVarNames.put("super", Boolean.TRUE);
		reservedVarNames.put("switch", Boolean.TRUE);
		reservedVarNames.put("synchronized", Boolean.TRUE);
		reservedVarNames.put("this", Boolean.TRUE);
		reservedVarNames.put("throw", Boolean.TRUE);
		reservedVarNames.put("throws", Boolean.TRUE);
		reservedVarNames.put("transient", Boolean.TRUE);
		reservedVarNames.put("true", Boolean.TRUE);
		reservedVarNames.put("try", Boolean.TRUE);
		reservedVarNames.put("void", Boolean.TRUE);
		reservedVarNames.put("volatile", Boolean.TRUE);
		reservedVarNames.put("while", Boolean.TRUE);
	}

	/**
	 * Constructor for standard property
	 * @param parent the HibernateClass this property belongs to
	 * @param node the XML node
	 * @throws TransientPropertyException
	 */
	public HibernateClassProperty (HibernateClass parent, Node node) throws TransientPropertyException {
		this(parent, node, TYPE_PROPERTY, null, true, false);
	}

	/**
	 * Constructor for standard property (validating optional)
	 * @param parent the HibernateClass this property belongs to
	 * @param node the XML node
	 * @param validate
	 * @throws TransientPropertyException
	 */
	public HibernateClassProperty (HibernateClass parent, Node node, boolean validate) throws TransientPropertyException {
		this(parent, node, TYPE_PROPERTY, null, validate, false);
	}

	/**
	 * Constructor for many-to-one property
	 * @param parent the HibernateClass this property belongs to
	 * @param node the XML node
	 * @param refType the relational type
	 * @param packageName the package for the relational class
	 * @throws TransientPropertyException
	 */
	public HibernateClassProperty (HibernateClass parent, Node node, int refType, String packageName) throws TransientPropertyException {
		this(parent, node, refType, packageName, true, false);
	}

	/**
	 * Constructor
	 * @param parent the HibernateClass this property belongs to
	 * @param node the XML node
	 * @param refType the relational type
	 * @param packageName the package for the relational class
	 * @param validate
	 * @param primaryKey
	 * @throws TransientPropertyException
	 */
	public HibernateClassProperty (HibernateClass parent, Node node, int refType, String packageName, boolean validate, boolean primaryKey) throws TransientPropertyException {
		this.parent = parent;
		setParentRoot(parent);
		setNode(node);
		this.refType = refType;
		this.primaryKey = primaryKey;
		for (Node attNode : XmlUtil.getAttributesIterable(node)) {
			String attrVal = attNode.getNodeValue();
			String attrKey = attNode.getNodeName();
			if (attrKey.equals("name")) {
				name = attrVal;
			}
			else if (attrKey.equals("column")) {
				column = attrVal;
			}
			else if (attrKey.equals("length")) {
				try {
					setLength(new Integer(attrVal.trim()));
				}
				catch (Exception e) {}
			}
			else if (attrKey.equals("not-null")) {
				if (HSUtil.isTrue(attrVal)) {
					notNull = true;
				}
			}
			else if (attrKey.equals("unique")) {
				if (HSUtil.isTrue(attrVal))
					this.unique = true;
			}
			else if (attrKey.equals("type")) {
				if (!(isManyToOne() || isOneToOne())) 
					type = attrVal;
			}
			else if (attrKey.equals("class")) {
				type = attrVal;
				if ((isManyToOne() || isOneToOne()) && null != packageName && type.indexOf(".") == -1) {
					type = packageName + "." + type;
				}
			}
		}

		for (final Node child : XmlUtil.getChildrenIterable(node)) {
			if (child.getNodeName().equals("column")) {
				String attNode = XmlUtil.getAttributeValue(child, "name");
				if (null != attNode) {
					column = attNode;
				}
			}
			else if (child.getNodeName().equals("meta")) {
				String key = XmlUtil.getAttributeValue(child, "attribute");
				String value = getNodeText(child);
				if (null != key && null != value) {
					if ((key.equals("alternate-key") && HSUtil.isTrue(value))
							|| (key.equals("use-in-equals") && HSUtil.isTrue(value))) {
						alternateKey = true;
					}
					if (ATTRIBUTE_INITIALIZATION.equals(key)) {
						this.initializationExpression = value;
					}
					if (ATTRIBUTE_INITIALIZATION_ORDER.equals(key)) {
						this.initializationOrder = Double.parseDouble(value);
					}
					if (ATTRIBUTE_REVERSE.equals(key)) {
						this.reversePropertyName = value;
					}
					if (key.equals("finder-method")) {
						finderMethod = value;
					}
					else if (key.equals("gen-property") && HSUtil.isFalse(value)) {
						throw new TransientPropertyException();
					}
				}
			}
		}
		
		if (column == null) {
			if (Arrays.asList("id", "property", "many-to-one").contains(node.getNodeName()))
				column = this.getName();
		}
		
		saveMetaData (node);

		// see if the implementation class is overridden
		if (null != get("property-type", true)) type = get("property-type", true);
		if (null != get("scope-get", true)) scopeGet = get("scope-get", true);
		if (null != get("scope-set", true)) scopeSet = get("scope-set", true);
		if (null != get("scope-field", true)) scopeField = get("scope-field", true);

		// validate
		if (validate && (null == name || name.length() == 0)) {
			throw new AttributeNotSpecifiedException(node, "name");
		}
		if (validate && (null == type || type.length() == 0)) {
			if (isManyToOne() || isOneToOne()) {
				throw new AttributeNotSpecifiedException(node, "class");
			}
			else {
				throw new AttributeNotSpecifiedException(node, "type");
			}
		}
	}
	
	
	protected HibernateClass getReverseClass() {
		return (this.isManyToOne() || this.isOneToOne()) ? HibernateMappingManager.getInstance().getHibernateClass(this.type) : null;
	}
	
	@Override
	public void postProcess() {
		if (this.reversePropertyName != null) {
			HibernateClass revClass = this.getReverseClass();
			if (revClass != null) {
				this.reverseProperty = revClass.getProperty(this.reversePropertyName);
			}
		}
	}
	

	protected String getTypeByName (String name) {
		String type = (String) primitiveMap.get(name);
		if (type != null)
			return type;
		type = (String) typeMap.get(name);
		if (type != null)
			return type;
		return name;
	}

	/**
	 * Return the column name that this property represents
	 */
	public String getColumn() {
		return column;
	}
	
	public boolean isUnique() {
		return unique;
	}
	
	public String getChildTableName() {
		return null;
	}

	/**
	 * Return the defined property name for this property
	 */
	public String getName() {
		return name;
	}
	
	public String getName_firstLetterUpper() {
		return ir.viratech.commons.code_gen.model.util.HSUtil.firstLetterUpper(getName());
	}
	
	/**
	 * Return a descriptive label based on the property name or the label meta attribute if exists
	 */
	public String getLabel() {
		if (getCustomProperties().size() == 0)
			return ir.viratech.commons.code_gen.model.util.HSUtil.getPropDescription(getName());
		else {
			String label = get(IHibernateClassProperty.LABEL_METADATA);
			if (null == label)
				return ir.viratech.commons.code_gen.model.util.HSUtil.getPropDescription(getName());
			else
				return label;
		}
	}

	/**
	 * Return the actual property name for this property (first letter upper case)
	 */
	public String getPropName() {
		return ir.viratech.commons.code_gen.model.util.HSUtil.firstLetterUpper(getName());
	}

	/**
	 * Return the getter name (without the parenthesis) for this property
	 * @return the getter name
	 */
	public String getGetterName() {
		String fullClassName = getAbsoluteClassName();
		if (null != fullClassName && (fullClassName.equals("boolean") || fullClassName.equals(Boolean.class.getName()))) {
			return "is" + getPropName();
		}
		else {
			return "get" + getPropName();
		}
	}

	/**
	 * Return the setter name (without the parenthesis) for this property
	 * @return the setter name
	 */
	public String getSetterName() {
		return "set" + getPropName();
	}

	public static String getVarName(String name) {
		if (null == reservedVarNames.get(name.toLowerCase())) return ir.viratech.commons.code_gen.model.util.HSUtil.firstLetterLower(name);
		else return "m_" + ir.viratech.commons.code_gen.model.util.HSUtil.firstLetterLower(name);
	}
	
	/**
	 * Return the name used as the Java variable name for this property (first letter lower case)
	 * @return the Java variable name
	 */
	public String getVarName() {
		String name = getName();
		return getVarName(name);
	}
	
	/**
	 * Return the value that was specified as the "type" attribute for this property
	 * @return the type attribute
	 */
	public String getType() {
		return type;
	}

	/**
	 * Set the type related to this property
	 * @param type
	 */
	void setType (String type) {
		this.type = type;
	}

	/**
	 * Return the fully qualified signature of the class representing this property
	 * (or the actual class if it is not a proxy)
	 */
	public String getAbsoluteSignatureClassName () {
		String className = getAbsoluteClassName();
		HibernateClass hc = HibernateMappingManager.getInstance().getHibernateClass(className);
		if (null == hc) return className;
		else return hc.getAbsoluteValueObjectSignatureClassName();
	}

	/**
	 * Return the name of the class representing this property
	 * (or the actual class if it is not a proxy)
	 * @return
	 */
	public String getSignatureClassName () {
		String className = getClassName();
		HibernateClass hc = HibernateMappingManager.getInstance().getHibernateClass(className);
		if (null == hc) return className;
		else return hc.getValueObjectClassName();
	}

	/**
	 * Return the fully qualified class name that represents this property
	 */
	public String getAbsoluteClassName() {
		if (null == type) return null;
		else {
			String rtnType = (String) typeMap.get(type);
			if (null == rtnType) return type;
			else return rtnType;
		}
	}

	/**
	 * Return the name of the class without the the package prefix that represents this property
	 */
	public String getClassName () {
		return ir.viratech.commons.code_gen.model.util.HSUtil.getClassPart(getAbsoluteClassName());
	}

	/**
	 * Return the package prefix for this property class without the class name
	 */
	public String getPackage() {
		return HSUtil.getPackagePart(getAbsoluteClassName());
	}

	/**
	 * Return the parent class for this property
	 * @return the parent HibernateClass
	 */
	public HibernateClass getParent() {
		return parent;
	}

	/**
	 * Return true if this property can be determined as an user type and false if not
	 */
	public boolean isUserType () {
		return (!isManyToOne()
				&& null != getParent().getValueObjectPackage()
				&& getParent().getValueObjectPackage().trim().length() > 0
				&& getPackage().equals(getParent().getValueObjectPackage()));
	}

	/**
	 * Return true if this property can not be null and false otherwise
	 */
	public boolean isRequired() {
		return notNull;
	}

	/**
	 * @return
	 */
	public boolean isAlternateKey() {
		return alternateKey;
	}

	public boolean isPrimaryKey() {
		return primaryKey;
	}

	/**
	 * @param isAlternateKey
	 */
	public void setAlternateKey(boolean isAlternateKey) {
		this.alternateKey = isAlternateKey;
		parent.alternateKeys = null;
	}

	/**
	 * Return true if this property is a many-to-one and false otherwise
	 */
	public boolean isManyToOne () {
		return refType == TYPE_MANY_TO_ONE;
	}

	/**
	 * Return true if this property is a many-to-one and false otherwise
	 */
	public boolean isOneToOne () {
		return refType == TYPE_ONE_TO_ONE;
	}

	/**
	 * Return true if the type of this property represents a primitive
	 */
	public boolean isPrimitive() {
		return (null != primitiveMap.get(type));
	}

	/**
	 * Return the object class representation for this class
	 */
	public String getObjectClass () {
		if (!isPrimitive()) {
			return getAbsoluteClassName();
		}
		else {
			return (String) primitiveMap.get(type);
		}
	}

	/**
	 * @return Returns the length.
	 */
	public Integer getLength() {
		return length;
	}

	/**
	 * @param length The length to set.
	 */
	public void setLength(Integer length) {
		this.length = length;
	}

	/**
	 * Return the static name for this property
	 * @return
	 */
	public String getStaticName () {
		return ir.viratech.commons.code_gen.model.util.HSUtil.getStaticName(name);
	}

	/**
	 * Return the HibernateClass representing the parent to the foreign key relationship
	 * or null if N/A
	 */
	public HibernateClass getForeignParent () {
		return HibernateMappingManager.getInstance().getHibernateClass(getAbsoluteClassName());
	}

	/**
	 * Return the "getter" scope
	 */
	public String getScopeGet () {
		return scopeGet;
	}

	/**
	 * Return the "setter" scope
	 */
	public String getScopeSet () {
		return scopeSet;
	}

	/**
	 * Return the field scope
	 */
	public String getScopeField () {
		return scopeField;
	}

	/**
	 * Return the name of a finder method to create in the DAO
	 */
	public String getFinderMethod () {
		return finderMethod;
	}

	/**
	 * Return the reserved properties associated with this element
	 */
	protected String[] getReservedProperties() {
		return IP;
	}
	private static final String[] IP = new String[] {"class", "name", "type", "column", "update", "insert", "formula", "access", "unsaved-value", "length", "unique", "not-null", "alternate-key", ATTRIBUTE_INITIALIZATION, ATTRIBUTE_INITIALIZATION_ORDER, "field-description", "scope-set", "scope-get", "scope-field", "use-in-equals", "property-type", "gen-property", ATTRIBUTE_REVERSE};

	/**
	 * Compare this to another object
	 */
	public int compareTo(HibernateClassProperty arg0) {
		return getPropName().compareTo(arg0.getPropName());
	}
	
	@Override
	public String getInitializationExpression() {
		return this.initializationExpression;
	}
	
	@Override
	public Double getInitializationOrder() {
		return initializationOrder;
	}
	
}