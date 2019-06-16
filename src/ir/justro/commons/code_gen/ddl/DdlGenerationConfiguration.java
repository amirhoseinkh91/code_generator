package ir.justro.commons.code_gen.ddl;

import ir.viratech.commons.code_gen.hsynch.Constants;
import ir.justro.commons.code_gen.hsynch.HSConfiguration;
import ir.justro.commons.code_gen.hsynch.HSynchUtil;

import java.io.File;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

public class DdlGenerationConfiguration extends HSConfiguration {
	
	/**
	 * Provides the required Hibernate dialect.
	 * example: org.hibernate.dialect.PostgreSQLDialect
	 * @return the required Hibernate dialect
	 */
	public String getHibernateDialect() {
		return this.getRequiredProperty(Constants.PROP_DDL_DIALECT);
	}
	
	/**
	 * Provides the absolute path of the output file. 
	 * example: sql/ddl/hsout.sql
	 * @return the output file location
	 */
	public String getOutputFileLocation() {
		String outputFilePath = this.getRequiredProperty(Constants.PROP_DDL_OUTPUT_FILE_PATH);
		boolean relative = !outputFilePath.startsWith("/");
		File file = relative ? this.getProjectFile(outputFilePath) : new File(outputFilePath);
		return file.getAbsolutePath();
	}

	private File getDdlSpecificHbmBaseDirectory() {
		String hbmLoc_DdlSpecific = this.getProperty(Constants.PROP_HBM_LOCATION_DDL_SPECIFIC);
		if (StringUtils.isBlank(hbmLoc_DdlSpecific))
			return null;
		File ddlSpecificHbmDirFile = this.getProjectFile(hbmLoc_DdlSpecific.trim());
		return (ddlSpecificHbmDirFile.exists() && ddlSpecificHbmDirFile.isDirectory()) ? ddlSpecificHbmDirFile : null;
	}
	
	@Override
	public Collection<File> getHbmFiles() {
		Collection<File> hbmFiles = super.getHbmFiles();
		File ddlSpecificHbmBaseDirectory = this.getDdlSpecificHbmBaseDirectory();
		if (ddlSpecificHbmBaseDirectory != null)
			hbmFiles.addAll(HSynchUtil.findHbmFiles(ddlSpecificHbmBaseDirectory));
		return hbmFiles;
	}


	private DdlGenerationConfiguration(String projectDirectory) {
		super(projectDirectory);
	}
	
	public static DdlGenerationConfiguration create(String projectDirectory, String configFileLocation) {
		DdlGenerationConfiguration conf = new DdlGenerationConfiguration(projectDirectory);
		conf.loadProp(new File(configFileLocation));
		return conf;
	}

}
