package ir.viratech.commons.code_gen.ui.model.xml;

import ir.viratech.commons.api.search.field.types.FieldInfo_String;
import ir.viratech.commons.code_gen.ui.UiXmlSchemaProvider;
import ir.viratech.commons.code_gen.ui.model.AbstractDtoPropertyModel;
import ir.viratech.commons.code_gen.ui.model.DtoModel;
import ir.viratech.commons.code_gen.ui.model.DtoPropertyModel_Dto;
import ir.viratech.commons.code_gen.ui.model.DtoPropertyModel_Primitive;
import ir.viratech.commons.code_gen.ui.model.ResourceModel;
import ir.viratech.commons.code_gen.ui.model.TypesUtil;
import ir.viratech.commons.code_gen.ui.model.UiGenerationModel;
import ir.viratech.commons.code_gen.util.GenerationConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * The Class UiGenerationXmlConfiguration.
 */
public class UiGenerationXmlConfiguration extends GenerationConfiguration {

	private static final String FIELD_INFO_CONTEXT_CLASS_DEFAULT_NAME = "FieldInfoContext";

	public static final String PROP_VU_BASE_DIR = "vu.BaseDir";
	public static final String PROP_SOURCE_LOCATION = "sourceLocation";
	
	public static final String PROP_DTO_DEFAULT_PARENT = "dto.parent.default";
	public static final String PROP_DTO_FIC_DEFAULT_PARENT = "dto.fic.parent.default";
	public static final String PROP_RESOURCE_DEFAULT_PARENT = "resource.parent.default";
	public static final String PROP_RESOURCE_DEFAULT_SWAGGER_GENERATION = "resource.swaggerGeneration.default";
	
	public static final String PROP_JSON_SCHEMA_GENERATION_ENABLED = "jsonSchema.generation.enabled";
	
	public static final String PROP_WEB_GENERATION_DIR = "web.generation.dir";
	
	
	private String getVUBaseDirPath() {
		return this.getRequiredProperty(PROP_VU_BASE_DIR);
	}
	
	public File getVUBaseDirectory() {
		return this.getProjectFile(this.getVUBaseDirPath());
	}
	
	
	/**
	 * Provides the output source folder name.
	 * example: src
	 * @return the output source folder name
	 */
	private String getOutputSourceFolderName() {
		return this.getRequiredProperty(PROP_SOURCE_LOCATION);
	}
	
	/**
	 * Provides the source folder in which the UI codes should be generated.
	 * @return the File of the destination source folder
	 */
	public File getOutputSourceDirectory() {
		return this.getProjectFile(this.getOutputSourceFolderName());
	}


	public boolean isJsonSchemaGenerationEnabled() {
		return this.getBooleanProperty(PROP_JSON_SCHEMA_GENERATION_ENABLED, true);
	}
	
	private String getWebGenerationDirName() {
		return this.getProperty(PROP_WEB_GENERATION_DIR, "WebDraft");
	}
	
	public File getWebGenerationDirectory() {
		return this.getProjectFile(this.getWebGenerationDirName());
	}
	
	
	
	
	
	
	private UiGenerationXmlConfiguration(String projectDirectory) {
		super(projectDirectory);
	}
	
	public static UiGenerationXmlConfiguration create(String projectDirectory, String configFileLocation) {
		UiGenerationXmlConfiguration conf = new UiGenerationXmlConfiguration(projectDirectory);
		conf.loadProp(new File(configFileLocation));
		return conf;
		
	}
	
	

	protected String doReplacements(String s, Map<String, String> replacements) {
		for (Entry<String, String> entry : replacements.entrySet()) {
			s = s.replaceAll("\\Q"+entry.getKey()+"\\E", entry.getValue());
		}
		return s;
	}
	
	protected Map<String, String> getTypeReplacements() {
		Map<String, String> replacements = new LinkedHashMap<>();
		replacements.put("(", "<");
		replacements.put(")", ">");
		return replacements;
	}

	protected String getCorrectedType(String typeStr) {
		String type1 = TypesUtil.getTypeDefinition(typeStr);
		String type2 = (type1 == null) ? typeStr : type1;
		return doReplacements(type2, getTypeReplacements());
	}


	private String getCorrectFieldInfoType(String fieldInfoType, String propType) {
		return (fieldInfoType == null) ?
				this.findFieldInfoType(propType, FieldInfo_String.class.getName()) :
					this.findFieldInfoType(fieldInfoType, fieldInfoType);
	}
	private String findFieldInfoType(String type, String def) {
		String d = TypesUtil.getFieldInfoType(type);
		if (d != null) {
			return d;
		}
		d = TypesUtil.getFieldInfoType(this.getCorrectedType(type));
		if (d != null) {
			return d;
		}
		return def;
	}




	private static final transient Logger logger = Logger.getLogger(UiGenerationXmlConfiguration.class);

	private static InputStream openStream(URL resource) {
		try {
			return resource.openStream();
		} catch (IOException e) {
			throw new RuntimeException("could not open resource '"+resource+"'", e);
		}
	}




	private List<DtoModel> dtoModels = new ArrayList<DtoModel>();
	
	private List<ResourceModel> resourceModels = new ArrayList<>();




	protected static Schema createSchema() throws SAXException {//TODO: should we create it every time?
		SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
		return schemaFactory.newSchema(UiXmlSchemaProvider.getSchemaUrl());
	}

	protected static ErrorHandler createErrorHandler() {
		return new ErrorHandler() {
			@Override
			public void warning(SAXParseException e) throws SAXException {
				throw e;
			}
			@Override
			public void fatalError(SAXParseException e) throws SAXException {
				throw e;
			}
			@Override
			public void error(SAXParseException e) throws SAXException {
				throw e;
			}
		};
	}

	protected static SAXReader createReader() throws SAXException, ParserConfigurationException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setSchema(createSchema());
		SAXParser parser = factory.newSAXParser();

		SAXReader reader = new SAXReader(parser.getXMLReader());
		reader.setValidation(false);
		reader.setErrorHandler(createErrorHandler());
		return reader;
	}

	protected static Document createDocument(InputStream resourceAsStream) throws Exception {
		return createReader().read(resourceAsStream);
	}


	@SuppressWarnings("unchecked")
	protected static List<Element> getChildren(Element element, String childName) {
		return element.elements(childName);
	}
	protected static String getAttribute(Element element, String attributeName) {
		Attribute attribute = element.attribute(attributeName);
		return (attribute == null) ? null : attribute.getValue();
	}

	protected static Boolean getBooleanAttribute(Element element, String attributeName, Boolean defaultValue) {
		String val = getAttribute(element, attributeName);
		return (val==null) ? defaultValue : Boolean.valueOf(val);
	}
	
	protected static int getIntegerAttribute(Element element, String attributeName){
		String val = getAttribute(element, attributeName);
		return (val==null) ? 0 : Integer.valueOf(val);
	}


	private static final String attr_dto_name = "name";
	private static final String attr_dtoName = "dtoName";
	private static final String attr_resourceName = "resourceName";
	private static final String attr_entityName = "entityName";
	private static final String attr_fic_name = "fieldInfoContextName";

	protected void fillPropertyModel(Element element_prop, AbstractDtoPropertyModel dtoPropertyModel) {
		String propName = getAttribute(element_prop, "name");
		String internalName = getAttribute(element_prop, "internalName");
		String propType0 = getAttribute(element_prop, "type");
		String internalType0 = getAttribute(element_prop, "internalType");
		String loadStyle = getAttribute(element_prop, "load");
		String saveStyle = getAttribute(element_prop, "save");
		boolean trackingModifications = getBooleanAttribute(element_prop, "trackModif", false);
		String toStringStyle = getAttribute(element_prop, "toString");

		String fieldInfoStyle = getAttribute(element_prop, "fieldInfo");
		Boolean searchable = getBooleanAttribute(element_prop, "searchable", null);
		String searchExpression = getAttribute(element_prop, "searchExp");
		String bundleKey = getAttribute(element_prop, "bundleKey");
		String clientWidget = getAttribute(element_prop, "client-widget");
		boolean clientRequired = getBooleanAttribute(element_prop, "client-required", false);
		int clientSearchLevel = getIntegerAttribute(element_prop, "client-searchLevel");


		if (internalName == null) {
			internalName = propName;
		}

		boolean loadable = !("disabled".equals(loadStyle));
		if (fieldInfoStyle == null) {
			fieldInfoStyle = (loadable ? "simple" : "disabled");
		}

		if (searchable == null) {
			searchable = loadable;
		}

		if (searchExpression == null) {
			searchExpression = internalName;
		}

		if (bundleKey == null) {
			bundleKey = propName;
		}

		if (propType0 == null) {
			propType0 = "String";
			logger.warn("xml default value of prop-type was not applied!!!");
		}

		if (internalType0 == null) {
			internalType0 = propType0;
		}

		String propType = this.getCorrectedType(propType0);
		String internalType = this.getCorrectedType(internalType0);

		dtoPropertyModel.setInternalName(internalName);
		dtoPropertyModel.setInternalType(internalType);
		dtoPropertyModel.setName(propName);
		dtoPropertyModel.setType(propType);
		dtoPropertyModel.setLoadStyle(loadStyle);
		dtoPropertyModel.setSaveStyle(saveStyle);
		dtoPropertyModel.setTrackingModifications(trackingModifications);
		dtoPropertyModel.setToStringStyle(toStringStyle);
		dtoPropertyModel.setFieldInfoStyle(fieldInfoStyle);
		dtoPropertyModel.setSearchable(searchable);
		dtoPropertyModel.setSearchExpression(searchExpression);
		dtoPropertyModel.setBundleKey(bundleKey);
		dtoPropertyModel.setClientWidget(clientWidget);
		dtoPropertyModel.setClientRequired(clientRequired);
		dtoPropertyModel.setClientSearchLevel(clientSearchLevel);
	}

	protected DtoPropertyModel_Primitive createPropertyModel_Primitive(Element element_prop) {
		DtoPropertyModel_Primitive dtoPropertyModel = new DtoPropertyModel_Primitive();
		this.fillPropertyModel(element_prop, dtoPropertyModel);

		String propType0 = getAttribute(element_prop, "type");
		Boolean sortable = getBooleanAttribute(element_prop, "sortable", null);
		String fieldInfoType0 = getAttribute(element_prop, "fieldInfoType");
		String typeKey = getAttribute(element_prop, "typeKey");
		//TODO: read it as child tag: String clientCustomProp = getAttribute(element_prop, "client-CustomProp");


		if (propType0 == null) {
			propType0 = "String";
			logger.warn("xml default value of prop-type was not applied!!!");
		}

		String fieldInfoType = this.getCorrectFieldInfoType(fieldInfoType0, propType0);

		dtoPropertyModel.setSortable(sortable);
		dtoPropertyModel.setFieldInfoType(fieldInfoType);
		dtoPropertyModel.setTypeKey(typeKey);
		//dtoPropertyModel.setClientCustomProp(clientCustomProp);
		return dtoPropertyModel;
	}

	protected DtoPropertyModel_Dto createPropertyModel_Dto(Element element_prop) {
		DtoPropertyModel_Dto dtoPropertyModel = new DtoPropertyModel_Dto();
		this.fillPropertyModel(element_prop, dtoPropertyModel);

		String fieldInfoContextClassName = getAttribute(element_prop, "fieldInfoContextClass");
		String findByDtoStyle0 = getAttribute(element_prop, "findByDto");
		String entityByDtoFinderStyle0 = getAttribute(element_prop, "entityByDtoFinder");

		if (fieldInfoContextClassName == null) {
			//TODO: resolve the warning on generic types.
			fieldInfoContextClassName = getTypeWithoutGeneric(dtoPropertyModel.getType())+"."+FIELD_INFO_CONTEXT_CLASS_DEFAULT_NAME+".class";
		}

		Pair<String, String> findByDtoStyles = dtoPropertyModel.calcFindByDtoStyles(findByDtoStyle0, entityByDtoFinderStyle0);
		String findByDtoStyle = findByDtoStyles.getLeft();
		String entityByDtoFinderStyle = findByDtoStyles.getRight();

		dtoPropertyModel.setFieldInfoContextClassName(fieldInfoContextClassName);
		dtoPropertyModel.setFindByDtoStyle(findByDtoStyle);
		dtoPropertyModel.setEntityByDtoFinderStyle(entityByDtoFinderStyle);
		return dtoPropertyModel;
	}

	private static String getTypeWithoutGeneric(String str){
		int n=str.indexOf('<');
		if(n>0){
			return str.substring(0,n);
		}
		return str;
	}
	
	protected AbstractDtoPropertyModel createPropertyModel(Element element_prop) {
		if (logger.isDebugEnabled()) {
			logger.debug("\t element_prop: "+element_prop);
		}

		if ("prop".equals(element_prop.getName())) {
			return this.createPropertyModel_Primitive(element_prop);
		}

		if ("prop-dto".equals(element_prop.getName())) {
			return this.createPropertyModel_Dto(element_prop);
		}

		return null;
	}

	/**
	 * Adds the model.
	 *
	 * @param modelFile the dto model file
	 * @param resourceAsStream the resource as stream
	 */
	public void addModel(String modelFile, InputStream resourceAsStream) {
		try {
			Document document = createDocument(resourceAsStream);
			Element rootElement = document.getRootElement();//uiModel
			
			List<DtoModel> localDtoModels = new ArrayList<DtoModel>();
			
			for (Element element_dto : getChildren(rootElement, "dto")) {
				if (logger.isDebugEnabled()) {
					logger.debug("element_dto: "+element_dto);
				}
				String name = getAttribute(element_dto, attr_dto_name);
				String entityName = getAttribute(element_dto, attr_entityName);
				String dtoName = getAttribute(element_dto, attr_dtoName);
				String dtoPackage = getAttribute(element_dto, "package");
				String dtoParent = getAttribute(element_dto, "parent");
				String entityPackage = getAttribute(element_dto, "entityPackage");
				boolean generatingFieldInfoContext = getBooleanAttribute(element_dto, "genFIC", false);
				String fieldInfoContextName = getAttribute(element_dto, attr_fic_name);
				String fieldInfoContextParent = getAttribute(element_dto, "FICParent");

				if (dtoName == null) {
					if (name == null) {
						throw new BadUiGenerationXmlConfigurationException("Error in "+modelFile+": both '"+attr_dto_name+"' and '"+attr_dtoName+"' are null.");
					}
					dtoName = new StringBuilder(name+"FullDTO").toString(); // Used StringBuilder to suppress SonarQube warning.
				}

				if (entityName == null) {
					if (name == null) {
						throw new BadUiGenerationXmlConfigurationException("Error in "+modelFile+": both '"+attr_dto_name+"' and '"+attr_entityName+"' are null.");
					}
					entityName = name;
				}

				if (dtoParent == null) {
					dtoParent = this.getRequiredProperty(PROP_DTO_DEFAULT_PARENT);
				}
				
				
				Map<String, String> replacements = getTypeReplacements();
				replacements.put("${entityName}", entityName);
				replacements.put("${dtoName}", dtoName);
				dtoParent = doReplacements(dtoParent, replacements);

				if (fieldInfoContextName == null) {
					fieldInfoContextName = FIELD_INFO_CONTEXT_CLASS_DEFAULT_NAME;
				}

				if (fieldInfoContextParent == null) {
					fieldInfoContextParent = this.getRequiredProperty(PROP_DTO_FIC_DEFAULT_PARENT);
				}
				fieldInfoContextParent = doReplacements(fieldInfoContextParent, replacements);

				DtoModel dtoModel = new DtoModel();
				dtoModel.setGenerationSource(modelFile);
				dtoModel.setDtoName(dtoName);
				dtoModel.setDtoPackage(dtoPackage);
				dtoModel.setDtoParent(dtoParent);
				dtoModel.setEntityName(entityName);
				dtoModel.setEntityPackage(entityPackage);
				dtoModel.setGeneratingFieldInfoContext(generatingFieldInfoContext);
				dtoModel.setFieldInfoContextName(fieldInfoContextName);
				dtoModel.setFieldInfoContextParent(fieldInfoContextParent);

				@SuppressWarnings("unchecked")
				List<Element> elements = element_dto.elements();
				for (Element element : elements) {
					AbstractDtoPropertyModel dtoPropertyModel = this.createPropertyModel(element);
					if (dtoPropertyModel != null) {
						dtoModel.addToDtoProperties(dtoPropertyModel);
						continue;
					}
					throw new RuntimeException("unknown element "+element);
				}

				this.dtoModels.add(dtoModel);
				localDtoModels.add(dtoModel);
			}

			for (Element element_resource : getChildren(rootElement, "resource")) {
				if (logger.isDebugEnabled()) {
					logger.debug("element_resource: "+element_resource);
				}
				
				String name = getAttribute(element_resource, attr_dto_name);
				String entityName = getAttribute(element_resource, attr_entityName);
				String resourceName = getAttribute(element_resource, attr_resourceName);
				String resourcePackage = getAttribute(element_resource, "package");
				String resourceParent = getAttribute(element_resource, "parent");
				String entityPackage = getAttribute(element_resource, "entityPackage");
				String fullDto = getAttribute(element_resource, "fullDto");
				String bundlePrefix = getAttribute(element_resource, "bundlePrefix");
				String entityMgr = getAttribute(element_resource, "entityMgr");
				String resourcePath = getAttribute(element_resource, "resourcePath");
				String featureEntityName = getAttribute(element_resource, "featureEntityName");
				String swaggerGen = getAttribute(element_resource, "swagger");
				
				if (resourceName == null) {
					if (name == null) {
						throw new BadUiGenerationXmlConfigurationException("Error in "+modelFile+": both '"+attr_dto_name+"' and '"+attr_dtoName+"' are null.");
					}
					resourceName = new StringBuilder(name+"Resource").toString(); // Used StringBuilder to suppress SonarQube warning.
				}

				if (entityName == null) {
					if (name == null) {
						throw new BadUiGenerationXmlConfigurationException("Error in "+modelFile+": both '"+attr_dto_name+"' and '"+attr_entityName+"' are null.");
					}
					entityName = name;
				}
				
				if (fullDto == null) {
					throw new BadUiGenerationXmlConfigurationException("Error in "+modelFile+": 'fullDto' is not provided.");
				}

				if (resourceParent == null) {
					resourceParent = this.getRequiredProperty(PROP_RESOURCE_DEFAULT_PARENT);
				}
				
				
				if (swaggerGen == null)
					swaggerGen = getProperty(PROP_RESOURCE_DEFAULT_SWAGGER_GENERATION, "enabled");
				boolean swaggerGenerationEnabled = "enabled".equals(swaggerGen);
				
				
				Map<String, String> replacements = getTypeReplacements();
				replacements.put("${entityName}", entityName);
				replacements.put("${fullDto}", fullDto);
				resourceParent = doReplacements(resourceParent, replacements);

				String searchFICName = fullDto+".FieldInfoContext";

				ResourceModel resourceModel = new ResourceModel();
				resourceModel.setGenerationSource(modelFile);
				resourceModel.setResourceName(resourceName);
				resourceModel.setResourcePackage(resourcePackage);
				resourceModel.setResourceParent(resourceParent);
				resourceModel.setEntityName(entityName);
				resourceModel.setEntityPackage(entityPackage);
				resourceModel.setFullDto(fullDto);
				resourceModel.setBundlePrefix(bundlePrefix);
				resourceModel.setEntityMgr(entityMgr);
				resourceModel.setResourcePath(resourcePath);
				resourceModel.setSearchFICName(searchFICName);
				resourceModel.setFeatureEntityName(featureEntityName);
				resourceModel.setSwaggerGenerationEnabled(swaggerGenerationEnabled);
				this.resourceModels.add(resourceModel);
			}

		} catch (Exception e) {
			throw new RuntimeException("Could not read/load the configuration.", e);
		}
	}



	/**
	 * Adds the model.
	 *
	 * @param file the file
	 */
	public void addModel(File file) {
		try {
			this.addModel(file.getPath(), new FileInputStream(file));
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Could not open file '"+file+"'", e);
		}
	}

	/**
	 * Adds the model.
	 *
	 * @param resource the resource
	 */
	public void addModel(URL resource) {
		this.addModel(resource.toExternalForm(), openStream(resource));
	}

	/**
	 * Creates the ui generation model.
	 *
	 * @return the ui generation model
	 */
	public UiGenerationModel createUiGenerationModel() {
		UiGenerationModel uiGenerationModel = new UiGenerationModel(this.dtoModels, this.resourceModels);
		uiGenerationModel.postProcess();
		return uiGenerationModel;
	}

	public File getJsonSchemasDirectory() {
		return new File(getVUBaseDirectory(), "json");
	}



}
