package ir.viratech.commons.code_gen.ui.model;

import ir.viratech.commons.util.containers.CollectionsUtil;


/**
 * The Class DtoPropertyModel_Primitive.
 */
public class DtoPropertyModel_Primitive extends AbstractDtoPropertyModel {

	private Boolean sortable;
	private String fieldInfoType;
	private String typeKey;
	private String clientCustomProp;



	/**
	 * Checks if is sortable.
	 *
	 * @return the boolean
	 */
	public Boolean isSortable() {
		return this.sortable;
	}
	
	/**
	 * Sets the sortable.
	 *
	 * @param sortable the new sortable
	 */
	public void setSortable(Boolean sortable) {
		this.sortable = sortable;
	}

	/**
	 * Gets the field info type.
	 *
	 * @return the field info type
	 */
	public String getFieldInfoType() {
		return this.fieldInfoType;
	}
	
	/**
	 * Sets the field info type.
	 *
	 * @param fieldInfoType the new field info type
	 */
	public void setFieldInfoType(String fieldInfoType) {
		this.fieldInfoType = fieldInfoType;
	}

	/**
	 * Gets the type key.
	 *
	 * @return the type key
	 */
	public String getTypeKey() {
		return this.typeKey;
	}
	
	/**
	 * Sets the type key.
	 *
	 * @param typeKey the new type key
	 */
	public void setTypeKey(String typeKey) {
		this.typeKey = typeKey;
	}

	/**
	 * Gets the client custom property.
	 *
	 * @return the client custom property
	 */
	public String getClientCustomProp() {
		return clientCustomProp;
	}
	
	/**
	 * Sets the custom property.
	 *
	 * @param clientCustomProp the new custom property
	 */
	public void setClientCustomProp(String clientCustomProp) {
		this.clientCustomProp = clientCustomProp;
	}
	

	@Override
	protected String toStringData() {
		return super.toStringData() +
				"sortable=" + this.sortable
				+ ", " +
				"fieldInfoType=" + this.fieldInfoType
				+ ", " +
				"typeKey=" + this.typeKey;
	}


	/* (non-Javadoc)
	 * @see ir.viratech.commons.code_gen.ui.model.DtoPropertyModel#getCategoryKey()
	 */
	@Override
	public String getCategoryKey() {
		return "primitive";
	}

	//==============================================================================================================
	//==============================================================================================================
	//=======================================  methods used in templates  ==========================================
	//==============================================================================================================
	//==============================================================================================================

	/**
	 * Gets the quoted type key.
	 *
	 * @return the quoted type key
	 */
	public String getQuotedTypeKey() {
		String typeKey = this.getTypeKey();
		return (typeKey == null) ? "null" : '"'+typeKey+'"';
	}
	
	public String getImprovedTypeKey() {
        String typeKey = this.getTypeKey();
		if (typeKey != null)
			return typeKey;
		return TypesUtil.getTypeKey(this.getType());
	}
	
	public String getSchemaImprovedTypeKey() {
		if (CollectionsUtil.isIn(this.getType(), "timestamp", "java.util.Date", "date", "Date"))
			return "date";
		return this.getImprovedTypeKey();
	}
	
	
	/**
	 * Gets the sortable string.
	 *
	 * @return the sortable string
	 */
	public String getSortableString() {
		Boolean sortable = this.isSortable();
		return (sortable == null) ? "null" : sortable.toString();
	}


}
