<#assign baseClassName = "Base" + className >
<#-- ================================================================================== -->
package ${packageName};

import ${model.resourcePackage}.base.${baseClassName};

import javax.ws.rs.Path;

import org.springframework.stereotype.Component;

/**
 *  This is a REST Resource class for entity "${model.entityName}".
 *
 */
@Component
@Path(${baseClassName}.RESOURCE_PATH)
public class ${className} extends ${baseClassName} {


}
