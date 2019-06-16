package ir.viratech.commons.code_gen.hsynch;

import ir.viratech.commons.code_gen.util.GenerationConfiguration;

import java.io.File;
import java.util.Collection;

public class HSConfiguration extends GenerationConfiguration {
	
	/**
	 * Provides the hbm base directory.
	 * example: src/ir/viratech/rest_proj/core/db/hbm
	 * @return
	 */
	public File getHbmBaseDirectory() {
		return this.getProjectFile(this.getRequiredProperty(Constants.PROP_HBM_LOCATION));
	}
	
	public Collection<File> getHbmFiles() {
		return HSynchUtil.findHbmFiles(this.getHbmBaseDirectory());
	}

	
	public HSConfiguration(File projectDirectory) {
		super(projectDirectory);
	}
	
	public HSConfiguration(String projectDirectory) {
		super(projectDirectory);
	}
}
