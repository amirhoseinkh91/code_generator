package ir.justro.commons.code_gen.ui.model;

import java.util.List;

/**
 * The Class UiGenerationModel.
 */
public class UiGenerationModel {

	private List<DtoModel> dtoModels;
	
	/**
	 * Gets the dto models.
	 *
	 * @return the dto models
	 */
	public List<DtoModel> getDtoModels() {
		return this.dtoModels;
	}
	
	/**
	 * Sets the dto models.
	 *
	 * @param dtoModels the new dto models
	 */
	public void setDtoModels(List<DtoModel> dtoModels) {
		this.dtoModels = dtoModels;
	}
	
	
	private List<ResourceModel> resourceModels;
	public List<ResourceModel> getResourceModels() {
		return this.resourceModels;
	}
	public void setResourceModels(List<ResourceModel> resourceModels) {
		this.resourceModels = resourceModels;
	}

	public DtoModel getSpeceficDtoModel(String dtoName){
		for (DtoModel dtoModel : this.dtoModels) {
			if (dtoModel.getEntityName().equals(dtoName)) {
				return dtoModel;
			}
		}
		return null;
	}
	public ResourceModel getSpeceficResourceModel(String dtoName){
		for (ResourceModel resourceModel : this.resourceModels) {
			if(resourceModel.getDtoModel().getEntityName().equals(dtoName)){
				return resourceModel;
			}
		}
		return null;
	}
	

	/**
	 * Instantiates a new ui generation model.
	 */
	public UiGenerationModel() {
	}
	
	/**
	 * Instantiates a new ui generation model.
	 *
	 * @param dtoModels the dto models
	 * @param resourceModels 
	 */
	public UiGenerationModel(List<DtoModel> dtoModels, List<ResourceModel> resourceModels) {
		this.setDtoModels(dtoModels);
		this.setResourceModels(resourceModels);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder res = new StringBuilder(this.getClass().getSimpleName()+"{"
				+ "dtoModels: [\n");
		for (DtoModel dtoModel : this.getDtoModels()) {
			res.append(dtoModel+",\n");
		}
		res.append("], resourceModels: [\n");
		for (ResourceModel resourceModel : this.getResourceModels()) {
			res.append(resourceModel+",\n");
		}
		res.append("]");
		res.append("}");
		return res.toString();
	}

	public void postProcess() {
		for (ResourceModel resourceModel : this.getResourceModels()) {
			resourceModel.postProcess(this);
		}
		
	}
}
