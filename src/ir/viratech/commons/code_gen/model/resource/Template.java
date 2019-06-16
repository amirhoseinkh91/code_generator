package ir.viratech.commons.code_gen.model.resource;

import ir.viratech.commons.code_gen.hsynch.Constants;

import java.util.Properties;

public class Template extends AbstractResource {

	private static final String PROP_RESOURCE_TYPE = "ResourceType";
	//private static final String PROP_JAVA_CLASS = "JavaClass";
	
	public static final String TYPE_CLASS = "C";
	public static final String TYPE_RESOURCE = "R";
	
	private String type;

	/**
	 * @see ir.viratech.commons.code_gen.model.resource.AbstractResource#evaluateMetaData(java.util.Properties)
	 */
	protected void evaluateMetaData(Properties properties) {
		type = properties.getProperty(PROP_RESOURCE_TYPE);
	}

	/**
	 * Return true if this template is related to a Java class and false if not
	 */
	public boolean isClassTemplate () {
		return TYPE_CLASS.equals(type);
	}

	/**
	 * Return true if this template is related to a resource file and false if not
	 */
	public boolean isResourceTemplate () {
		return (null == type || TYPE_RESOURCE.equals(type));
	}

	protected Properties getMetaData () {
		if (null != type) {
			Properties p = new Properties();
			p.setProperty(PROP_RESOURCE_TYPE, type);
			return p;
		}
		else return null;
	}

	protected String getResourceDirectory() {
		return Constants.TEMPLATES_DIR_NAME;
	}
	protected String getFileExtension() {
		return Constants.EXTENSION_TEMPLATE;
	}
}