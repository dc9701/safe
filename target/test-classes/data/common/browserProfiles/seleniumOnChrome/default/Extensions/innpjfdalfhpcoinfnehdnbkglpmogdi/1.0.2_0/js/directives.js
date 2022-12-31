'use strict';

/* Directives */


angular.module('ChromeModifyHeaders.directives', []).
    directive('appVersion', ['version', function(version) {
            return function(scope, elm, attrs) {
                elm.text(version);
            };
        }]).
//    directive('getStatus', function() {
//
//        return {
//            restrict: 'C',
//            replace: true,
//            transclude: true,
//            scope: {
//                rowStatus: '@rowStatus'
//            },
//            template: 
//                    '<div style="padding:4px 0 0 10px;" ng-switch on="rowStatus">' +
//                    '<span ng-switch-when="ENABLED" class="glyphicon glyphicon-off green"></span>' +
//                    '<span ng-switch-when="DISABLED" class="glyphicon glyphicon-off red"></span>' +
//                    '</div>'
//        };
//    }).
    directive('iconAction', function(){
        return {
            restrict: 'E',
            replace: true,
            scope: {
                iconClick: '&iconClick',
                rowStatus: '@rowStatus'
            },
            template: 
                    '<span ng-switch on="rowStatus">' +
                    '<a href="" ng-click="iconClick()">' +
                    '<span ng-switch-when="ENABLED" class="glyphicon glyphicon-off green"></span>' +
                    '<span ng-switch-when="DISABLED" class="glyphicon glyphicon-off red"></span>' +
                    '</a></span>'
        };
    }).
    directive('headerGrid', function() {
        return function(scope, element, attrs) {

            // apply DataTable options, use defaults if none specified by user
            var options = {};
            if (attrs.headerGrid.length > 0) {
                options = scope.$eval(attrs.headerGrid);
            } else {
                options = {
                    "bJQueryUI" : true,
                    "bPaginate" : false,
                    "bFilter" : false,
                    "bInfo" : false,
                    "bProcessing" : false,
                    "bDeferRender" : true,
                    "bSort": true,
                    "bAutoWidth": false,
                    "aaSorting": [[1, 'asc']]
                };
            }

            // Tell the dataTables plugin what columns to use
            // We can either derive them from the dom, or use setup from the controller           
            var explicitColumns = [];
            element.find('th').each(function(index, elem) {
                explicitColumns.push($(elem).text());
            });
            if (explicitColumns.length > 0) {
                options["aoColumns"] = explicitColumns;
            } else if (attrs.aoColumns) {
                options["aoColumns"] = scope.$eval(attrs.aoColumns);
            }

            // aoColumnDefs is dataTables way of providing fine control over column config
            if (attrs.aoColumnDefs) {
                options["aoColumnDefs"] = scope.$eval(attrs.aoColumnDefs);
            }
            
            if (attrs.fnRowCallback) {
                options["fnRowCallback"] = scope.$eval(attrs.fnRowCallback);
            }
            
            // apply the plugin
            scope.dataTable = element.dataTable(options);

            // all this code just to select a row
            // need to find a better way
            if (attrs.id) {
                scope[attrs.id] = attrs.id;
                $("#"+attrs.id+" tbody").click(function(event) {
                    var toAdd = true;
                    var parentNode = $(event.target.parentNode);
                    if (parentNode.hasClass('row_selected')) {
                        parentNode.removeClass('row_selected');
                        toAdd = false;
                    } 
                    $(scope.dataTable.fnSettings().aoData).each(function() {
                        $(this.nTr).removeClass('row_selected');
                    });
                    if (toAdd) {
                        parentNode.addClass('row_selected');
                    }
                });
            }
            
            // watch for any changes to our data, rebuild the DataTable
            scope.$watch(attrs.aaData, function(value) {
                var val = value || null;
                if (val) {
                    scope.dataTable.fnClearTable();
                    scope.dataTable.fnAddData(scope.$eval(attrs.aaData));
                }
            });
        };
    });
