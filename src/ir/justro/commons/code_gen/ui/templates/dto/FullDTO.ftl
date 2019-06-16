<#assign baseClassName = "Base" + className >
<#-- ================================================================================== -->
package ${packageName};

import ${model.dtoPackage}.base.${baseClassName};


/**
 * A DTO for class ${model.entityName}.
 *
 */
public class ${className} extends ${baseClassName} {
	
	<#if model.generatingFieldInfoContext>
	/**
	 * FieldInfoContext for ${className}
	 */
	public static class ${model.fieldInfoContextName} extends ${baseClassName}.Base${model.fieldInfoContextName} {
		
	}
	</#if>
	
}
