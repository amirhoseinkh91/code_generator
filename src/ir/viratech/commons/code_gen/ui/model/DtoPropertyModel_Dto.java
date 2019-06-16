package ir.viratech.commons.code_gen.ui.model;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;



/**
 * The Class DtoPropertyModel_Dto.
 */
public class DtoPropertyModel_Dto extends AbstractDtoPropertyModel {

	private String fieldInfoContextClassName;
	private String findByDtoStyle;//In DTO
	private String entityByDtoFinderStyle;//In FIC

	/**
	 * Gets the field info context class name.
	 *
	 * @return the field info context class name
	 */
	public String getFieldInfoContextClassName() {
		return this.fieldInfoContextClassName;
	}
	
	/**
	 * Sets the field info context class name.
	 *
	 * @param fieldInfoContextClassName the new field info context class name
	 */
	public void setFieldInfoContextClassName(String fieldInfoContextClassName) {
		this.fieldInfoContextClassName = fieldInfoContextClassName;
	}

	/**
	 * Gets the find by dto style.
	 *
	 * @return the find by dto style
	 */
	public String getFindByDtoStyle() {
		return this.findByDtoStyle;
	}
	
	/**
	 * Sets the find by dto style.
	 *
	 * @param findByDtoStyle the new find by dto style
	 */
	public void setFindByDtoStyle(String findByDtoStyle) {
		this.findByDtoStyle = findByDtoStyle;
	}

	/**
	 * Gets the entity by dto finder style.
	 *
	 * @return the entity by dto finder style
	 */
	public String getEntityByDtoFinderStyle() {
		return this.entityByDtoFinderStyle;
	}
	
	/**
	 * Sets the entity by dto finder style.
	 *
	 * @param entityByDtoFinderStyle the new entity by dto finder style
	 */
	public void setEntityByDtoFinderStyle(String entityByDtoFinderStyle) {
		this.entityByDtoFinderStyle = entityByDtoFinderStyle;
	}


	@Override
	protected String toStringData() {
		return super.toStringData() +
				"ficClass=" + this.fieldInfoContextClassName
				+ ", " +
				"findByDto=" + this.findByDtoStyle
				+ ", " +
				"entityByDtoFinder=" + this.entityByDtoFinderStyle;
	}

	/* (non-Javadoc)
	 * @see ir.viratech.commons.code_gen.ui.model.DtoPropertyModel#getCategoryKey()
	 */
	@Override
	public String getCategoryKey() {
		return "dto";
	}

	/**
	 * Calc find by dto styles.
	 *
	 * @param findByDtoStyle the find by dto style
	 * @param entityByDtoFinderStyle the entity by dto finder style
	 * @return Pair<styleInDTO, styleInFiC>
	 */
	public Pair<String, String> calcFindByDtoStyles(String findByDtoStyle, String entityByDtoFinderStyle) {
		//This code is written carefully!!! Do not change it easily!
		boolean neededFindByDto = this.getSaveStyle().startsWith("hooked-replaceEntity");
		if (findByDtoStyle == null) {
			findByDtoStyle = neededFindByDto ? (isDisabled(entityByDtoFinderStyle)?"abstract":"useContext") : "disabled";
		}
		if (entityByDtoFinderStyle == null) {
			entityByDtoFinderStyle = (this.isSearchable() || "useContext".equals(findByDtoStyle)) ? "abstract" : "disabled";
		}
		return Pair.of(findByDtoStyle, entityByDtoFinderStyle);
	}

	//==============================================================================================================
	//==============================================================================================================
	//=======================================  methods used in templates  ==========================================
	//==============================================================================================================
	//==============================================================================================================


	/**
	 * Checks if is find by dto disabled.
	 *
	 * @return true, if is find by dto disabled
	 */
	public boolean isFindByDtoDisabled() {
		return isDisabled(this.getFindByDtoStyle());
	}
	
	/**
	 * Checks if is find by dto enabled.
	 *
	 * @return true, if is find by dto enabled
	 */
	public boolean isFindByDtoEnabled() {
		return !this.isFindByDtoDisabled();
	}

	/**
	 * Gets the find by dto method name in dto.
	 *
	 * @return the find by dto method name in dto
	 */
	public String getFindByDtoMethodNameInDto() {
		return "findByDto_"+ getCapFirst(this.getName());
	}


	/**
	 * Checks if is entity by dto finder disabled.
	 *
	 * @return true, if is entity by dto finder disabled
	 */
	public boolean isEntityByDtoFinderDisabled() {
		return isDisabled(this.getEntityByDtoFinderStyle());
	}
	
	/**
	 * Checks if is entity by dto finder enabled.
	 *
	 * @return true, if is entity by dto finder enabled
	 */
	public boolean isEntityByDtoFinderEnabled() {
		return !this.isEntityByDtoFinderDisabled();
	}

	/**
	 * Gets the entity by dto finder creator name.
	 *
	 * @return the entity by dto finder creator name
	 */
	public String getEntityByDtoFinderCreatorName() {
		return "create"+getCapFirst(this.getEntityByDtoFinderFieldName());
	}
	
	/**
	 * Gets the entity by dto finder field name.
	 *
	 * @return the entity by dto finder field name
	 */
	public String getEntityByDtoFinderFieldName() {
		return "entityByDtoFinder_"+ getCapFirst(this.getName());
	}
	
	/**
	 * Gets the entity by dto finder getter name.
	 *
	 * @return the entity by dto finder getter name
	 */
	public String getEntityByDtoFinderGetterName() {
		return "get"+getCapFirst(this.getEntityByDtoFinderFieldName());
	}
	
	/**
	 * Gets the find by dto method name in fic.
	 *
	 * @return the find by dto method name in fic
	 */
	public String getFindByDtoMethodNameInFIC() {
		return "findByDto_"+ getCapFirst(this.getName());
	}


	/**
	 * Gets the name_ dto suggested.
	 *
	 * @return the name_ dto suggested
	 */
	public String getName_DtoSuggested() {
		String name = this.getName();
		if (StringUtils.endsWithIgnoreCase(name, "dto")) {
			return name;
		}
		return name+"Dto";
	}

}
