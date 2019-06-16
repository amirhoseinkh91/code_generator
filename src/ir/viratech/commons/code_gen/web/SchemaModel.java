package ir.viratech.commons.code_gen.web;

import ir.viratech.commons.code_gen.ui.model.AbstractDtoPropertyModel;
import ir.viratech.commons.code_gen.ui.model.DtoModel;
import ir.viratech.commons.code_gen.ui.model.DtoPropertyModel_Dto;
import ir.viratech.commons.code_gen.ui.model.DtoPropertyModel_Primitive;
import ir.viratech.commons.code_gen.ui.model.ResourceModel;
import ir.viratech.commons.code_gen.ui.model.UiGenerationModel;
import ir.viratech.commons.util.containers.CollectionsUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class SchemaModel {
	
	private String key;
	private String path;
	private String title;
	private String featureName;
	private List<SchemaProperty> propertyList;
	private JsonParser jsonParser;
	private JsonGenerator jsonGen;
	
	public SchemaModel(){
		
	}
	
	public SchemaModel(File jsonFile) {
		this.propertyList = new ArrayList<>();
		try {
			this.jsonParser = new JsonFactory().createParser(jsonFile);
			jsonParser.nextToken();//return JsonToken.START_OBJECT
			while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
				if(jsonParser.getCurrentName()!=null && jsonParser.getCurrentName().equals("properties")){
					jsonParser.nextToken();//return JsonToken.START_OBJECT
					while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
						String fieldName = jsonParser.getCurrentName();//property name
						jsonParser.nextToken();//return JsonToken.START_OBJECT
						SchemaProperty prop = createPropFromJson(fieldName);
						this.addToPropertyList(prop);
					}
				}else if(jsonParser.getCurrentName().equals("title")){
					jsonParser.nextToken();
					this.setTitle(jsonParser.getValueAsString());
				}else if(jsonParser.getCurrentName().equals("path")){
					jsonParser.nextToken();
					this.setPath(jsonParser.getValueAsString());
				}else if(jsonParser.getCurrentName().equals("key")){
					jsonParser.nextToken();
					this.setKey(jsonParser.getValueAsString());
				}else if(jsonParser.getCurrentName().equals("featureName")){
					jsonParser.nextToken();
					this.setFeatureName(jsonParser.getValueAsString());
				}else if(jsonParser.getCurrentName().equals("extents")){
					//TODO: do something if needed
					while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
					}
				}else{
					jsonParser.nextToken();
				}
			}
			jsonParser.close();
		} catch (IOException e) {
			System.out.println();
		}
	}

	private SchemaProperty createPropFromJson(String fieldName)throws IOException, JsonParseException {
		SchemaProperty prop = new SchemaProperty();
		prop.setModel(fieldName);
		while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
			String key = jsonParser.getCurrentName();
			jsonParser.nextToken();
			String value = jsonParser.getValueAsString();
			switch (key) {
			case "label":
				prop.setLabel(value);
				break;
			case "type":
				prop.setType(value);
				break;
			case "enumType":
				prop.setEnumType(value);
				break;
			case "innerForm":
				prop.setInnerModel(new SchemaModel());
				while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
					String innerFieldName = jsonParser.getCurrentName();//property name
					jsonParser.nextToken();//return JsonToken.START_OBJECT
					SchemaProperty innerProp = createPropFromJson(innerFieldName);
					prop.getInnerModel().addToPropertyList(innerProp);
				}
				break;
			case "widget":
				prop.setWidget(value);
				break;
			case "required":
				prop.setRequired(jsonParser.getCurrentToken() == JsonToken.VALUE_TRUE);
				break;
			case "multiple":
				prop.setMultiple(jsonParser.getCurrentToken() == JsonToken.VALUE_TRUE);
				break;
			case "searchable":
				prop.setSearchable(jsonParser.getCurrentToken() == JsonToken.VALUE_TRUE);
				break;
			case "searchLevel":
				prop.setSearchLevel(Integer.valueOf(value));
				break;
			}
		}
		return prop;
	}
	
	//TODO: prop info not completed
	public SchemaModel(ResourceModel resourceModel, UiGenerationModel uiGenerationModel){
		this.setKey(resourceModel.getEntityName().toLowerCase());
		this.setPath(resourceModel.getResourcePath());
		this.setFeatureName(resourceModel.getFeatureEntityName());
		this.propertyList = new ArrayList<>();
		for (AbstractDtoPropertyModel dtoPropModel : resourceModel.getDtoModel().getDtoProperties()) {
			SchemaProperty prop = CreateSchemaPropFromDtoProp(uiGenerationModel, dtoPropModel);
			this.addToPropertyList(prop);
		}
	}
	
	public SchemaModel(DtoModel dtoModel, UiGenerationModel uiGenerationModel){
		this.setKey(dtoModel.getEntityName().toLowerCase());
		this.propertyList = new ArrayList<>();
		for (AbstractDtoPropertyModel dtoPropModel : dtoModel.getDtoProperties()) {
			SchemaProperty prop = CreateSchemaPropFromDtoProp(uiGenerationModel, dtoPropModel);
			this.addToPropertyList(prop);
		}
	}

	private SchemaProperty CreateSchemaPropFromDtoProp(UiGenerationModel uiGenerationModel,AbstractDtoPropertyModel dtoPropModel) {
		SchemaProperty prop = new SchemaProperty();
		prop.setModel(dtoPropModel.getName());
		prop.setLabel(dtoPropModel.getBundleKey());
		prop.setRequired(dtoPropModel.isClientRequired());
		prop.setSearchable(dtoPropModel.isSearchable());
		prop.setSearchLevel(dtoPropModel.getImprovedSearchLevel());
		if (dtoPropModel instanceof DtoPropertyModel_Primitive) {
			DtoPropertyModel_Primitive dtoPropModelPrimitive = (DtoPropertyModel_Primitive)dtoPropModel;
			prop.setWidget(dtoPropModelPrimitive.getClientWidget());
			if(dtoPropModelPrimitive.getSaveStyle().contains("disabled"))
				prop.setType("disabled");
			else
				prop.setType(dtoPropModelPrimitive.getSchemaImprovedTypeKey());
			
		}else if (dtoPropModel instanceof DtoPropertyModel_Dto) {
			DtoPropertyModel_Dto dtoPropModelDto = (DtoPropertyModel_Dto)dtoPropModel;
			if(dtoPropModelDto.getSaveStyle().contains("replaceEntity")){
				prop.setType("enum");
				ResourceModel propDtoResourceModel = uiGenerationModel.getSpeceficResourceModel(
						dtoPropModelDto.getInternalType().substring(dtoPropModelDto.getInternalType().lastIndexOf('.')+1));
				if(propDtoResourceModel!=null){
					prop.setEnumType(propDtoResourceModel.getResourcePath());
				}
			}else if(dtoPropModelDto.getSaveStyle().contains("dtoSaveTo")){
				prop.setType("innerForm");
				DtoModel dtoModel = uiGenerationModel.getSpeceficDtoModel(dtoPropModelDto.getInternalType().substring(
								dtoPropModelDto.getInternalType().lastIndexOf('.')+1));
				if(dtoModel!=null)
					prop.setInnerModel(new SchemaModel(dtoModel,uiGenerationModel));
			}else if(dtoPropModelDto.getSaveStyle().equals("disabled")){
				//FIXME the name of type
				prop.setType("disabled");
			}else if(dtoPropModelDto.getSaveStyle().equals("abstract")){
				//TODO check correctness
				prop.setType(prop.getModel());
			}
		}
		return prop;
	}
	
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<SchemaProperty> getPropertyList() {
		return propertyList;
	}
	public void addToPropertyList(SchemaProperty prop){
		if(this.propertyList==null)
			this.propertyList = new ArrayList<SchemaProperty>();
		this.propertyList.add(prop);
	}
	public String getFeatureName() {
		return featureName;
	}
	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}



	//TODO
	public File generateController(String path){
		File controllerFile = new File(path);
		return controllerFile;
	}
	//TODO
	public File generateHtml(String path){
		File htmlFile = new File(path);
		return htmlFile;
	}
	public File generateJson(File jsonFile){
		try {
			jsonGen = new JsonFactory().createGenerator(jsonFile,JsonEncoding.UTF8);
			jsonGen.useDefaultPrettyPrinter();
			jsonGen.writeStartObject();
//			jsonGen.writeStringField("title", this.getTitle());
			jsonGen.writeStringField("path",this.getPath());
			jsonGen.writeStringField("key", this.getKey());
			jsonGen.writeStringField("featureName", this.getFeatureName());
			jsonGen.writeObjectFieldStart("properties");
			
			writePropertyListToJsonFile(this);

			jsonGen.writeEndObject();//Properties
			jsonGen.writeObjectFieldStart("extents");
			jsonGen.writeArrayFieldStart("list");
			for (SchemaProperty prop : this.getPropertyList()) {
				if(prop.getType()!=null && !prop.getModel().equals("uid") && 
						CollectionsUtil.isIn(prop.getType(), "string", "integer","boolean"))
					jsonGen.writeString(prop.getModel());
			}
			jsonGen.writeEndArray();
			jsonGen.writeEndObject();//extents
			jsonGen.writeEndObject();
			jsonGen.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonFile;
	}

	private void writePropertyListToJsonFile(SchemaModel schemaModel) throws IOException,JsonGenerationException {
		for (SchemaProperty prop : schemaModel.getPropertyList()) {
			
			if(!prop.getModel().equals("uid")){
				try {
					jsonGen.writeObjectFieldStart(prop.getModel());
					jsonGen.writeStringField("label", prop.getLabel());
					jsonGen.writeStringField("type", prop.getType());
					if(prop.getType()!=null){
						if(prop.getType().equals("enum")){
							jsonGen.writeStringField("enumType", prop.getEnumType());
						}else if(prop.getType().equals("innerForm")){
							jsonGen.writeObjectFieldStart("innerForm");
							writePropertyListToJsonFile(prop.getInnerModel());
							jsonGen.writeEndObject();//innerForm
							if(prop.getInnerModel()==null)
								throw new IllegalStateException("InnerForm of "+prop.getModel()+" was NULL in creating jsonFile of "+this.key);
						}
					}else{
						throw new IllegalStateException("Type of "+prop.getModel()+" was NULL in creating jsonFile of "+this.key);
					}
					if(prop.getWidget()!= null)
						jsonGen.writeStringField("widget", prop.getWidget());
					jsonGen.writeBooleanField("multiple", prop.isMultiple());
					jsonGen.writeBooleanField("required", prop.isRequired());
					jsonGen.writeBooleanField("searchable", prop.isSearchable());
					jsonGen.writeNumberField("searchLevel", prop.getSearchLevel());
					//TODO: WidgetParam
					jsonGen.writeEndObject();
				} catch(IllegalStateException ise){
					System.err.println("====================== ERROR =======================");
					System.err.println(ise.getMessage());
				} 
			}
		}
	}
}
