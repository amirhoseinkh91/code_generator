package ir.justro.commons.code_gen.model;

import ir.justro.commons.code_gen.model.exception.HibernateDocumentLoadException;
import ir.justro.commons.code_gen.model.mapping.HibernateDocument;
import ir.justro.commons.code_gen.model.mapping.HibernateMappingManager;
import ir.justro.commons.code_gen.model.util.Synchronizer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;


public class ModelGenerator {
	
	private static final transient Logger logger = Logger.getLogger(ModelGenerator.class);
	
	private void run(Collection<File> hbmFiles, ModelGenerationConfiguration modelGenerationConfiguration, boolean overwrite) {
		HibernateMappingManager hibernateMappingManager = HibernateMappingManager.getInstance();
		hibernateMappingManager.makeMappingFiles(hbmFiles);
		try {
			hibernateMappingManager.load();
		} catch (HibernateDocumentLoadException e) {
			logger.error("", e);
			return;
		}
		List<HibernateDocument> documents = new ArrayList<>();
		for (File file : hbmFiles) {
			HibernateDocument doc = hibernateMappingManager.getHibernateDocument(file);
			if (null != doc) {
				documents.add(doc);
			}
		}
		for (HibernateDocument doc : documents) {
			doc.postProcess();
		}
		Synchronizer synchronizer = new Synchronizer(documents, modelGenerationConfiguration.getOutputSourceDirectory(), overwrite);
		synchronizer.synchronize();

	}
	
	
	/**
	 * args[1] = project directory
	 * args[2] =  configuration file location
	 * @param args
	 */
	public static void main(String... args) {
		if (args.length != 2) {
			System.err.println("Usage: "+ModelGenerator.class.getSimpleName()+" <project-dir> <config-dir>");
			System.exit(1);
		}
		
		ModelGenerationConfiguration modelGenerationConfiguration = ModelGenerationConfiguration.create(args[0], args[1]);
		ModelGenerationConfigurationHolder.setModelGenerationConfiguration(modelGenerationConfiguration);
		new ModelGenerator().run(modelGenerationConfiguration.getHbmFiles(), modelGenerationConfiguration, false);
		System.out.println("OK");
	}

}
