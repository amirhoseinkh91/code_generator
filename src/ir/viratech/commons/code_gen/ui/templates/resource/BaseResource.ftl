<#assign childClassName = model.resourceName >
<#assign entityNameVar = model.entityName?uncap_first>
<#-- ================================================================================== -->
package ${packageName};

<#if model.swaggerGenerationEnabled >
import com.wordnik.swagger.annotations.Api;

</#if>
import ${model.entityPackage}.${model.entityName};

/**
 *  Base class for "${childClassName}".
 *  It is an automatically generated file and should not be edited.
 */
<#if model.swaggerGenerationEnabled >
@Api(value = ${className}.RESOURCE_PATH_BASE, description = ${className}.RESOURCE_DESCRIPTION)
</#if>
public abstract class ${className} extends ${model.resourceParent} {
	
<#if model.swaggerGenerationEnabled >
	/**
	 * The resource path base used in swagger.
	 */
	public static final String RESOURCE_PATH_BASE = "/${model.resourcePath}"+"/"+PATH_PART_ITEMS;
	
	/**
	 * The resource description used in swagger.
	 */
	public static final String RESOURCE_DESCRIPTION = "${model.entityName} Resource";
	
</#if>
	/**
	 * The path which is handled by this resource/
	 */
	public static final String RESOURCE_PATH = "/${model.resourcePath}"+PATH_PARAM_PART__ITEMS;
	
	/**
	 * Provides the entity manager used by the resource.
	 * @return an instance of the required entity manager
	 */
	@Override
	protected ir.viratech.commons.model.BasicEntityMgr<${model.entityName}> getMgr() {
		return ${model.entityMgr}.getInstance();
	}

	/**
	 * Factory method for FullDTO.
	 * @return a FullDTO instance
	 */
	@Override
	protected ${model.fullDtoName} createFullDTO() {
		return new ${model.fullDtoName}();
	}

	/**
	 * Provides the class of FieldInfoContext used for search.
	 * @return the class of FieldInfoContext 
	 */
	@Override
	protected Class<${model.searchFICName}> getFullDtoFieldInfoContextClass() {
		return ${model.searchFICName}.class;
	}

	/**
	 * Provides the bundle prefix used in i18n.
	 * @return the bundle prefix
	 */
	@Override
	protected String getBundlePrefix() {
		return "${model.bundlePrefix}";
	}
	
<#if model.featureEntityName?? >
	/**
	 * Provides the feature entity name by which access checking is performed.
	 * @return the feature entity name
	 */
	@Override
	protected String getFeatureEntityName() {
		return "${model.featureEntityName}";
	}
	
</#if>
	
	/**
	 * Default constructor.
	 * Also adds the extents.
	 */
	public ${className}() {
		super();
		<#list model.extents as extent>
		this.addFieldInfoContext("${extent.name}", ${extent.ficClassName}.class);
		</#list>
	}

}
