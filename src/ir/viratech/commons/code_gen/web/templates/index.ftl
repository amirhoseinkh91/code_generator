<#list schemaNameList as schemaName>
<!-- ${schemaName} -->
<script src="app/modules/management/${schemaName}/${schemaName}Srvc.js"></script>
<script src="app/modules/management/${schemaName}/${schemaName}Config.js"></script>
<script src="app/modules/management/${schemaName}/${schemaName}Ctrl.js"></script>
</#list>