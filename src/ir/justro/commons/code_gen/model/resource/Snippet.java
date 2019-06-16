package ir.justro.commons.code_gen.model.resource;

import ir.justro.commons.code_gen.hsynch.Constants;

import java.util.Properties;



public class Snippet extends AbstractResource {
	/**
	 * @see ir.justro.commons.code_gen.model.resource.AbstractResource#evaluateMetaData(java.util.Properties)
	 */
	protected void evaluateMetaData(Properties properties) {
	}
	protected String getFileExtension() {
		return Constants.EXTENSION_SNIPPET;
	}
	protected String getResourceDirectory() {
		return Constants.SNIPPETS_DIR_NAME;
	}
}
