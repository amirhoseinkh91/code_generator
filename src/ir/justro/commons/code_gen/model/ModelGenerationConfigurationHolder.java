package ir.justro.commons.code_gen.model;

public class ModelGenerationConfigurationHolder {
	
	private static ModelGenerationConfiguration modelGenerationConfiguration;
	
	public static ModelGenerationConfiguration getModelGenerationConfiguration() {
		return modelGenerationConfiguration;
	}
	
	public static void setModelGenerationConfiguration(ModelGenerationConfiguration modelGenerationConfiguration) {
		ModelGenerationConfigurationHolder.modelGenerationConfiguration = modelGenerationConfiguration;
	}
	
	
	public static String getProperty(String key) {
		return getModelGenerationConfiguration().getProperty(key);
	}
	
	public static String getRequiredProperty(String key) {
		return getModelGenerationConfiguration().getRequiredProperty(key);
	}
	
	public static boolean getBooleanProperty(String key, boolean defaultValue) {
		return getModelGenerationConfiguration().getBooleanProperty(key, defaultValue);
	}
	
}
