package ir.justro.commons.code_gen.web;

import java.util.HashMap;

public class SchemaProperty {
	private String model;
	private String label;
	private String type;
	private String widget;
	private String enumType;
	private SchemaModel innerModel;
	private boolean multiple;
	private boolean required;
	private boolean searchable;
	private int searchLevel;
	private HashMap<String,String> widgetParams;
	
	public SchemaProperty(String model, String label, String type, String widget,
			boolean multiple, boolean required, boolean searchable) {
		super();
		this.model = model;
		this.label = label;
		this.type = type;
		this.widget = widget;
		this.multiple = multiple;
		this.required = required;
		this.searchable = searchable;
		this.widgetParams = new HashMap<>();
	}
	
	public SchemaProperty(){
		
	}

	//TODO
	public String generateHtml(){
		return null;
	}
	
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getWidget() {
		return widget;
	}
	public void setWidget(String widget) {
		this.widget = widget;
	}
	public String getEnumType() {
		return enumType;
	}
	public void setEnumType(String enumType) {
		this.enumType = enumType;
	}
	public boolean isMultiple() {
		return multiple;
	}
	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	public boolean isSearchable() {
		return searchable;
	}
	public void setSearchable(boolean searchable) {
		this.searchable = searchable;
	}
	public int getSearchLevel() {
		return searchLevel;
	}
	public void setSearchLevel(int searchLevel) {
		this.searchLevel = searchLevel;
	}

	public HashMap<String, String> getWidgetParams() {
		return widgetParams;
	}
	public void addWidgetParam(String key, String value) {
		this.widgetParams.put(key, value);
	}
	public SchemaModel getInnerModel() {
		return innerModel;
	}

	public void setInnerModel(SchemaModel innerModel) {
		this.innerModel = innerModel;
	}

	@Override
	public String toString() {
		return "SchemaProperty [model=" + model + ", label=" + label
				+ ", type=" + type + ", widget=" + widget + ", multiple="
				+ multiple + "]";
	}

}
