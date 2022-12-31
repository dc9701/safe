'use strict';


// Declare app level module which depends on filters, and services
angular.module('ChromeModifyHeaders', [
  'ngRoute',
  'ChromeModifyHeaders.filters',
  'ChromeModifyHeaders.services',
  'ChromeModifyHeaders.directives',
  'ChromeModifyHeaders.controllers'
]).
config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/view1', {templateUrl: 'partials/partial1.html', controller: 'OptionsCtrl'});
  $routeProvider.when('/view2', {templateUrl: 'partials/partial2.html', controller: 'OptionsCtrl'});
  $routeProvider.otherwise({redirectTo: '/view1'});
}]);

Array.prototype.move = function (old_index, new_index) {
    while (old_index < 0) {
        old_index += this.length;
    }
    while (new_index < 0) {
        new_index += this.length;
    }
    if (new_index >= this.length) {
        var k = new_index - this.length;
        while ((k--) + 1) {
            this.push(undefined);
        }
    }
    this.splice(new_index, 0, this.splice(old_index, 1)[0]);
    return this; // for testing purposes
};

