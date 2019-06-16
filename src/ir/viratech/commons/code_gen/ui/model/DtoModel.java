package ir.viratech.commons.code_gen.ui.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class DtoModel.
 */
public class DtoModel {
	
	private String generationSource = null;
	public String getGenerationSource() {
		return this.generationSource;
	}
	public void setGenerationSource(String generationSource) {
		this.generationSource = generationSource;
	}
	
	
	private String entityName = null;
	private String entityPackage = null; //ir....model.user

	private String dtoName = null;
	private String dtoPackage = null;//"ir......web.rest.user";

	private String dtoParent = null;

	private boolean generatingFieldInfoContext = false;
	private String fieldInfoContextParent = null;

	private String fieldInfoContextName = null;


	private List<AbstractDtoPropertyModel> dtoProperties = new ArrayList<AbstractDtoPropertyModel>();
	
	/**
	 * Gets the entity name.
	 *
	 * @return the entity name
	 */
	public String getEntityName() {
		return this.entityName;
	}
	
	/**
	 * Sets the entity name.
	 *
	 * @param entityName the new entity name
	 */
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	/**
	 * Gets the entity package.
	 *
	 * @return the entity package
	 */
	public String getEntityPackage() {
		return this.entityPackage;
	}
	
	/**
	 * Sets the entity package.
	 *
	 * @param entityPackage the new entity package
	 */
	public void setEntityPackage(String entityPackage) {
		this.entityPackage = entityPackage;
	}

	/**
	 * Gets the dto name.
	 *
	 * @return the dto name
	 */
	public String getDtoName() {
		return this.dtoName;
	}
	
	/**
	 * Sets the dto name.
	 *
	 * @param dtoName the new dto name
	 */
	public void setDtoName(String dtoName) {
		this.dtoName = dtoName;
	}

	/**
	 * Gets the dto package.
	 *
	 * @return the dto package
	 */
	public String getDtoPackage() {
		return this.dtoPackage;
	}
	
	/**
	 * Sets the dto package.
	 *
	 * @param dtoPackage the new dto package
	 */
	public void setDtoPackage(String dtoPackage) {
		this.dtoPackage = dtoPackage;
	}

	/**
	 * Gets the dto parent.
	 *
	 * @return the dto parent
	 */
	public String getDtoParent() {
		return this.dtoParent;
	}
	
	/**
	 * Sets the dto parent.
	 *
	 * @param dtoParent the new dto parent
	 */
	public void setDtoParent(String dtoParent) {
		this.dtoParent = dtoParent;
	}

	/**
	 * Checks if is generating field info context.
	 *
	 * @return true, if is generating field info context
	 */
	public boolean isGeneratingFieldInfoContext() {
		return this.generatingFieldInfoContext;
	}
	
	/**
	 * Sets the generating field info context.
	 *
	 * @param generatingFieldInfoContext the new generating field info context
	 */
	public void setGeneratingFieldInfoContext(boolean generatingFieldInfoContext) {
		this.generatingFieldInfoContext = generatingFieldInfoContext;
	}

	/**
	 * Gets the field info context parent.
	 *
	 * @return the field info context parent
	 */
	public String getFieldInfoContextParent() {
		return this.fieldInfoContextParent;
	}
	
	/**
	 * Sets the field info context parent.
	 *
	 * @param fieldInfoContextParent the new field info context parent
	 */
	public void setFieldInfoContextParent(String fieldInfoContextParent) {
		this.fieldInfoContextParent = fieldInfoContextParent;
	}


	/**
	 * Gets the field info context name.
	 *
	 * @return the field info context name
	 */
	public String getFieldInfoContextName() {
		return this.fieldInfoContextName;
	}
	
	/**
	 * Sets the field info context name.
	 *
	 * @param fieldInfoContextName the new field info context name
	 */
	public void setFieldInfoContextName(String fieldInfoContextName) {
		this.fieldInfoContextName = fieldInfoContextName;
	}




	/**
	 * Gets the dto properties.
	 *
	 * @return the dto properties
	 */
	public List<AbstractDtoPropertyModel> getDtoProperties() {
		return this.dtoProperties;
	}
	
	/**
	 * Sets the dto properties.
	 *
	 * @param dtoPropertyModels the new dto properties
	 */
	public void setDtoProperties(List<AbstractDtoPropertyModel> dtoPropertyModels) {
		this.dtoProperties = dtoPropertyModels;
	}

	/**
	 * Adds the to dto properties.
	 *
	 * @param dtoPropertyModel the dto property model
	 */
	public void addToDtoProperties(AbstractDtoPropertyModel dtoPropertyModel) {
		this.dtoProperties.add(dtoPropertyModel);
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder res = new StringBuilder(this.getClass().getSimpleName()+"{" +
				"dtoName: "+this.dtoName
				+ ", " +
				"dtoPackage: "+this.dtoPackage
				+ ", " +
				"dtoParent: "+this.dtoParent
				+ ", " +
				"entityName: "+this.entityName
				+ ", " +
				"entityPackage: "+this.entityPackage
				+ ", " +
				"genFIC: "+this.generatingFieldInfoContext
				+ ", " +
				"genSource: "+this.generationSource
				+ ", " +
				"fieldInfoContextName: "+this.fieldInfoContextName
				+ ", " +
				"fieldInfoContextParent: "+this.fieldInfoContextParent
				+ ", " +
				"dtoProperties: [\n");
		for (AbstractDtoPropertyModel dtoPropertyModel : this.dtoProperties) {
			res.append("\t"+dtoPropertyModel+"\n");
		}
		res.append("\t]" + "}");
		return res.toString();
	}
}
