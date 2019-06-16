angular.module('${schema.key}Module').controller('${schema.key}Ctrl', function($scope, ${schema.key}Srvc) {

	$scope.Data = {
		mode: 'view',
		searchMode: 'none',
		${schema.key}List: [],
		selected${schema.key}: {
		<#list schema.propertyList as prop><#if prop.type=="innerForm">
			${prop.model}: null,
		</#if></#list>
		},
		original${schema.key}: {},
		<#list schema.propertyList as prop><#if prop.type=="enum">
		${prop.model}List: [],
		</#if></#list>
		validationClicked: false
	}
	
	$scope.Func = {
		onAdd${schema.key}Click: function(){
			$scope.Data.mode = 'add';
			$scope.Func.reset();
		},
		onSelect${schema.key}: function(${schema.key}){
			${schema.key}Srvc.get${schema.key}(${schema.key}.uid).then(function(response){
				$scope.Data.selected${schema.key} = response.data.originalElement;
				$scope.Data.original${schema.key} = angular.copy($scope.Data.selected${schema.key});
				<#list schema.propertyList as prop>
				<#if prop.type=="enum">
				$scope.Func.initiate${prop.model}();
				<#elseif prop.type=="date">
				$scope.Data.selected${schema.key}.${prop.model} = new Date($scope.Data.selected${schema.key}.${prop.model});
				</#if>
				</#list>
				$scope.Data.mode='view';
			});
		},
		onEdit${schema.key}Click: function(){
			$scope.Data.mode = 'edit';
		},
		onSave${schema.key}Click: function(){
			if($scope.${schema.key}Form.$valid){
				${schema.key}Srvc.save${schema.key}($scope.Data.selected${schema.key}).then(function(response){
					$scope.Controller.${schema.key}ListController.refreshList()
					$scope.Func.resetForm();
				});
			}else{
				$scope.Data.validationClicked = true;
			}
		},
		onUpdate${schema.key}Click: function(){
			if($scope.${schema.key}Form.$valid){
				${schema.key}Srvc.update${schema.key}($scope.Data.selected${schema.key}).then(function(response){
					$scope.Controller.${schema.key}ListController.refreshList()
					$scope.Func.resetForm();
				});
			}else{
				$scope.Data.validationClicked = true;
			}
		},
		onDelete${schema.key}Click: function(){
			${schema.key}Srvc.delete${schema.key}($scope.Data.selected${schema.key}.uid).then(function(response){
				$scope.Func.resetForm();
				$scope.Func.reset();
				$scope.Controller.${schema.key}ListController.refreshList();
			});
		},
		onCancelClick: function(){
			$scope.Data.selected${schema.key} = angular.copy($scope.Data.original${schema.key});
			<#list schema.propertyList as prop>
			<#if prop.type=="enum">
			$scope.Func.initiate${prop.model}();
			<#elseif prop.type=="date">
			$scope.Data.selected${schema.key}.${prop.model} = new Date($scope.Data.selected${schema.key}.${prop.model});
			</#if>
			</#list>
			$scope.Func.resetForm();
		},
		
		onChangeSearchModeClick: function(mode){
			$scope.Data.searchMode = mode;
		},
		onSearchClick: function(advancedMode){
			if(advancedMode){
				$scope.Func.onChangeSearchModeClick('advanced');
				$scope.Controller.${schema.key}ListController.searchQuery = $scope.Controller.${schema.key}AdvancedSearchController.searchQuery;
				$scope.Controller.${schema.key}ListController.searchableFieldInfo = $scope.Controller.${schema.key}AdvancedSearchController.searchableFieldInfo;
			}else{
				$scope.Func.onChangeSearchModeClick('quick');
				$scope.Controller.${schema.key}ListController.searchQuery = $scope.Controller.${schema.key}SearchController.searchQuery;
				$scope.Controller.${schema.key}ListController.searchableFieldInfo = $scope.Controller.${schema.key}SearchController.searchableFieldInfo;			
			}
			$scope.Controller.${schema.key}ListController.refreshList(true);
		},
		onExitSearchModeClick: function(){
			$scope.Func.onChangeSearchModeClick('none');
			$scope.Controller.${schema.key}AdvancedSearchController.searchQuery = {};
			$scope.Controller.${schema.key}SearchController.searchQuery = {};
			$scope.Controller.${schema.key}ListController.exitSearchMode();
		},
		<#list schema.propertyList as prop><#if prop.type=="enum">
		
		get${prop.model}List: function(){
			${schema.key}Srvc.get${prop.model}List().then(function(response){
				for ( var int = 0; int < response.data.originalElement.length; int++) {
					$scope.Data.${prop.model}List.push(response.data.originalElement[int]);					
				}
			});
		},
		onSelect${prop.model}: function(${prop.model}){
			$scope.Data.${prop.model} = ${prop.model};
			$scope.Data.selected${schema.key}.${prop.model} = {
				uid:$scope.Data.${prop.model}.uid,
				title:$scope.Data.${prop.model}.title
			};
		},
		initiate${prop.model}: function(){
			$scope.Data.${prop.model} = null;
			if($scope.Data.selected${schema.key}.${prop.model}){
				for ( var int = 0; int < $scope.Data.${prop.model}List.length; int++) {
					if($scope.Data.${prop.model}List[int].uid==$scope.Data.selected${schema.key}.${prop.model}.uid)
						$scope.Data.${prop.model} = $scope.Data.${prop.model}List[int];
				}
			}
		},
		</#if></#list>
		
		reset: function(){
			$scope.Data.selected${schema.key} = {};
			<#list schema.propertyList as prop>
			<#if prop.type=="innerForm">
			$scope.Data.selected${schema.key}.${prop.model} = {};
			<#elseif prop.type=="enum">
			$scope.Data.${prop.model} = null;
			</#if>
			</#list>
		},
		resetForm: function() {
			$scope.Data.mode = 'view';
			$scope.Data.validationClicked = false;
		}
		
	}
	
	$scope.Controller = {
		${schema.key}ListController : {
			headers: [
			<#list schema.propertyList as prop>
				<#if prop.type == "string" || prop.type=="integer" || prop.type=="bool">
				{key:'${prop.model}'},		
				<#elseif prop.type == "enum">
				{key:'${prop.model}.title'},
				<#elseif prop.type == "innerForm">
				<#list prop.innerModel.propertyList as innerProp>
				<#if innerProp.type == "string" || innerProp.type=="integer" || innerProp.type=="bool">
				{key:'${prop.model}.${innerProp.model}'},
				</#if>
				</#list>
				</#if>
			</#list>
			],
			getList : ${schema.key}Srvc.getFull${schema.key}List,
			onListItemSelect : $scope.Func.onSelect${schema.key},
			searchFunction : ${schema.key}Srvc.search${schema.key},
		},
		${schema.key}SearchController: {
			advanced: false,
			searchableFieldInfo: [
			<#list schema.propertyList as prop>
			<#if prop.searchLevel==1>
				<#if prop.type == "innerForm">
					<#list prop.innerModel.propertyList as innerProp>
					<#if innerProp.searchLevel==1>
				{key:"${prop.model}.${innerProp.model}", type:"${innerProp.type}", label:"${innerProp.model}"},
					</#if>
					</#list>
				<#elseif prop.type == "enum">
				{key:"${prop.model}", type:"${prop.type}", label:"${prop.model}", itemList:$scope.Data.${prop.model}List},
				<#else>
				{key:"${prop.model}", type:"${prop.type}", label:"${prop.model}"},
				</#if>
			</#if>
			</#list>
			],
			onSearchClick: $scope.Func.onSearchClick,
			onExitSearchModeClick: $scope.Func.onExitSearchModeClick
		},
		${schema.key}AdvancedSearchController: {
			advanced: true,
			searchableFieldInfo: [
			<#list schema.propertyList as prop>
			<#if prop.searchLevel==1 || prop.searchLevel==2 >
				<#if prop.type == "innerForm">
					<#list prop.innerModel.propertyList as innerProp>
					<#if innerProp.searchLevel==1 >
				{key:"${prop.model}.${innerProp.model}", type:"${innerProp.type}", label:"${innerProp.model}"},
					</#if>
					</#list>
				<#elseif prop.type == "enum">
				{key:"${prop.model}", type:"${prop.type}", label:"${prop.model}", itemList:$scope.Data.${prop.model}List},
				<#else>
				{key:"${prop.model}", type:"${prop.type}", label:"${prop.model}"},
				</#if>
			</#if>
			</#list>
			],
			onSearchClick: $scope.Func.onSearchClick,
			onExitSearchModeClick: $scope.Func.onExitSearchModeClick
		}
	}
	
	var Run = function(){
	<#list schema.propertyList as prop><#if prop.type=="enum">
		$scope.Func.get${prop.model}List();
	</#if></#list>
	}
	
	Run();
});