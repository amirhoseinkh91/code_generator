angular.module('${schema.key}Module').config(['$stateProvider', function($stateProvider) {
	var ${schema.key}States = [
		{
			state: "home.management.${schema.key}",
			config: {
				url: '/${schema.key}/',
				views: {
					'mainContent@home': {
						templateUrl: "app/modules/management/${schema.key}/${schema.key}.html",
						controller: '${schema.key}Ctrl'
					}
				}
			}
		}
	];
	${schema.key}States.forEach(function(state) {
		$stateProvider.state(state.state, state.config);
	});
			
}]);
