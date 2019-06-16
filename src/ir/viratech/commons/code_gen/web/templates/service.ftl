angular.module('${schema.key}Module', []);
angular.module('${schema.key}Module').factory('${schema.key}Srvc', [ 'Restangular', '$q',
	function(Restangular, $q) {

	return{
		getFull${schema.key}List: function(startPage, pageLen){
			return Restangular.all('${schema.path}/items').getList(
					{start:startPage, len:pageLen, extent:"full"});
		},
		getExtend${schema.key}List: function(){
			return Restangular.all('${schema.path}/items').getList({len:-1});
		},
		get${schema.key}List: function(){
			return Restangular.all('${schema.path}/items').getList();
		},
		get${schema.key}: function(uid){
			return Restangular.one('${schema.path}/items/'+uid).get();
		},
		save${schema.key}: function(data){
			return Restangular.all('${schema.path}/items').post(data);
		},
		update${schema.key}: function(data){
			var uid = data.uid;
			return Restangular.all('${schema.path}/items/'+uid).post(data);
		},
		delete${schema.key}: function(uid){
			return Restangular.one('${schema.path}/items/'+uid).remove();
		},
		search${schema.key}: function(query, startPage, pageLen){
			return Restangular.all('${schema.path}/items').customPOST(query,'',
				{start:startPage, len:pageLen, extent:'full'},{'X-HTTP-Method-Override':'GET'});
		},
		
		<#list schema.propertyList as prop>
		<#if prop.type! == "enum">
		<#if prop.enumType??>
		get${prop.model}List: function(){
			return Restangular.all('${prop.enumType}/items').getList({len:-1});
		},
		<#else>
		get${prop.model}List: function(){
			return Restangular.all('${schema.path}/${prop.model}').getList();
		},
		</#if>
		</#if>
		</#list>
	}
	
}]);