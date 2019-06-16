package ir.viratech.commons.code_gen.model.mapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.ComparatorUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.comparators.ComparableComparator;
import org.w3c.dom.Node;

import ir.viratech.commons.code_gen.hsynch.Constants;
import ir.viratech.commons.code_gen.model.ModelGenerationConfigurationHolder;
import ir.viratech.commons.code_gen.model.exception.AttributeNotSpecifiedException;
import ir.viratech.commons.code_gen.model.exception.TransientPropertyException;
import ir.viratech.commons.code_gen.model.util.HSUtil;
import ir.viratech.commons.code_gen.model.util.XmlUtil;



public class HibernateClass extends BaseElement implements Comparable<HibernateClass> {
	
	public static final int TYPE_CLASS = 1;
	public static final int TYPE_SUBCLASS = 2;
	public static final int TYPE_JOINED_SUBCLASS = 3;
	public static final int TYPE_COMPONENT = 4;
	public static final int TYPE_UNION_SUBCLASS = 5;

	private HibernateDocument document;
	private HibernateClass parent;
	private String packageName;
	private String proxy;
	private String tableName;
	private String tableSchemaName;
	protected String absoluteValueObjectClassName;
	private HibernateClassId id;
	private HibernateClassProperty version;
	private HibernateClassProperty timestamp;

	private List<HibernateClassProperty> properties = new ArrayList<>();
	private List<HibernateClassProperty> manyToOneList = new ArrayList<>();
	private List<HibernateClassProperty> oneToOneList = new ArrayList<>();
	private List<HibernateClassCollectionProperty> collectionList = new ArrayList<>();
	private List<HibernateComponentClass> componentList = new ArrayList<>();
	List<HibernateClass> subclassList = new ArrayList<>();
	private List<HibernateQuery> queries = new ArrayList<>();

	private String daoPackage;
	private String interfacePackage;
	private String baseDAOPackage;
	private String baseValueObjectPackage;
	//private String rootDAOPackage;
	private String baseRootDAOClassName;
	private String absoluteBaseRootDAOClassName;
	private String absoluteRootMGRClassName;
	//private String rootDAOClassName;
	//private String absoluteRootDAOClassName;
	
	private boolean syncDAO = true;
	private boolean syncValueObject = true;
	private boolean syncCustom = true;
	private int type;
	//private boolean isParent;
	private String scope = "public";
	
	// cache
	List<HibernateClassProperty> alternateKeys;
	List<HibernateClassProperty> requiredFields;
	Map<String, IHibernateClassProperty> allProperties;
	Map<String, IHibernateClassProperty> allPropertiesWithComposite;
	Map<String, IHibernateClassProperty> allPropertiesByColumn;

	/**
	 * Constructor for non-subclass
	 * @param node the XML node
	 * @param packageName the package name for this class
	 * @param project the Eclipse project
	 */
	public HibernateClass (Node node, String packageName, HibernateDocument document) {
		this(node, packageName, null, document);
	}

	/**
	 * Constructor for subclass
	 * @param node the XML node
	 * @param packageName the package name for this class
	 * @param parent the parent for this subclass
	 * @param project the Eclipse project
	 */
	public HibernateClass (Node node, String packageName, HibernateClass parent, HibernateDocument document) {
		this(node, packageName, parent, true, TYPE_CLASS, document);
	}

	/**
	 * Constructor for component
	 * @param node the XML node
	 * @param packageName the package name for this class
	 * @param parent the parent for this component
	 * @param project the Eclipse project
	 * @param validate whether or not to allow full data
	 * @param type the type of class (matches the TYPE statics in this class)
	 */
	public HibernateClass (Node node, String packageName, HibernateClass parent, boolean validate, int type, HibernateDocument document) {
		this.type = type;
		this.packageName = packageName;
		this.parent = parent;
		this.document = document;
		this.tableSchemaName = document.getSchemaName();
		setNode(node);
		for (Node attNode : XmlUtil.getAttributesIterable(node)) {
			if (attNode.getNodeName().equals("table")) {
				tableName = attNode.getNodeValue();
			}
			if (attNode.getNodeName().equals("schema")) {
				tableSchemaName = attNode.getNodeValue();
			}
			if (attNode.getNodeName().equals("proxy")) {
				proxy = attNode.getNodeValue();
			}
			else if ((type == TYPE_CLASS || type == TYPE_JOINED_SUBCLASS || type == TYPE_UNION_SUBCLASS || type == TYPE_SUBCLASS)  
					&& attNode.getNodeName().equals("name")) {
				setValueObjectClassName(attNode.getNodeValue());
			}
		}
		
		tableName = HSUtil.addSchemaName(tableSchemaName, tableName); 

		handleChildNodes(node);
		saveMetaData(node);
		id = new HibernateClassId(this, node, packageName);
		if (!id.exists()) {
			id = null;
		}
		if (validate && (null == absoluteValueObjectClassName || absoluteValueObjectClassName.length() == 0)) {
			throw new AttributeNotSpecifiedException(node, "name");
		}
		if (null != getAbsoluteValueObjectProxyClassName() && getAbsoluteValueObjectProxyClassName().equals(getAbsoluteValueObjectClassName())) {
			proxy = null;
		}
	}

	private void handleChildNodes(Node node) {
		if (!node.hasChildNodes())
			return;
		for (final Node child : XmlUtil.getChildrenIterable(node)) {
			String childNodeName = child.getNodeName();
			if (childNodeName.equals("meta")) {
				String key = XmlUtil.getAttributeValue(child, "attribute");
				// check for the auto-DAO meta attribute for class level disabling of the DAO generation
				String value = getNodeText(child);
				if (null != key && null != value) {
					if (null != key && null != value) {
						if (key.equals(Constants.PROP_SYNC_DAO) && HSUtil.isFalse(value)) {
							syncDAO = false;
						}
						else if (key.equals(Constants.PROP_SYNC_VALUE_OBJECT) && HSUtil.isFalse(value)) {
							syncValueObject = false;
						}
						else if (key.equals(Constants.PROP_SYNC_CUSTOM) && HSUtil.isFalse(value)) {
							syncCustom = false;
						}
						else if (key.equals("scope-class")) {
							scope = value;
						}
					}
				}
			}
			try {
				if (childNodeName.equals("property")) {
			        properties.add(new HibernateClassProperty(this, child));
				}
				else if (childNodeName.equals("many-to-one")) {
					manyToOneList.add(new HibernateClassProperty(this, child, HibernateClassProperty.TYPE_MANY_TO_ONE, packageName));
				}
				else if (childNodeName.equals("one-to-one")) {
					oneToOneList.add(new HibernateClassProperty(this, child, HibernateClassProperty.TYPE_ONE_TO_ONE, packageName));
				}
				else if (childNodeName.equals(HibernateClassCollectionProperty.TYPE_SET)) {
					collectionList.add(new HibernateClassCollectionProperty(this, child, HibernateClassCollectionProperty.TYPE_SET, packageName));
				}
				else if (childNodeName.equals(HibernateClassCollectionProperty.TYPE_ARRAY) || childNodeName.equals(HibernateClassCollectionProperty.TYPE_PRIMITIVE_ARRAY)) {
					collectionList.add(new HibernateClassCollectionProperty(this, child, childNodeName, packageName));
				}
				else if (childNodeName.equals(HibernateClassCollectionProperty.TYPE_BAG)) {
					collectionList.add(new HibernateClassCollectionProperty(this, child, HibernateClassCollectionProperty.TYPE_BAG, packageName));
				}
				else if (childNodeName.equals(HibernateClassCollectionProperty.TYPE_LIST)) {
					collectionList.add(new HibernateClassCollectionProperty(this, child, HibernateClassCollectionProperty.TYPE_LIST, packageName));
				}
				else if (childNodeName.equals(HibernateClassCollectionProperty.TYPE_MAP)) {
					collectionList.add(new HibernateClassCollectionProperty(this, child, HibernateClassCollectionProperty.TYPE_MAP, packageName));
				}
				else if (childNodeName.equals("version")) {
					this.version = new HibernateClassProperty(this, child);
				}
				else if (childNodeName.equals("timestamp")) {
					this.timestamp = new HibernateClassProperty(this, child, false);
					if (null == this.timestamp.getType()) this.timestamp.setType(Date.class.getName());
				}
				else if (childNodeName.equals("component")) {
					componentList.add(new HibernateComponentClass(child, packageName, this, false, document));
				}
				else if (childNodeName.equals("dynamic-component")) {
					componentList.add(new HibernateComponentClass(child, packageName, this, true, document));
				}
				else if (childNodeName.equals("subclass")) {
				    HibernateClass subclass = new HibernateClass(child, packageName, this, true, TYPE_SUBCLASS, document);
					subclassList.add(subclass);
					document.addClass(subclass);
				}
				else if (childNodeName.equals("joined-subclass")) {
				    HibernateClass subclass = new HibernateClass(child, packageName, this, true, TYPE_JOINED_SUBCLASS, document);
					subclassList.add(subclass);
					document.addClass(subclass);
				}
				else if (childNodeName.equals("union-subclass")) {
				    HibernateClass subclass = new HibernateClass(child, packageName, this, true, TYPE_UNION_SUBCLASS, document);
					subclassList.add(subclass);
					document.addClass(subclass);
				}
				else if (childNodeName.equals("join")) {
					handleChildNodes(child);
				}
			}
			catch (TransientPropertyException e)
			{}
		}

	}

	/**
	 * Set the name of this class
	 * @param name
	 */
	protected void setValueObjectClassName (String name) {
		if (null != packageName && name.indexOf(".") < 0) {
			absoluteValueObjectClassName = packageName + "." + name;
		}
		else {
			absoluteValueObjectClassName = name;
		}
	}

	/**
	 * Return the name of the extension class without any package prefix that represents the
	 * value object used by hibernate for persistance
	 */
	public String getValueObjectClassName() {
		if (null != absoluteValueObjectClassName) {
			return ir.viratech.commons.code_gen.model.util.HSUtil.getClassPart(absoluteValueObjectClassName);
		}
		else {
			return null;
		}
	}

	/**
	 * Return the fully qualified name of the extension class that represents the value object
	 * used by hibernate for persistance
	 * @return the fully qualified class name
	 */
	public String getAbsoluteValueObjectClassName() {
		return absoluteValueObjectClassName;
	}

	/**
	 * Return the relative class name of the proxy or null if N/A
	 */
	public String getValueObjectProxyClassName () {
		if (null == proxy) return null;
		else if (proxy.indexOf('.') >= 0)
			return proxy.substring(proxy.lastIndexOf('.') + 1, proxy.length());
		else return proxy;
	}

	/**
	 * Return the fully qualitifed name of the proxy or null if N/A
	 */
	public String getAbsoluteValueObjectProxyClassName () {
		if (null == proxy) return null;
		return getProxyPackage() + "." + getValueObjectProxyClassName();
	}

	/**
	 * Return the name of the extension class without any package prefix that represents the
	 * value object used by hibernate for persistance (or the proxy if exists)
	 */
	public String getValueObjectSignatureClassName() {
		String absoluteSignatureClassName = getAbsoluteValueObjectSignatureClassName();
		if (null != absoluteSignatureClassName) {
			return ir.viratech.commons.code_gen.model.util.HSUtil.getClassPart(absoluteSignatureClassName);
		}
		else {
			return null;
		}
	}

	/**
	 * Return the fully qualified name of the extension class that represents the business object
	 * used by hibernate for persistance (or the proxy if exists)
	 * @return the fully qualified class name
	 */
	public String getAbsoluteValueObjectSignatureClassName() {
		String proxyClassName = getAbsoluteValueObjectProxyClassName();
		if (null != proxyClassName) return proxyClassName;
		else return absoluteValueObjectClassName;
	}

	/**
	 * Return the name without the package prefix of the base value object class used for the hibernate persistance 
	 */
	public String getBaseValueObjectClassName () {
		return "Base" + getValueObjectClassName();
	}

	/**
	 * Return the name without the package prefix of the base DAO class used as the SessionFactory wrapper
	 */
	public String getBaseDAOClassName () {
		return "Base" + getValueObjectClassName() + "DAO";
	}

	/**
	 * Return the name without the package prefix of the base DAO class used as the SessionFactory wrapper
	 */
	public String getBaseMGRClassName () {
		return "Base" + getValueObjectClassName() + "Mgr";
	}

	/**
	 * Return the name without the package prefix of the extension DAO class 
	 */
	public String getDAOClassName () {
		return getValueObjectClassName() + "DAO";
	}

	/**
	 * Return the name without the package prefix of the extension MGR class 
	 */
	public String getMGRClassName () {
		return getValueObjectClassName() + "Mgr";
	}

	/**
	 * Return the fully qualified class name of the DAO used for the hibernate persistance
	 */
	public String getAbsoluteDAOClassName () {
		return getDAOPackage() + "." + getDAOClassName();
	}

	/**
	 * Return the fully qualified class name of the MGR
	 */
	public String getAbsoluteMGRClassName () {
		return getMGRPackage() + "." + getMGRClassName();
	}

	/**
	 * Return the name without the package prefix of the extension DAO class used as the SessionFactory wrapper
	 */
	public String getDAOInterfaceName () {
		return getValueObjectClassName() + "DAO";
	}

	/**
	 * Return the fully qualified class name of the DAO used for the hibernate persistance
	 */
	public String getAbsoluteDAOInterfaceName () {
		return getInterfacePackage() + "." + getDAOInterfaceName();
	}

	/**
	 * Return the fully qualified class name of the Base DAO used for the hibernate persistance
	 */
	public String getAbsoluteBaseDAOClassName () {
		return getBaseDAOPackage() + "." + getBaseDAOClassName();
	}

	/**
	 * Return the fully qualified class name of the Base MGR
	 */
	public String getAbsoluteBaseMGRClassName () {
		return getBaseMGRPackage() + "." + getBaseMGRClassName();
	}
	
	/**
	 * Return the fully qualified class name of the base value object class used for the hibernate persistance
	 */
	public String getAbsoluteBaseValueObjectClassName () {
		return getBaseValueObjectPackage() + "." + getBaseValueObjectClassName();
	}

	/**
	 * Return the package prefix without the class name of the extension class that represents
	 * the value object used by hibernate for persistance
	 * @return the package name
	 */
	public String getValueObjectPackage () {
		return HSUtil.getPackagePart(absoluteValueObjectClassName);
	}

	/**
	 * Return the package of the proxy for this class or null if N/A
	 */
	public String getProxyPackage () {
		if (null == proxy) return null;
		else return getValueObjectPackage();
	}

	/**
	 * Return the package prefix that relates to the base DAO class used as the wrapper to the SessionFactory access
	 */
	public String getBaseValueObjectPackage () {
		if (null == baseValueObjectPackage) {
			String basePackageStyle = ModelGenerationConfigurationHolder.getProperty(Constants.PROP_BASE_VO_PACKAGE_STYLE);
			String basePackageName = ModelGenerationConfigurationHolder.getProperty(Constants.PROP_BASE_VO_PACKAGE_NAME);
			if (Constants.PROP_VALUE_SAME.equals(basePackageStyle)) {
				baseValueObjectPackage = getValueObjectPackage();
			}
			else if (Constants.PROP_VALUE_ABSOLUTE.equals(basePackageStyle)) {
				if (null == basePackageName) basePackageName = Constants.DEFAULT_BASE_VO_PACKAGE;
				baseValueObjectPackage = basePackageName;
			}
			else {
				// relative
				if (null == basePackageName) basePackageName = Constants.DEFAULT_BASE_VO_PACKAGE;
				baseValueObjectPackage = HSUtil.addPackageExtension(getValueObjectPackage(), basePackageName); 
			}
		}
		return baseValueObjectPackage;
	}

	/**
	 * Return the package prefix that relates to the extension DAO class used as the wrapper to the SessionFactory access
	 */
	public String getDAOPackage () {
		if (null == daoPackage) {
			String daoPackageStyle = ModelGenerationConfigurationHolder.getProperty(Constants.PROP_DAO_PACKAGE_STYLE);
			String daoPackageName = ModelGenerationConfigurationHolder.getProperty(Constants.PROP_DAO_PACKAGE_NAME);
			if (Constants.PROP_VALUE_SAME.equals(daoPackageStyle)) {
				daoPackage = getValueObjectPackage();
			}
			else if (Constants.PROP_VALUE_ABSOLUTE.equals(daoPackageStyle)) {
				if (null == daoPackageName) daoPackageName = Constants.DEFAULT_DAO_PACKAGE;
				daoPackage = daoPackageName;
			}
			else {
				if (null == daoPackageName) daoPackageName = Constants.DEFAULT_DAO_PACKAGE;
				daoPackage = HSUtil.addPackageExtension(getValueObjectPackage(), daoPackageName); 
			}
		}
		return daoPackage;
	}

	/**
	 * Return the package prefix that relates to the extension MGR class 
	 */
	public String getMGRPackage () {
		String bdp = getBaseDAOPackage();
		int i=bdp.lastIndexOf(".");
		return bdp.substring(0, i+1)+"logic";
	}

	public String getInterfacePackage () {
		if (null == interfacePackage) {
			interfacePackage = getDAOPackage() + ".iface";
		}
		return interfacePackage;
	}

	/**
	 * Return the package prefix that relates to the base DAO class used as the wrapper to the SessionFactory access
	 */
	public String getBaseDAOPackage () {
		if (null == baseDAOPackage) {
			boolean useBaseBusinessObjPackage = true;
			try {
				String s = ModelGenerationConfigurationHolder.getProperty(Constants.PROP_BASE_DAO_USE_BASE_PACKAGE);
				if (null != s) useBaseBusinessObjPackage = new Boolean(s).booleanValue();
			}
			catch (Exception e) {}
			if (useBaseBusinessObjPackage) {
				baseDAOPackage = getBaseValueObjectPackage();
			}
			else {
				String baseDAOPackageStyle = ModelGenerationConfigurationHolder.getProperty(Constants.PROP_BASE_DAO_PACKAGE_STYLE);
				String baseDAOPackageName = ModelGenerationConfigurationHolder.getProperty(Constants.PROP_BASE_DAO_PACKAGE_NAME);
				if (Constants.PROP_VALUE_SAME.equals(baseDAOPackageStyle)) {
					baseDAOPackage = getDAOPackage();
				}
				else if (Constants.PROP_VALUE_ABSOLUTE.equals(baseDAOPackageStyle)) {
					if (null == baseDAOPackageName) baseDAOPackageName = Constants.DEFAULT_BASE_DAO_PACKAGE;
					baseDAOPackage = baseDAOPackageName;
				}
				else {
					if (null == baseDAOPackageName) baseDAOPackageName = Constants.DEFAULT_BASE_DAO_PACKAGE;
					baseDAOPackage = HSUtil.addPackageExtension(getDAOPackage(), baseDAOPackageName); 
				}
			}
		}
		return baseDAOPackage;
	}

	/**
	 * Return the package prefix that relates to the base MGR class 
	 */
	public String getBaseMGRPackage () {
		return getBaseDAOPackage();
	}
	
	/**
	 * Return the package prefix of the root DAO class.
	 */
	public String getRootDAOPackage () {
		return getDAOPackage();
	}

	/**
	 * Return a descriptive label based on the class name (or using the 'label' meta override)
	 */
	public String getLabel() {
		if (getCustomProperties().size() == 0)
			return ir.viratech.commons.code_gen.model.util.HSUtil.getPropDescription(getValueObjectClassName());
		else {
			String label = get(IHibernateClassProperty.LABEL_METADATA);
			if (null == label)
				return ir.viratech.commons.code_gen.model.util.HSUtil.getPropDescription(getValueObjectClassName());
			else
				return label;
		}
	};

	/**
	 * Return the variable name related to this class that will be used for the generation
	 */
	public String getVarName () {
		return ir.viratech.commons.code_gen.model.util.HSUtil.firstLetterLower(getValueObjectClassName());
	}

	/**
	 * Return the variable name related to DAO this class that will be used for the generation
	 */
	public String getDAOVarName () {
		return ir.viratech.commons.code_gen.model.util.HSUtil.firstLetterLower(getDAOClassName());
	}

	/**
	 * Return the id object for the given HibernateClass
	 */
	public HibernateClassId getId() {
		if (type == TYPE_COMPONENT) return getId(false);
		else return getId(true);
	}

	/**
	 * Return the id object for the given HibernateClass
	 */
	public HibernateClassId getId(boolean goToParent) {
		if (goToParent) {
			if (null != parent) return parent.getId();
			else return id;
		}
		else return id;
	}

	/**
	 * return the properties that represent the standard columns in the that relating to this hibernate class
	 * @return a List of HibernateClassProperty objects
	 */
	public List<HibernateClassProperty> getProperties() {
		return properties;
	}
	
	/**
	 * Return a list of all properties including the properties are hidden inside the components that belong to
	 * this class
	 * @return a list of IHibernateClassProperty objects
	 */
	public List<IHibernateClassProperty> getPropertiesWithComponents () {
	    if (null == propertiesWithComponents) {
	        propertiesWithComponents = new ArrayList<>();
	        for (Iterator<HibernateClassProperty> i=getProperties().iterator(); i.hasNext(); ) {
	            propertiesWithComponents.add(i.next());
	        }
	        for (Iterator<HibernateComponentClass> i=getComponentList().iterator(); i.hasNext(); ) {
	            propertiesWithComponents.add(i.next());
	        }
	    }
	    return propertiesWithComponents;
	}
	private List<IHibernateClassProperty> propertiesWithComponents;

	/**
	 * Return the objects that represent many-to-one relationships for the hibernate class
	 * @return a List of HibernateClassProperty objects
	 */
	public List<HibernateClassProperty> getManyToOneList() {
		return manyToOneList;
	}

	/**
	 * Return the objects that represent one-to-one relationships for the hibernate class
	 * @return a List of HibernateClassProperty objects
	 */
	public List<HibernateClassProperty> getOneToOneList() {
		return oneToOneList;
	}

	/**
	 * Return the name of the table that will be used for persistance of this hibernate class
	 */
	public String getTableName() {
		if (null == tableName && null != parent) return parent.getTableName();
		else return tableName;
	}

	/**
	 * Return the list of collection objects for this hibernate class
	 * @return a List of HibernateClassCollectionProperty objects
	 */
	public List<HibernateClassCollectionProperty> getCollectionList() {
		return collectionList;
	}
	
	public void addSubclass(HibernateClass subclass) {
		this.subclassList.add(subclass);
	}
	
	/**
	 * Return a list of class that will subclass this hibernate class
	 * @return a list of HibernateClass objects
	 */
	public List<HibernateClass> getSubclassList() {
		return subclassList;
	}

	/**
	 * Return a list of the components that are defined for this class
	 * @return a list of ComponentHibernateClass objects
	 */
	public List<HibernateComponentClass> getComponentList() {
		return componentList;
	}
	
	/**
	 * Return the parent HibernateClass object if this is a subclass or null if N/A
	 * @return the parent of the subclass
	 */
	public HibernateClass getParent() {
		return parent;
	}

	/**
	 * Return true if this class is a subclass or false if not
	 */
	public boolean isSubclass () {
		return (null != getParent());
	}

	/**
	 * Return a list of objects represent the query definitions related to this class.
	 * <p><b>Note: since queries are not defined within the class node, queries will
	 * only be added if there is a single class definition in the mapping configuration
	 * file.</b></p>
	 * @return a list of HibernateQuery objects
	 */
	public List<HibernateQuery> getQueries() {
		return queries;
	}

	/**
	 * Set the queries for this class
	 * @param a List of HibernateQuery objects
	 */
	public void setQueries(List<HibernateQuery> queries) {
		this.queries = queries;
	}

	/**
	 * @return true if this class is been allowed to auto-sync the related DAO class and false if not 
	 */
	public boolean canSyncDAO() {
		return syncDAO;
	}

	/**
	 * @return true if this class is been allowed to auto-sync the value object files and false if not 
	 */
	public boolean canSyncValueObject() {
		return syncValueObject;
	}

	/**
	 * @return true if this class is been allowed to auto-sync the custom templates and false if not
	 */
	public boolean canSyncCustom() {
		return syncCustom;
	}

	/**
	 * Return the fully qualified class name of the base root DAO class.
	 */
	public String getAbsoluteBaseRootDAOClassName () {
		if (null == absoluteBaseRootDAOClassName) {
			absoluteBaseRootDAOClassName = ModelGenerationConfigurationHolder.getRequiredProperty(Constants.PROP_CUSTOM_ROOT_DAO_CLASS);
		}
		return absoluteBaseRootDAOClassName;
	}

	/**
	 * Return the fully qualified class name of the base root DAO class.
	 */
	public String getAbsoluteRootMGRClassName () {
		if (null == absoluteRootMGRClassName) {
			absoluteRootMGRClassName = ModelGenerationConfigurationHolder.getRequiredProperty(Constants.PROP_ROOT_MGR_CLASS);
		}
		return absoluteRootMGRClassName;
	}

	/**
	 * Return the fully qualified class name of the root DAO class.
	 */
	public String getAbsoluteRootDAOClassName () {
		return getDAOPackage() + "." + getRootDAOClassName();
	}

	/**
	 * Return the class name of the root DAO class without the package prefix.
	 */
	public String getRootDAOClassName () {
		return "AbstractEntityDAO";
	}

	/**
	 * @return true if this class uses a custom DAO and false if not
	 */
	public boolean useCustomDAO () {
		String useCustomDAO = ModelGenerationConfigurationHolder.getProperty(Constants.PROP_USE_CUSTOM_ROOT_DAO);
		if (null != useCustomDAO && useCustomDAO.equalsIgnoreCase(Boolean.TRUE.toString())) {
			return (null != ModelGenerationConfigurationHolder.getProperty(Constants.PROP_CUSTOM_ROOT_DAO_CLASS));
		}
		return false;
	}
	
	/**
	 * Return the class name of the root DAO class without the package prefix.
	 */
	public String getBaseRootDAOClassName () {
		if (null == baseRootDAOClassName) {
			String s = getAbsoluteBaseRootDAOClassName();
			int index = s.lastIndexOf(".");
			if (index > 0) baseRootDAOClassName = s.substring(index+1, s.length());
			else baseRootDAOClassName = s;
		}
		return baseRootDAOClassName;
	}

	/**
	 * Return the HibernateClassProperty that relates to the version or null if N/A
	 */
	public HibernateClassProperty getVersion() {
		return version;
	}

	/**
	 * Return the HibernateClassProperty that relates to the timestamp or null if N/A
	 */
	public HibernateClassProperty getTimestamp() {
		return timestamp;
	}

	/**
	 * Return a list of properties that relate to the alternate keys (or a 0 length list if N/A)
	 * @return a list of IHibernateClassProperty objects
	 */
	public List<HibernateClassProperty> getAlternateKeys() {
		if (null == alternateKeys) {
			alternateKeys = new ArrayList<>();
			for (Iterator<HibernateClassProperty> i=getProperties().iterator(); i.hasNext(); ) {
				HibernateClassProperty prop = (HibernateClassProperty) i.next();
				if (prop.isAlternateKey()) alternateKeys.add(prop);
			}
			for (Iterator<HibernateClassProperty> i=getOneToOneList().iterator(); i.hasNext(); ) {
				HibernateClassProperty prop = (HibernateClassProperty) i.next();
				if (prop.isAlternateKey()) alternateKeys.add(prop);
			}
			for (Iterator<HibernateClassProperty> i=getManyToOneList().iterator(); i.hasNext(); ) {
				HibernateClassProperty prop = (HibernateClassProperty) i.next();
				if (prop.isAlternateKey()) alternateKeys.add(prop);
			}
		}
		return alternateKeys;
	}

	/**
	 * Return a list of properties that are required
	 * @return a list of IHibernateClassProperty objects
	 */
	public List<HibernateClassProperty> getRequiredFields() {
		if (null == requiredFields) {
			requiredFields = new ArrayList<>();
			for (Iterator<HibernateClassProperty> i=getOneToOneList().iterator(); i.hasNext(); ) {
				HibernateClassProperty prop = (HibernateClassProperty) i.next();
				if (prop.isRequired()) requiredFields.add(prop);
			}
			for (Iterator<HibernateClassProperty> i=getManyToOneList().iterator(); i.hasNext(); ) {
				HibernateClassProperty prop = (HibernateClassProperty) i.next();
				if (prop.isRequired()) requiredFields.add(prop);
			}
			for (Iterator<HibernateClassProperty> i=getProperties().iterator(); i.hasNext(); ) {
				HibernateClassProperty prop = (HibernateClassProperty) i.next();
				if (prop.isRequired()) requiredFields.add(prop);
			}
		}
		return requiredFields;
	}

	/**
	 * Return the root parent of a subclass or this if no subclass
	 */
	public HibernateClass getParentRoot () {
		if (isSubclass()) return parent.getParentRoot();
		else return this;
	}

	/**
	 * Return all the properties of the class but do not include any composite key properties
	 * @return a list of IHibernateClassProperty
	 */
	public Collection<IHibernateClassProperty> getAllProperties () {
		return getAllProperties(false);
	}

	/**
	 * Return all the properties of the class
	 * @addCompositeKeyProperties set to true if you want to add properties that are part of the composite key
	 * @return a list of IHibernateClassProperty objects
	 */
	public Collection<IHibernateClassProperty> getAllProperties (boolean addCompositeKeyProperties) {
		Map<String, IHibernateClassProperty> values = null;
		if (addCompositeKeyProperties) values = allPropertiesWithComposite;
		else values = allProperties;
		if (null == values) {
			values = new LinkedHashMap<>();
			if (null != getId() && !isSubclass()) {
				if (addCompositeKeyProperties) {
					if (getId().isComposite()) {
						for (Iterator<HibernateClassProperty> i=getId().getProperties().iterator(); i.hasNext(); ) {
							HibernateClassProperty hcp = i.next();
							values.put(ir.viratech.commons.code_gen.model.util.HSUtil.firstLetterUpper(hcp.getName()), hcp);
						}
					}
				} else {
					if (!getId().isComposite() || getId().hasExternalClass()) {
						values.put(ir.viratech.commons.code_gen.model.util.HSUtil.firstLetterUpper(getId().getProperty().getName()), getId().getProperty());
					}
				}
			}
			for (Iterator<HibernateClassProperty> i=getProperties().iterator(); i.hasNext(); ) {
				HibernateClassProperty hcp = i.next();
				values.put(ir.viratech.commons.code_gen.model.util.HSUtil.firstLetterUpper(hcp.getName()), hcp);
			}
			for (Iterator<HibernateClassProperty> i=getManyToOneList().iterator(); i.hasNext(); ) {
				HibernateClassProperty hcp = i.next();
				values.put(ir.viratech.commons.code_gen.model.util.HSUtil.firstLetterUpper(hcp.getName()), hcp);
			}
			for (Iterator<HibernateClassProperty> i=getOneToOneList().iterator(); i.hasNext(); ) {
				HibernateClassProperty hcp = i.next();
				values.put(ir.viratech.commons.code_gen.model.util.HSUtil.firstLetterUpper(hcp.getName()), hcp);
			}
			for (Iterator<HibernateClassCollectionProperty> i=getCollectionList().iterator(); i.hasNext(); ) {
				HibernateClassProperty hcp = i.next();
				values.put(ir.viratech.commons.code_gen.model.util.HSUtil.firstLetterUpper(hcp.getName()), hcp);
			}
			for (Iterator<HibernateComponentClass> i=getComponentList().iterator(); i.hasNext(); ) {
				HibernateComponentClass hcc = i.next();
				for (Iterator<HibernateClassProperty> i1=hcc.getProperties().iterator(); i1.hasNext(); ) {
				    HibernateClassProperty hcp = i1.next();
				    values.put(ir.viratech.commons.code_gen.model.util.HSUtil.firstLetterUpper(hcp.getName()), hcp);
				}
			}
			if (addCompositeKeyProperties) allPropertiesWithComposite = values;
			else allProperties = values;
		}
		return values.values();
	}

	/**
	 * Return the property matching the property name
	 */
	public IHibernateClassProperty getProperty(String propName) {
		if (null == propName) return null;
		getAllProperties();
		return (IHibernateClassProperty) allProperties.get(ir.viratech.commons.code_gen.model.util.HSUtil.firstLetterUpper(propName));
	}
	
	public boolean needsInitialization() {
		if (this.isSubclass())
			return true;
		for (IHibernateClassProperty prop : this.getAllProperties()) {
			if (prop.getInitializationExpression() != null)
				return true;
		}
		return false;
	}
	
	public List<IHibernateClassProperty> getPropertiesWithInitialization() {
		List<IHibernateClassProperty> props = new ArrayList<>();
		for (IHibernateClassProperty prop : this.getAllProperties()) {
			if (prop.getInitializationExpression() != null)
				props.add(prop);
		}
		Collections.sort(props, ComparatorUtils.transformedComparator(ComparatorUtils.nullHighComparator(ComparableComparator.<Double>comparableComparator()), new Transformer<IHibernateClassProperty, Double>() {
			@Override
			public Double transform(IHibernateClassProperty prop) {
				return prop.getInitializationOrder();
			}
		}));
		return props;
	}

	/* *
	 * Return the property matching the column name
	 * /
	public IHibernateClassProperty getPropertyByColName(String colName) {
		if (null == colName) return null;
		if (null == allPropertiesByColumn) {
			allPropertiesByColumn = new HashMap<>();
			if (null != getId()) {
				if (getId().isComposite()) {
					for (Iterator<HibernateClassProperty> i=getId().getProperties().iterator(); i.hasNext(); ) {
						HibernateClassProperty hcp = i.next();
						allPropertiesByColumn.put(hcp.getColumn(), hcp);
					}
				}
				else {
					HibernateClassProperty hcp = (HibernateClassProperty) getId().getProperty();
					allPropertiesByColumn.put(hcp.getColumn(), hcp);
				}
			}
			for (Iterator<HibernateClassProperty> i=getProperties().iterator(); i.hasNext(); ) {
				HibernateClassProperty hcp = (HibernateClassProperty) i.next();
				allPropertiesByColumn.put(hcp.getColumn(), hcp);
			}
			for (Iterator<HibernateClassProperty> i=getManyToOneList().iterator(); i.hasNext(); ) {
				HibernateClassProperty hcp = (HibernateClassProperty) i.next();
				allPropertiesByColumn.put(hcp.getColumn(), hcp);
			}
			for (Iterator<HibernateClassProperty> i=getOneToOneList().iterator(); i.hasNext(); ) {
				HibernateClassProperty hcp = (HibernateClassProperty) i.next();
				allPropertiesByColumn.put(hcp.getColumn(), hcp);
			}
			for (Iterator<HibernateComponentClass> i=getComponentList().iterator(); i.hasNext(); ) {
				HibernateComponentClass hcc = (HibernateComponentClass) i.next();
				for (Iterator<HibernateClassProperty> j=hcc.getProperties().iterator(); j.hasNext(); ) {
					IHibernateClassProperty hcp = j.next();
					if (null != hcp.getColumn()) allPropertiesByColumn.put(hcp.getColumn(), hcp);
				}
			}
			if (null != getId()) {
				if (getId().isComposite()) {
					for (Iterator<HibernateClassProperty> i=getId().getProperties().iterator(); i.hasNext(); ) {
						HibernateClassProperty hcp = i.next();
						allPropertiesByColumn.put(hcp.getColumn(), hcp);
					}
				}
			}
			else {
				allProperties.put(getId().getProperty().getColumn(), getId().getProperty());
			}
		}
		return (IHibernateClassProperty) allPropertiesByColumn.get(colName);
	}
	*/
	
	/**
	 * Return true if this class has a proxy and false if not
	 */
	public boolean hasProxy () {
		return null != proxy;
	}

	public boolean isComponent () {
		return type == TYPE_COMPONENT;
	}

	/**
	 * Return the class scope of the value object 
	 * @return
	 */
	public String getValueObjectScope() {
		return scope;
	}

	/**
	 * Return the document that relates to this mapping file
	 */
	public HibernateDocument getDocument () {
		return document;
	}

	/**
	 * Return the reserved properties associated with this element
	 */
	protected String[] getReservedProperties() {
		return RP;
	}
	private static final String[] RP = new String[] {"name", "table", "discriminator", "mutable", "schema", "proxy", "dynamic-update", "dynamic-insert", "select-before-update", "polymorphism", "where", "persister", "batch-size", "optimistic-lock", "lazy", "class-description"};

	/**
	 * Compare this to another object
	 */
	public int compareTo(HibernateClass arg0) {
		return getValueObjectClassName().compareTo((arg0).getValueObjectClassName());
	}

	public void postProcess() {
		for (IHibernateClassProperty prop : this.getAllProperties()) {
			prop.postProcess();
		}
	}
}
