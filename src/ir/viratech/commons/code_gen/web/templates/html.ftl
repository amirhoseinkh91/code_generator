<div class="col-sm-12">
	<div class="panel panel-default panel-page-header">
		<div class="panel-heading">${schema.featureName} MANAGEMENT</div>
		<div class="panel-body">
			<div class="col-md-8" access-checker="API_LIST_${schema.featureName}">
				<div class="panel panel-default">
					<div class="panel-body">
						<vt-search control-fn="Controller.${schema.key}SearchController"></vt-search>
					</div>
				</div>
				<div class="panel panel-default">
					<div class="panel-body">
						<vt-grid control-fn="Controller.${schema.key}ListController"></vt-grid>
					</div>
				</div>
			</div>
			
			<div class="col-md-4" access-checker="API_VIEW_${schema.featureName}" 
				ng-hide="Data.searchMode=='advanced'">
				<div class="panel panel-default">
					<div class="panel-body panel-management">
						<div class="col-sm-12">
							<a class="btn btn-sm btn-default btn-text"
								access-checker="API_ADD_${schema.featureName}"
								ng-show="true" type="button"
								ng-click="Func.onAdd${schema.key}Click()" ng-disabled="Data.mode=='add'" ng-bind="'CREATE'|EnToFaButton"></a>
							<a class="btn btn-sm btn-primary btn-text"
								access-checker="API_EDIT_${schema.featureName}"
								ng-show="Data.selected${schema.key}.uid && Data.mode=='view'" type="button"
								ng-click="Func.onEdit${schema.key}Click()" ng-disabled="" ng-bind="'EDIT'|EnToFaButton"></a>
							<a class="btn btn-sm btn-success btn-text"
								access-checker="API_ADD_${schema.featureName}"
								ng-show="Data.mode=='add'" type="button"
								ng-click="Func.onSave${schema.key}Click()" ng-bind="'SAVE'|EnToFaButton"
								ng-disabled="${schema.key}Form.$invalid && Data.validationClicked"></a>
							<a class="btn btn-sm btn-success btn-text"
								access-checker="API_EDIT_${schema.featureName}"
								ng-show="Data.mode=='edit'" type="button"
								ng-click="Func.onUpdate${schema.key}Click()" ng-bind="'UPDATE'|EnToFaButton"
								ng-disabled="${schema.key}Form.$invalid && Data.validationClicked"></a>
							<a class="btn btn-sm btn-warning btn-text"
								ng-show="Data.mode=='edit' || Data.mode=='add'" type="button"
								ng-click="Func.onCancelClick()" ng-disabled="" ng-bind="'CANCEL'|EnToFaButton"></a>
							<a class="btn btn-sm btn-danger btn-text"
								access-checker="API_DELETE_${schema.featureName}"
								ng-show="Data.selected${schema.key}.uid && Data.mode=='view'" type="button"
								ng-click="Func.onDelete${schema.key}Click()" ng-disabled="" ng-bind="'DELETE'|EnToFaButton"></a>
							<a class="btn btn-sm btn-default pull-left"
								ng-show="true" type="button"
								ng-click="Func.onChangeSearchModeClick('advanced')">
								<span class="glyphicon glyphicon-filter" aria-hidden="true"></span>
							</a>
						</div>
					</div>
				</div>
				<div class="panel panel-default" ng-show="Data.selected${schema.key}.uid || Data.mode=='add'">
					<div class="panel-body">
						<form name="${schema.key}Form" role="form" novalidate>
						<#list schema.propertyList as prop>
							<div class="row rowForm"<#if prop.required> ng-class="{'has-error':${schema.key}Form.${prop.model}.$invalid && Data.validationClicked}"</#if>>
								<#if prop.type!="innerForm">
								<label class="col-sm-5"<#if prop.required> ng-class="{'required':Data.mode!='view'}"</#if>>${prop.model}</label>
								<div ng-show="Data.mode=='view'" class="col-sm-7">
							{{Data.selected${schema.key}.${prop.model}<#if prop.type=="enum">.title</#if><#if prop.type=="date">|persianDate:'yyyy/MM/dd'</#if><#if prop.type=="bool">|checkmark<#else>|EnToFaNumber</#if>}}
								</div>
								</#if>
								<#if prop.type!="disabled">
								<div <#if prop.type!="innerForm">ng-show="Data.mode!='view'" class="col-sm-7"<#else>class="col-sm-12"</#if>>
								<#if prop.type=="string">
								<#if !prop.widget??>
									<input type="text" class="form-control" name="${prop.model}" <#if prop.required>required</#if>
										ng-model="Data.selected${schema.key}.${prop.model}">
								<#elseif prop.widget=="textarea">
									<textarea type="text" class="form-control" name="${prop.model}" <#if prop.required>required</#if>
										ng-model="Data.selected${schema.key}.${prop.model}"></textarea>
								<#elseif prop.widget=="password">
									<input type="password" class="form-control" name="${prop.model}" <#if prop.required>required</#if>
										ng-model="Data.selected${schema.key}.${prop.model}">
								</#if>
								
								<#elseif prop.type=="integer">
									<input type="number" class="form-control" name="${prop.model}" <#if prop.required>required</#if>
										ng-model="Data.selected${schema.key}.${prop.model}">
								<#elseif prop.type=="bool">
									<input type="checkbox" class="" name="${prop.model}" <#if prop.required>required</#if>
										ng-model="Data.selected${schema.key}.${prop.model}">
								<#elseif prop.type=="enum">
									<select class="form-control" ng-model="Data.${prop.model}" name="${prop.model}" <#if prop.required>required</#if>
										ng-change="Func.onSelect${prop.model}(Data.${prop.model})" 
										ng-options="${prop.model} as ${prop.model}.title for ${prop.model} in Data.${prop.model}List">
									</select>
								<#elseif prop.type=="date">
									<vt-persian-date-picker name="${prop.model}" model="Data.selected${schema.key}.${prop.model}"></vt-persian-date-picker>
								<#elseif prop.type=="innerForm">
									<div class="panel panel-default"><div class="panel-heading">${prop.label}</div><div class="panel-body">
										<#list prop.innerModel.propertyList as innerProp>
										<div class="row rowForm"<#if innerProp.required> ng-class="{'has-error':${schema.key}Form.${innerProp.model}.$invalid && Data.validationClicked}"</#if>>
											<label class="col-sm-5"<#if innerProp.required> ng-class="{'required':Data.mode!='view'}"</#if>">${innerProp.model}</label>
											<div ng-show="Data.mode=='view'" class="col-sm-7">
										{{Data.selected${schema.key}.${prop.model}.${innerProp.model}|EnToFaNumber}}<#if innerProp.type=="date">|persianDate:'yyyy/MM/dd'</#if>
											</div>
											<div ng-show="Data.mode!='view'" class="col-sm-7">
											<#if innerProp.type=="string">
											<#if !innerProp.widget??>
												<input type="text" class="form-control" name="${innerProp.model}" <#if innerProp.required>required</#if>
													ng-model="Data.selected${schema.key}.${prop.model}.${innerProp.model}">
											<#elseif innerProp.widget=="textarea">
												<textarea type="text" class="form-control" name="${innerProp.model}" <#if innerProp.required>required</#if>
													ng-model="Data.selected${schema.key}.${prop.model}.${innerProp.model}"></textarea>
											</#if>
											<#elseif innerProp.type=="password">
												<input type="password" class="form-control" name="${innerProp.model}" <#if innerProp.required>required</#if>
													ng-model="Data.selected${schema.key}.${prop.model}.${innerProp.model}">
											
											<#elseif innerProp.type=="integer">
												<input type="number" class="form-control" name="${innerProp.model}" <#if innerProp.required>required</#if>
													ng-model="Data.selected${schema.key}.${prop.model}.${innerProp.model}">
											<#elseif innerProp.type=="bool">
												<input type="checkbox" class="" name="${innerProp.model}" <#if innerProp.required>required</#if>
													ng-model="Data.selected${schema.key}.${prop.model}.${innerProp.model}">
											<#elseif innerProp.type=="date">
												<vt-persian-date-picker name="${innerProp.model}" 
													model="Data.selected${schema.key}.${prop.model}.${innerProp.model}"></vt-persian-date-picker>
												<#else>
												<#if innerProp.widget??>
													<${innerProp.model}${innerProp.widget} <#if innerProp.required>required</#if>
														ng-model="Data.selected${schema.key}.${prop.model}.${innerProp.model}"></${innerProp.model}${innerProp.widget}>
												<#else>
													<${innerProp.model} <#if innerProp.required>required</#if>
														ng-model="Data.selected${schema.key}.${prop.model}.${prop.model}.${innerProp.model}"></${innerProp.model}>
												</#if>
												</#if>
												</div>
											</div>
										</#list>
									</div></div>
								<#else>
								<#if prop.widget??>
									<${prop.model}${prop.widget} <#if prop.required>required</#if>
										ng-model="Data.selected${schema.key}.${prop.model}"></${prop.model}${prop.widget}>
								<#else>
									<${prop.model} <#if prop.required>required</#if>
										ng-model="Data.selected${schema.key}.${prop.model}"></${prop.model}>
								</#if>
								</#if>
								</div>
								</#if>
							</div>	
						</#list>
						</form>
					</div>
				</div>
			</div>
			<div class="col-md-4" access-checker="API_VIEW_${schema.featureName}" ng-show="Data.searchMode=='advanced'">
				<div class="panel panel-default">
					<div class="panel-body">
						<div class="col-sm-12">
							<vt-search control-fn="Controller.${schema.key}AdvancedSearchController"></vt-search>
						</div>
					</div>
				</div>
			</div>
			
		</div>
	</div>
</div>



