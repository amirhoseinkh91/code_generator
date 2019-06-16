package ir.viratech.commons.code_gen.util;

import ir.viratech.commons.code_gen.hsynch.HSConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class GenerationConfiguration {

	private static final transient Logger logger = Logger.getLogger(HSConfiguration.class);
	
	private Properties prop = new Properties();
	
	protected void loadProp(File file) {
		try {
			prop.load(new FileInputStream(file));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String getProperty(String key) {
		String val = prop.getProperty(key);
		if (logger.isDebugEnabled()) {
			logger.debug("key: " + key+" val: "+val);
		}
		return val;
	}
	
	public String getRequiredProperty(String key) {
		String val = this.getProperty(key);
		if (val == null) {
			throw new IllegalStateException("The required property '"+key+"' is not available.");
		}
		return val;
	}
	
	public String getProperty(String key, String defaultValue) {
		String val = this.getProperty(key);
		return (val == null) ? defaultValue : val;
	}
	
	public boolean getBooleanProperty(String key, boolean defaultValue) {
		String s = this.getProperty(key);
		if (null == s) return defaultValue;
		else return s.toUpperCase().startsWith("T");
	}
	
	private final File projectDirectory;

	/**
	 * Provides the project directory
	 * @return the project directory
	 */
	public File getProjectDirectory() {
		return this.projectDirectory;
	}
	
	public File getProjectFile(String path) {
		return new File(this.getProjectDirectory(), path);
	}
	
	public GenerationConfiguration(File projectDirectory) {
		this.projectDirectory = projectDirectory;
		logger.debug("projectDirectory: " + projectDirectory);
	}
	
	public GenerationConfiguration(String projectDirectory) {
		this(new File(projectDirectory));
	}
}
