package ir.viratech.commons.code_gen.web;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;
import ir.viratech.commons.code_gen.ui.model.xml.UiGenerationXmlConfiguration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

public class WebGenerator {
	private static final transient Logger logger = Logger.getLogger(WebGenerator.class);

	private UiGenerationXmlConfiguration uiGenerationXmlConfiguration;
	private Configuration cfg;
	private File webDir;
	
	public WebGenerator(UiGenerationXmlConfiguration uiGenerationXmlConfiguration) {
		this.uiGenerationXmlConfiguration = uiGenerationXmlConfiguration;
		this.cfg = new Configuration();

		// Where do we load the templates from:
		this.cfg.setClassForTemplateLoading(WebGenerator.class, "templates");
		this.cfg.setIncompatibleImprovements(new Version(2, 3, 20));
		this.cfg.setDefaultEncoding("ISO-8859-1");
		this.cfg.setLocale(Locale.US);
		this.cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		
		this.webDir = this.uiGenerationXmlConfiguration.getWebGenerationDirectory();
	}
	
	private void generateFile(Map<String, Object> model, File fileDir, String fileName, String templateLoc)throws IOException, TemplateException{
		
		fileDir.mkdirs();
		File file = new File(fileDir, fileName);
		
		Template template = this.cfg.getTemplate(templateLoc);
		logger.info("creating file: " + file.getAbsolutePath());
		Writer fileWriter = new FileWriter(file);
		try {
			template.process(model, fileWriter);
		} finally {
			fileWriter.close();
		}
	}
	
	private File getSchemaFile(String schemaName) {
		return new File(this.webDir, schemaName);
	}
	
	private void generateHtml(Map<String, Object> model,String schemaName)throws IOException, TemplateException{
		this.generateFile(model, this.getSchemaFile(schemaName), schemaName+".html", "html.ftl");
	}
	
	private void generateService(Map<String, Object> model,String schemaName)throws IOException, TemplateException{
		this.generateFile(model, this.getSchemaFile(schemaName), schemaName+"Srvc.js", "service.ftl");
	}
	
	private void generateController(Map<String, Object> model,String schemaName) throws IOException, TemplateException{
		this.generateFile(model, this.getSchemaFile(schemaName), schemaName+"Ctrl.js", "controller.ftl");
	}
	private void generateConfig(Map<String, Object> model,String schemaName) throws IOException, TemplateException{
		this.generateFile(model, this.getSchemaFile(schemaName), schemaName+"Config.js", "config.ftl");
	}
	private void generateIndex(Map<String, Object> model) throws IOException, TemplateException{
		this.generateFile(model, this.webDir,"index.html", "index.ftl");
	}
	private void generateModule(Map<String, Object> model) throws IOException, TemplateException{
		this.generateFile(model, this.webDir,"managementModule.js", "module.ftl");
	}
	
	public void generate(SchemaModel schemaModel) throws IOException, TemplateException{
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("schema", schemaModel);
		
		this.generateConfig(model,schemaModel.getKey());
		this.generateController(model,schemaModel.getKey());
		this.generateService(model,schemaModel.getKey());
		this.generateHtml(model,schemaModel.getKey());
	}
	
	private void generate() throws IOException, TemplateException {
		File searchBase = this.uiGenerationXmlConfiguration.getJsonSchemasDirectory();
		String[] extensions = {"json"};
		@SuppressWarnings("unchecked")
		Collection<File> jsonFiles = FileUtils.listFiles(searchBase, extensions , true);
		
		Map<String, Object> aggregateSchemaModels = new HashMap<String, Object>();
		List<String> schemaNameList = new ArrayList<String>();
		aggregateSchemaModels.put("schemaNameList", schemaNameList);
		
		for (File jsonFile : jsonFiles) {
			SchemaModel schemaModel = new SchemaModel(jsonFile);
            schemaNameList.add(schemaModel.getKey());
			this.generate(schemaModel);
		}
		this.generateIndex(aggregateSchemaModels);
		this.generateModule(aggregateSchemaModels);
	}
	
	
	public static void main(String[] args) throws IOException, TemplateException {
		if (args.length != 2) {
			System.err.println("Usage: "+WebGenerator.class.getSimpleName()+" <project-dir> <config-file>");
			System.exit(1);
		}
		
		new WebGenerator(UiGenerationXmlConfiguration.create(args[0], args[1])).generate();
	}

}
