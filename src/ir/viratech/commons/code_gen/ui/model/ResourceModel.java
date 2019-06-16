package ir.viratech.commons.code_gen.ui.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class ResourceModel.
 */
public class ResourceModel {

	private String generationSource = null;
	public String getGenerationSource() {
		return this.generationSource;
	}
	public void setGenerationSource(String generationSource) {
		this.generationSource = generationSource;
	}
	

	private String entityName = null;
	private String entityPackage = null; //ir....model.user

	private String resourceName = null;
	private String resourcePackage = null;//"ir......web.rest.user";

	private String resourceParent = null;

	private String fullDto = null;
	private DtoModel dtoModel;
	
	private String resourcePath;
	public String getResourcePath() {
		return this.resourcePath;
	}
	public void setResourcePath(String resourcePath) {
		this.resourcePath = resourcePath;
	}
	
	private String bundlePrefix;
	public String getBundlePrefix() {
		return this.bundlePrefix;
	}
	public void setBundlePrefix(String bundlePrefix) {
		this.bundlePrefix = bundlePrefix;
	}
	
	private String entityMgr;
	public void setEntityMgr(String entityMgr) {
		this.entityMgr = entityMgr;
	}
	public String getEntityMgr() {
		return this.entityMgr;
	}
	
	
	private String featureEntityName;
	public String getFeatureEntityName() {
		return this.featureEntityName;
	}
	public void setFeatureEntityName(String featureEntityName) {
		this.featureEntityName = featureEntityName;
	}
	
	private String searchFICName;
	public String getSearchFICName() {
		return this.searchFICName;
	}
	public void setSearchFICName(String searchFICName) {
		this.searchFICName = searchFICName;
	}
	

	private List<ResourceExtentModel> extents = new ArrayList<>();
	public List<ResourceExtentModel> getExtents() {
		return this.extents;
	}
	public void addToExtents(ResourceExtentModel extentModel) {
		this.extents.add(extentModel);
	}

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

	public String getResourceName() {
		return this.resourceName;
	}
	
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getResourcePackage() {
		return this.resourcePackage;
	}
	
	public void setResourcePackage(String resourcePackage) {
		this.resourcePackage = resourcePackage;
	}

	public String getResourceParent() {
		String resourceParent = this.resourceParent;
		if (resourceParent == null) {
			resourceParent = "ir.viratech.commons.api.resource.BaseAbstractMgrBasedResource<${entityName}, ${fullDtoName}>";
		}
		resourceParent = resourceParent.replaceAll("\\Q${entityName}\\E", this.getEntityName());
		resourceParent = resourceParent.replaceAll("\\Q${fullDtoName}\\E", this.getFullDto());
		return resourceParent;
	}
	
	public void setResourceParent(String resourceParent) {
		this.resourceParent = resourceParent;
	}


	public String getFullDto() {
		return this.fullDto;
	}
	public void setFullDto(String fullDto) {
		this.fullDto = fullDto;
	}
	
	public String getFullDtoName() {
		return this.getFullDto();
	}
	
	public void setDtoModel(DtoModel dtoModel){
		this.dtoModel = dtoModel;
	}
	public DtoModel getDtoModel(){
		return this.dtoModel;
	}
	
	
	private boolean swaggerGenerationEnabled;
	public boolean isSwaggerGenerationEnabled() {
		return this.swaggerGenerationEnabled;
	}
	public void setSwaggerGenerationEnabled(boolean swaggerGenerationEnabled) {
		this.swaggerGenerationEnabled = swaggerGenerationEnabled;
	}

	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder res = new StringBuilder(this.getClass().getSimpleName()+"{" +
				"resourceName: "+this.resourceName
				+ ", " +
				"resourcePackage: "+this.resourcePackage
				+ ", " +
				"entityName: "+this.entityName
				+ ", " +
				"entityPackage: "+this.entityPackage
				+ ", " +
				"genSource: "+this.generationSource
				+ ", " +
				"fullDtoName: "+this.fullDto
				+ ", " +
				"extents: [\n");
		for (ResourceExtentModel extent : this.extents) {
			res.append("\t"+extent+"\n");
		}
		res.append("\t]" + "}");
		return res.toString();
	}
	
	public void postProcess(UiGenerationModel uiGenerationModel) {
		for (DtoModel dtoModel : uiGenerationModel.getDtoModels()) {
			if(this.getFullDtoName().equals(dtoModel.getDtoPackage()+".dto."+dtoModel.getDtoName())){
				this.setDtoModel(dtoModel);
				break;
			}
		}
		
	}
}
