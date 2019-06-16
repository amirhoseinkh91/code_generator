package ir.viratech.commons.code_gen.ui.model;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * The Class DtoPropertyModel.
 */
public abstract class AbstractDtoPropertyModel {

	private String name;
	private String type;
	private String internalName;
	private String internalType;
	private String loadStyle;
	private String saveStyle;
	private boolean trackingModifications;
	private String toStringStyle;

	private String fieldInfoStyle;
	private boolean searchable;
	private String searchExpression;
	private String bundleKey;

	private String clientWidget;
	private boolean clientRequired;
	private int clientSearchLevel;
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the internal name.
	 *
	 * @return the internal name
	 */
	public String getInternalName() {
		return this.internalName;
	}
	
	/**
	 * Sets the internal name.
	 *
	 * @param internalName the new internal name
	 */
	public void setInternalName(String internalName) {
		this.internalName = internalName;
	}
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return this.type;
	}
	
	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * Gets the internal type.
	 *
	 * @return the internal type
	 */
	public String getInternalType() {
		return this.internalType;
	}
	
	/**
	 * Sets the internal type.
	 *
	 * @param internalType the new internal type
	 */
	public void setInternalType(String internalType) {
		this.internalType = internalType;
	}
	
	/**
	 * Gets the load style.
	 *
	 * @return the load style
	 */
	public String getLoadStyle() {
		return this.loadStyle;
	}
	
	/**
	 * Sets the load style.
	 *
	 * @param loadStyle the new load style
	 */
	public void setLoadStyle(String loadStyle) {
		this.loadStyle = loadStyle;
	}
	
	/**
	 * Gets the save style.
	 *
	 * @return the save style
	 */
	public String getSaveStyle() {
		return this.saveStyle;
	}
	
	/**
	 * Sets the save style.
	 *
	 * @param saveStyle the new save style
	 */
	public void setSaveStyle(String saveStyle) {
		this.saveStyle = saveStyle;
	}
	
	/**
	 * Checks if is tracking modifications.
	 *
	 * @return true, if is tracking modifications
	 */
	public boolean isTrackingModifications() {
		return this.trackingModifications;
	}
	
	/**
	 * Sets the tracking modifications.
	 *
	 * @param trackingModifications the new tracking modifications
	 */
	public void setTrackingModifications(boolean trackingModifications) {
		this.trackingModifications = trackingModifications;
	}
	
	/**
	 * Gets the to string style.
	 *
	 * @return the to string style
	 */
	public String getToStringStyle() {
		return this.toStringStyle;
	}
	
	/**
	 * Sets the to string style.
	 *
	 * @param toStringStyle the new to string style
	 */
	public void setToStringStyle(String toStringStyle) {
		this.toStringStyle = toStringStyle;
	}

	/**
	 * Gets the field info style.
	 *
	 * @return the field info style
	 */
	public String getFieldInfoStyle() {
		return this.fieldInfoStyle;
	}
	
	/**
	 * Sets the field info style.
	 *
	 * @param fieldInfoStyle the new field info style
	 */
	public void setFieldInfoStyle(String fieldInfoStyle) {
		this.fieldInfoStyle = fieldInfoStyle;
	}

	/**
	 * Checks if is searchable.
	 *
	 * @return true, if is searchable
	 */
	public boolean isSearchable() {
		return this.searchable;
	}
	
	/**
	 * Sets the searchable.
	 *
	 * @param searchable the new searchable
	 */
	public void setSearchable(boolean searchable) {
		this.searchable = searchable;
	}

	/**
	 * Gets the search expression.
	 *
	 * @return the search expression
	 */
	public String getSearchExpression() {
		return this.searchExpression;
	}
	
	/**
	 * Sets the search expression.
	 *
	 * @param searchExpression the new search expression
	 */
	public void setSearchExpression(String searchExpression) {
		this.searchExpression = searchExpression;
	}

	/**
	 * Gets the bundle key.
	 *
	 * @return the bundle key
	 */
	public String getBundleKey() {
		return this.bundleKey;
	}
	
	/**
	 * Sets the bundle key.
	 *
	 * @param bundleKey the new bundle key
	 */
	public void setBundleKey(String bundleKey) {
		this.bundleKey = bundleKey;
	}

	/**
	 * Gets the clientWidget.
	 *
	 * @return the clientWidget
	 */
	public String getClientWidget() {
		return clientWidget;
	}
	
	/**
	 * Sets the clientWidget.
	 *
	 * @param clientWidget the new clientWidget
	 */
	public void setClientWidget(String clientWidget) {
		this.clientWidget = clientWidget;
	}
	
	
	public boolean isClientRequired() {
		return clientRequired;
	}
	
	public void setClientRequired(boolean clientRequired) {
		this.clientRequired = clientRequired;
	}


	public int getClientSearchLevel() {
		return clientSearchLevel;
	}

	public void setClientSearchLevel(int clientSearchLevel) {
		this.clientSearchLevel = clientSearchLevel;
	}

	
	private String __getPar(String s, String t) {
		return (s==null || s.equals(t)) ? "" : ("("+s+")");
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "<" + this.toStringData() + ">";
	}
	protected String toStringData() {
		return this.name+this.__getPar(this.internalName, this.name)+": "+this.type+this.__getPar(this.internalType, this.type)
				+ ", " +
				"load=" + this.loadStyle
				+ ", " +
				"save=" + this.saveStyle
				+ ", " +
				"trackModif=" + this.trackingModifications
				+ ", " +
				"toString=" + this.toStringStyle
				+ ", " +
				"fieldInfo=" + this.fieldInfoStyle
				+ ", " +
				"searchable=" + this.searchable
				+ ", " +
				"searchExp=" + this.searchExpression
				+ ", " +
				"bundleKey=" + this.bundleKey
				+", " +
				"clientWidget=" + this.clientWidget;
	}

	/**
	 * Gets the category key.
	 *
	 * @return the category key
	 */
	public abstract String getCategoryKey();

	//==============================================================================================================
	//==============================================================================================================
	//=======================================  methods used in templates  ==========================================
	//==============================================================================================================
	//==============================================================================================================

	protected static boolean isDisabled(String mode) {
		return StringUtils.equals(mode, "disabled");
	}


	private static List<String> booleanTypes = Arrays.asList("boolean","Boolean","java.lang.Boolean");
	private static String getGetterPrefix(String type) {
		return (booleanTypes.contains(type))? "is" : "get";
	}

	protected static String getCapFirst(String str) {
		char firstChar = Character.toUpperCase(str.charAt(0));
		return firstChar + str.substring(1);
	}

	/**
	 * Gets the dto getter name.
	 *
	 * @return the dto getter name
	 */
	public String getDtoGetterName(){
		return getGetterPrefix(this.getType()) + getCapFirst(this.getName());
	}

	/**
	 * Gets the internal getter name.
	 *
	 * @return the internal getter name
	 */
	public String getInternalGetterName(){
		return getGetterPrefix(this.getInternalType()) + getCapFirst(this.getInternalName());
	}

	/**
	 * Gets the dto setter name.
	 *
	 * @return the dto setter name
	 */
	public String getDtoSetterName(){
		return "set" + getCapFirst(this.getName());
	}

	/**
	 * Gets the internal setter name.
	 *
	 * @return the internal setter name
	 */
	public String getInternalSetterName(){
		return "set" + getCapFirst(this.getInternalName());
	}

	/**
	 * Gets the specialized internal name.
	 *
	 * @return the specialized internal name
	 */
	public String getSpecializedInternalName() {
		return ("__internalProperty_"+this.getInternalName());
	}

	/**
	 * Checks if is load disabled.
	 *
	 * @return true, if is load disabled
	 */
	public boolean isLoadDisabled() {
		return isDisabled(this.getLoadStyle());
	}
	
	/**
	 * Checks if is getter disabled.
	 *
	 * @return true, if is getter disabled
	 */
	public boolean isGetterDisabled() {
		return this.isLoadDisabled();
	}
	
	/**
	 * Checks if is load enabled.
	 *
	 * @return true, if is load enabled
	 */
	public boolean isLoadEnabled() {
		return !this.isLoadDisabled();
	}
	
	/**
	 * Checks if is getter enabled.
	 *
	 * @return true, if is getter enabled
	 */
	public boolean isGetterEnabled() {
		return !this.isGetterDisabled();
	}


	/**
	 * Checks if is save disabled.
	 *
	 * @return true, if is save disabled
	 */
	public boolean isSaveDisabled() {
		String saveStyle = this.getSaveStyle();
		return isDisabled(saveStyle)  || StringUtils.equals(saveStyle, "disabled-withSetter");
	}
	
	/**
	 * Checks if is setter disabled.
	 *
	 * @return true, if is setter disabled
	 */
	public boolean isSetterDisabled() {
		return isDisabled(this.getSaveStyle());
	}
	
	/**
	 * Checks if is save enabled.
	 *
	 * @return true, if is save enabled
	 */
	public boolean isSaveEnabled() {
		return !this.isSaveDisabled();
	}
	
	/**
	 * Checks if is setter enabled.
	 *
	 * @return true, if is setter enabled
	 */
	public boolean isSetterEnabled() {
		return !this.isSetterDisabled();
	}

	/**
	 * Checks if is property annothation needed in getter.
	 *
	 * @return true, if is property annothation needed in getter
	 */
	public boolean isPropertyAnnothationNeededInGetter(){
		return this.isGetterEnabled();
	}
	
	/**
	 * Checks if is property annothation needed in setter.
	 *
	 * @return true, if is property annothation needed in setter
	 */
	public boolean isPropertyAnnothationNeededInSetter(){
		return !this.isPropertyAnnothationNeededInGetter() && this.isSetterEnabled();
	}

	/**
	 * Checks if is field info disabled.
	 *
	 * @return true, if is field info disabled
	 */
	public boolean isFieldInfoDisabled() {
		return isDisabled(this.getFieldInfoStyle());
	}
	
	/**
	 * Checks if is field info enabled.
	 *
	 * @return true, if is field info enabled
	 */
	public boolean isFieldInfoEnabled() {
		return !this.isFieldInfoDisabled();
	}
	
	public int getImprovedSearchLevel(){
		int searchLevel = this.getClientSearchLevel();
		if(searchLevel==0)
			searchLevel = (this.isSearchable()) ? 1 : 0;
		return searchLevel;
	}

}
