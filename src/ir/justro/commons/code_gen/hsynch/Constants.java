package ir.justro.commons.code_gen.hsynch;

import org.apache.velocity.app.VelocityEngine;

public class Constants {

	public static final String PROP_CUSTOM_TEMPLATES_ENABLED = "CustomTemplatesEnabled";
	public static final String PROP_GENERATION_VALUE_OBJECT_ENABLED = "ValueObjectGenerationEnabled";
	public static final String PROP_GENERATION_DAO_ENABLED = "DAOGenerationEnabled";
	public static final String PROP_GENERATION_DAO_IFACE_ENABLED = "DAOIfaceGenerationEnabled";
	public static final String PROP_GENERATION_CUSTOM_ENABLED = "CustomGenerationEnabled";
	public static final String PROP_BASE_VO_PACKAGE_NAME = "BaseValueObjectPackageName";
	public static final String PROP_BASE_VO_PACKAGE_STYLE = "BaseValueObjectPackageStyle";
	public static final String DEFAULT_BASE_VO_PACKAGE = "base";
	public static final String PROP_BASE_DAO_EXCEPTION = "BaseDAOException";
	public static final String PROP_BASE_DAO_PACKAGE_NAME = "BaseDAOPackageName";
	public static final String PROP_BASE_DAO_PACKAGE_STYLE = "BaseDAOPackageStyle";
	public static final String PROP_DAO_PACKAGE_NAME = "DAOPackageName";
	public static final String PROP_DAO_PACKAGE_STYLE = "DAOPackageStyle";
	public static final String DEFAULT_DAO_PACKAGE = "dao";
	public static final String PROP_BASE_DAO_USE_BASE_PACKAGE = "BaseDAOUseBasePackage";
	public static final String DEFAULT_BASE_DAO_PACKAGE = "base";
	public static final String PROP_USE_CUSTOM_ROOT_DAO = "UseCustomRootDAO";
	public static final String PROP_CUSTOM_ROOT_DAO_CLASS = "CustomRootDAOClass";
	public static final String PROP_ROOT_MGR_CLASS = "RootMGRClass";	
	public static final String PROP_CONTEXT_OBJECT = "ContextObject";
	public static final String PROP_GENERATION_ENABLED = "GenerationEnabled";
	public static final String PROP_SOURCE_LOCATION = "SourceLocation";
	public static final String PROP_PROJECT_TEMPLATE_LOCATIONS = "ProjectTemplateLocations";
	public static final String PROP_TEMPLATE_PARAMETERS = "TemplateParameters";
	public static final String PROP_CONFIGURATION_FILE = "ConfigFile";
	public static final String PROP_SYNC = "sync";
	public static final String PROP_SYNC_DAO = "sync-DAO";
	public static final String PROP_SYNC_VALUE_OBJECT = "sync-VO";
	public static final String PROP_SYNC_CUSTOM = "sync-custom";
	
	public static final String PROP_HBM_LOCATION = "hbm.location";
	
	public static final String PROP_HBM_LOCATION_DDL_SPECIFIC = "hbm.location.ddlSpecific";
	public static final String PROP_DDL_DIALECT = "ddl.hibernate.dialect";
	public static final String PROP_DDL_OUTPUT_FILE_PATH = "ddl.output.path";
	
	//public static final String HS_PATH_BASE = ".hibernateSynchronizer3";
	public static final String TEMPLATES_DIR_NAME = "templates";
	public static final String SNIPPETS_DIR_NAME = "snippets";
	public static final String HS_CONFIG_FILE_NAME = "config.properties";
	//public static final String HS_CONFIG_FILE_PATH = HS_PATH_BASE+"/"+HS_CONFIG_FILE_NAME;
	
	public static final String PROP_VALUE_ABSOLUTE = "Absolute";
	public static final String PROP_VALUE_RELATIVE = "Relative";
	public static final String PROP_VALUE_SAME = "Same";
	
	public static final String EXTENSION_SNIPPET = ".svm";
	public static final String EXTENSION_TEMPLATE = ".tvm";
	public static final String EXTENSION_HBM = ".hbm.xml";

	public static final String PROP_USE_MODULAR_GENERATION = "useModularGeneration";

	public static VelocityEngine templateGenerator;

	static {
		try {
			templateGenerator = new VelocityEngine();
			templateGenerator.init();
		}
		catch (Exception e) {
		}
	}

}
