package ir.justro.commons.code_gen.ui;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;
import ir.justro.commons.code_gen.ui.model.DtoModel;
import ir.justro.commons.code_gen.ui.model.ResourceModel;
import ir.justro.commons.code_gen.ui.model.UiGenerationModel;
import ir.justro.commons.code_gen.ui.model.xml.UiGenerationXmlConfiguration;
import ir.justro.commons.code_gen.web.SchemaModel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * The Class UiGenerator.
 */
public class UiGenerator {
	private static final transient Logger logger = Logger.getLogger(UiGenerator.class);
	
	private UiGenerationXmlConfiguration uiGenerationXmlConfiguration;
	private Configuration cfg;

	/**
	 * Instantiates a new ui generator.
	 *
	 * @param srcPath the src path
	 * @param searchBase the search base
	 */
	public UiGenerator(UiGenerationXmlConfiguration uiGenerationXmlConfiguration) {
		this.uiGenerationXmlConfiguration = uiGenerationXmlConfiguration;
		
		this.cfg = new Configuration();

		// Where do we load the templates from:
		this.cfg.setClassForTemplateLoading(UiGenerator.class, "templates");

		// Some other recommended settings:
		this.cfg.setIncompatibleImprovements(new Version(2, 3, 20));
		this.cfg.setDefaultEncoding("UTF-8");
		this.cfg.setLocale(Locale.US);
		this.cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
	}



	private UiGenerationModel createModel() {
		File searchBase = uiGenerationXmlConfiguration.getVUBaseDirectory();
		String[] extensions = {"vu.xml"};
		@SuppressWarnings("unchecked")
		Collection<File> vuFiles = FileUtils.listFiles(searchBase, extensions , true);
		for (File vuFile : vuFiles) {
			logger.info("adding model: " + vuFile);
			uiGenerationXmlConfiguration.addModel(vuFile);
		}
		return uiGenerationXmlConfiguration.createUiGenerationModel();
	}




	private void generateFile(Map<String, Object> model, boolean overwrite, String templateLocation, String outputFilePath, String outputFileName) throws IOException, TemplateException {
		File filePath = new File(this.uiGenerationXmlConfiguration.getOutputSourceDirectory(), outputFilePath);
		filePath.mkdirs();
		File outputFile = new File(filePath, outputFileName);
		Template template = this.cfg.getTemplate(templateLocation);
		if (!overwrite && outputFile.exists()) {
			return;
		}
		logger.info("creating resource: " + outputFile.getAbsolutePath());
		Writer fileWriter = new FileWriter(outputFile);
		try {
			template.process(model, fileWriter);
		} finally {
			fileWriter.close();
		}
	}

	private void generateJavaClass(Object dtoModel, boolean overwrite, String templateLocation, String packageName, String className) throws IOException, TemplateException {
		Map<String, Object>	model = new HashMap<String, Object>();
		model.put("model", dtoModel);
		model.put("packageName", packageName);
		model.put("className", className);
		String outputFilePath = packageName.replace(".", File.separator);
		String outputFileName = className + ".java";
		this.generateFile(model, overwrite, templateLocation, outputFilePath, outputFileName);
	}



	private void generateBaseFullDTO(DtoModel dtoModel) throws IOException, TemplateException {
		String packageName = dtoModel.getDtoPackage() + ".base";
		String className = "Base" + dtoModel.getDtoName();
		this.generateJavaClass(dtoModel, true, "dto/BaseFullDTO.ftl", packageName, className);
	}
	private void generateFullDtoIfNeeded(DtoModel dtoModel) throws IOException, TemplateException {
		String packageName = dtoModel.getDtoPackage() + ".dto";
		String className = dtoModel.getDtoName();
		this.generateJavaClass(dtoModel, false, "dto/FullDTO.ftl", packageName, className);
	}
	private void generateDto(DtoModel dtoModel) throws IOException, TemplateException {
		this.generateBaseFullDTO(dtoModel);
		this.generateFullDtoIfNeeded(dtoModel);
	}
	
	private void generateJsonSchema(ResourceModel resourceModel, UiGenerationModel uiGenerationModel) throws IOException, TemplateException{
		File filePath = uiGenerationXmlConfiguration.getJsonSchemasDirectory();
		filePath.mkdirs();
		String outputFileName = resourceModel.getResourcePath()+".json";
		File outputFile = new File(filePath, outputFileName.toLowerCase());
		
		new SchemaModel(resourceModel, uiGenerationModel).generateJson(outputFile);
	}



	private void generateBaseResource(ResourceModel resourceModel) throws IOException, TemplateException {
		String packageName = resourceModel.getResourcePackage() + ".base";
		String className = "Base" + resourceModel.getResourceName();
		this.generateJavaClass(resourceModel, true, "resource/BaseResource.ftl", packageName, className);
	}
	private void generateResourceIfNeeded(ResourceModel resourceModel) throws IOException, TemplateException {
		String packageName = resourceModel.getResourcePackage();
		String className = resourceModel.getResourceName();
		this.generateJavaClass(resourceModel, false, "resource/Resource.ftl", packageName, className);
	}
	private void generateResource(ResourceModel resourceModel) throws IOException, TemplateException {
		this.generateBaseResource(resourceModel);
		this.generateResourceIfNeeded(resourceModel);
	}

	private void generate(UiGenerationModel uiGenerationModel) throws IOException, TemplateException {
		for (DtoModel dtoModel : uiGenerationModel.getDtoModels()) {
			logger.info("generating from dto model: " + dtoModel.getDtoName());
			if (logger.isDebugEnabled())
				logger.debug("dto model: " + dtoModel);
			this.generateDto(dtoModel);
		}
		
		for (ResourceModel resourceModel : uiGenerationModel.getResourceModels()) {
			logger.info("generating from resource model: " + resourceModel);
			this.generateResource(resourceModel);
		}
		
		if (this.uiGenerationXmlConfiguration.isJsonSchemaGenerationEnabled()) {
			for (ResourceModel resourceModel : uiGenerationModel.getResourceModels()) {
				logger.info("generating from json schema: " + resourceModel);
				this.generateJsonSchema(resourceModel, uiGenerationModel);
			}
		}
	}

	private void generateAll() throws IOException, TemplateException {
		UiGenerationModel uiGenerationModel = this.createModel();
		this.generate(uiGenerationModel);
	}


	/**
	 * Generates UI from .vu.xml files
	 *
	 * @param args the arguments
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws TemplateException the template exception
	 */
	public static void main(String[] args) throws IOException, TemplateException {
		if (args.length != 2) {
			System.err.println("Usage: "+UiGenerator.class.getSimpleName()+" <project-dir> <config-file>");
			System.exit(1);
		}
		
		new UiGenerator(UiGenerationXmlConfiguration.create(args[0], args[1])).generateAll();
	}

}
