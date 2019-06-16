package ir.justro.commons.code_gen.model;

import ir.justro.commons.code_gen.hsynch.Constants;
import ir.justro.commons.code_gen.hsynch.HSConfiguration;

import java.io.File;


public class ModelGenerationConfiguration extends HSConfiguration {
	
	private File configurationDirectory;

	/**
	 * Provides the configuration directory
	 * @return the configuration directory
	 */
	public File getConfigurationDirectory() {
		return this.configurationDirectory;
	}
	
	/**
	 * Provides the output source folder name.
	 * example: src
	 * @return the output source folder name
	 */
	private String getOutputSourceFolderName() {
		return this.getRequiredProperty(Constants.PROP_SOURCE_LOCATION);
	}
	
	/**
	 * Provides the source folder in which the model should be generated.
	 * @return the File of the destination source folder
	 */
	public File getOutputSourceDirectory() {
		return this.getProjectFile(this.getOutputSourceFolderName());
	}

	public boolean isModularProject() {
		return getBooleanProperty(Constants.PROP_USE_MODULAR_GENERATION,false);
	}

	private ModelGenerationConfiguration(String projectDirectory) {
		super(projectDirectory);
	}
	
	public static ModelGenerationConfiguration create(String projectDirectory, String configurationDirectory) {
		ModelGenerationConfiguration conf = new ModelGenerationConfiguration(projectDirectory);
		conf.configurationDirectory = new File(configurationDirectory);
		conf.loadProp(new File(conf.configurationDirectory, "config.properties"));
		return conf;
	}

}
