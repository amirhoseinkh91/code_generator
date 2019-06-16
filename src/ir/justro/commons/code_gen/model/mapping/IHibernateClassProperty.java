package ir.justro.commons.code_gen.model.mapping;


public interface IHibernateClassProperty {

	String LABEL_METADATA = "label";
	
	void postProcess();
	
	/**
	 * Return the defined property name for this property
	 */
	String getName();

	/**
	 * Return a descriptive label based on the property name
	 */
	String getLabel();
	
	/**
	 * Return the actual property name for this property (first letter upper case)
	 */
	String getPropName();

	/**
	 * Return the getter name (without the parenthesis) for this property
	 * @return the getter name
	 */
	String getGetterName();

	/**
	 * Return the setter name (without the parenthesis) for this property
	 * @return the setter name
	 */
	String getSetterName();

	/**
	 * Return the name used as the Java variable name for this property (first letter lower case)
	 * @return the Java variable name
	 */
	String getVarName();
	
	/**
	 * Return the fully qualified class name that represents this property
	 */
	String getAbsoluteClassName();

	/**
	 * Return the name of the class without the the package prefix that represents this property
	 */
	String getClassName ();

	/**
	 * Return the fully qualified class name or interface if applicable that represents this property
	 */
	String getAbsoluteSignatureClassName();

	/**
	 * Return the name of the class or interface if applicable without the the package prefix that represents this property
	 */
	String getSignatureClassName ();

	/**
	 * Return the package prefix for this property class without the class name
	 */
	String getPackage();

	/**
	 * Return the parent class for this property
	 * @return the parent HibernateClass
	 */
	HibernateClass getParent();
	
	/**
	 * Return the column name that this represents
	 * @return the column name
	 */
	String getColumn();

	/*
	 * Return the meta-data associated with this element
	public List getMetaData  ();
	 */

	/**
	 * Return true if the type of this property represents a primitive
	 */
	boolean isPrimitive();

	/**
	 * Return the object class representation for this class
	 */
	String getObjectClass ();

	/**
	 * Return the static name for this property
	 */
	String getStaticName ();
	
	/**
	 * How to initialize the property of a new object. 
	 * @return the initialization expression
	 */
	String getInitializationExpression();

	/**
	 * The order to initialize the property of a new object.
	 * The more the value is, the later it is initialized.
	 * Null (unspecified) values have the least priority.
	 * @return the initialization order value
	 */
	Double getInitializationOrder();

}
