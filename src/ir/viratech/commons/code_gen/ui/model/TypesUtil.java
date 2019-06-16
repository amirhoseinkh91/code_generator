package ir.viratech.commons.code_gen.ui.model;

import ir.viratech.commons.api.search.field.types.FieldInfo_Boolean;
import ir.viratech.commons.api.search.field.types.FieldInfo_Double;
import ir.viratech.commons.api.search.field.types.FieldInfo_Integer;
import ir.viratech.commons.api.search.field.types.FieldInfo_Long;
import ir.viratech.commons.api.search.field.types.FieldInfo_String;
import ir.viratech.commons.api.search.field.types.FieldInfo_Timestamp;
import ir.viratech.commons.model.search.types.CommonTypeKeys;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class TypesUtil {
	
	/**
	 * This map holds short names for common types.
	 * These short names can be used in "type" attribute of *.vu.xml files.
	 */
	private static Map<String, String> typeDefinitions;
	static {
		typeDefinitions = new HashMap<String, String>();
		typeDefinitions.put("integer", "Integer");
		typeDefinitions.put("string", "String");
		typeDefinitions.put("bool", "boolean");
		typeDefinitions.put("date", "java.util.Date");
		typeDefinitions.put("Date", "java.util.Date");
		typeDefinitions.put("timestamp", "java.util.Date");
	}

	public static String getTypeDefinition(String typeStr) {
		return typeDefinitions.get(typeStr);
	}


	
	private static Map<String, String> fieldInfoTypes;
	private static Map<String, String> typeKeys;
	static {
		fieldInfoTypes = new HashMap<String, String>();
		typeKeys = new HashMap<String, String>();

		for (String x : Arrays.asList("String", "java.lang.String")) {
			fieldInfoTypes.put(x, FieldInfo_String.class.getName());
			typeKeys.put(x, CommonTypeKeys.TYPE_KEY__STRING);
		}

		for (String x : Arrays.asList("int", "Integer", "java.lang.Integer")) {
			fieldInfoTypes.put(x, FieldInfo_Integer.class.getName());
			typeKeys.put(x, CommonTypeKeys.TYPE_KEY__INTEGER);
		}

		for (String x : Arrays.asList("boolean", "Boolean", "java.lang.Boolean")) {
			fieldInfoTypes.put(x, FieldInfo_Boolean.class.getName());
			typeKeys.put(x, CommonTypeKeys.TYPE_KEY__BOOL);
		}

		for (String x : Arrays.asList("long", "Long", "java.lang.Long")) {
			fieldInfoTypes.put(x, FieldInfo_Long.class.getName());
			typeKeys.put(x, CommonTypeKeys.TYPE_KEY__LONG);
		}

		for (String x : Arrays.asList("double", "Double", "java.lang.Double")) {
			fieldInfoTypes.put(x, FieldInfo_Double.class.getName());
			typeKeys.put(x, CommonTypeKeys.TYPE_KEY__DOUBLE);
		}
		
		for (String x : Arrays.asList("date", "Date", "java.util.Date", "timestamp")){
			fieldInfoTypes.put(x, FieldInfo_Timestamp.class.getName());
			typeKeys.put(x, CommonTypeKeys.TYPE_KEY__TIMESTAMP);
		}
	}
	
	public static String getFieldInfoType(String type) {
		return fieldInfoTypes.get(type);
	}
	
	public static String getTypeKey(String type) {
		return typeKeys.get(type);
	}
	
	
	
}
