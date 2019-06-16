package ir.justro.commons.code_gen.model.resource;

import java.io.File;
import java.util.Map;

import org.apache.velocity.context.Context;

public interface Resource {

	/**
	 * Return the name of the resource
	 */
	public String getName();


	/**
	 * Return the content of the resource
	 */
	public String getContent();

	/**
	 * Load the resource from the given file
	 * @param fileName the fileName
	 * @param is the file contents
	 */
	public void load (File file) throws Exception;



	/**
	 * Return the results of this content with the Context given
	 * @param context
	 * @return the results
	 */
	public String merge (Context context) throws Exception;

	/**
	 * Return true if the resource has been modified from the original value and false if not
	 */
	public boolean isModified();

	/**
	 * Set whether this resource has been modified or not
	 * @param modified
	 */
	public void setModified (boolean modified);
	
	/**
	 * Return the description of this resource
	 */
	public String getDescription ();
	
	/**
	 * Set the description of this resource
	 */
	public void setDescription (String description);

	/*
	 * Return the file associated with this resource
	public File getFile ();
	 */
	

	/**
	 * Return the file name associated with this resource
	 */
	public String getFileName ();
	
	/**
	 * Return the actual contents of the underlying resource
	 */
	public String getFormattedFileContents ();


	/**
	 * Evaluate the editor content and properties to return the actual contents to persist
	 * @param entryMap
	 * @param contents
	 */
	public String evaluate (Map<String, Object> entryMap, String contents);
}
