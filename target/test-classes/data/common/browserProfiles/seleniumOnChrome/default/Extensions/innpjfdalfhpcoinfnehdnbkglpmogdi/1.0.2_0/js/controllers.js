'use strict';

/* Controllers */

angular.module('ChromeModifyHeaders.controllers', []).
        controller("OptionsCtrl", 
                function($scope, cmhControlService, cmhHeaderListService, $compile) {
                    
            $scope.headers = cmhHeaderListService.loadHeaders();
            $scope.cmh_index = -1;
            $scope.cmh_action = '';
            $scope.cmh_hdr_name = '';
            $scope.cmh_hdr_value = '';
            $scope.cmh_hdr_description = '';
            $scope.gridSelection = [];
            $scope.dataTable;
                    
            $scope.isStarted = function() {
                return cmhControlService.getStatus() === 'STARTED';
            };
            $scope.isStopped = function() {
                return cmhControlService.getStatus() === 'STOPPED';
            };
            $scope.start = function() {
                cmhControlService.setStatus('STARTED');
                cmhHeaderListService.saveHeaders($scope.headers.slice());
            };
            $scope.stop = function() {
                cmhControlService.setStatus('STOPPED');
                cmhHeaderListService.saveHeaders($scope.headers.slice());
            };
            $scope.save = function() {
                var row = {
                    index: $scope.cmh_index,
                    action: $scope.cmh_action, 
                    name: $scope.cmh_hdr_name, 
                    value: $scope.cmh_hdr_value, 
                    description:$scope.cmh_hdr_description, 
                    state: "ENABLED"
                };
                if ($scope.validate(row)) {
                    $scope.saveRow(row);
                    $scope.clearForm();
                }
            };
            
            $scope.validate = function(row) {
                if (!row) return false;
                if (row.action === 'Add' || (row.action === 'Modify')) {
                    if (row.name && row.value) return true;
                } else if (row.action === 'Filter') {
                    if (row.name) return true;
                } else return false;
            };
            
             $scope.saveRow = function(row) {
                var existing = $scope.headers.slice();
                if (row.index && row.index !== -1) {
                    //existing.push(row);
                    existing.splice(row.index-1, 1, row);
                } else {
                   //existing.splice(row.index, 1); 
                   row.index = existing.length+1;
                   existing.splice(row.index, 0, row);
                }
                cmhHeaderListService.saveHeaders(existing);
                $scope.headers = existing;
            };
            $scope.clearForm = function() {
                $scope.cmh_index = -1;
                $scope.cmh_action = '';
                $scope.cmh_hdr_name = '';
                $scope.cmh_hdr_value = '';
                $scope.cmh_hdr_description = '';
            };
            
            $scope.toggleSelected = function() {
                var index = $scope.fnGetSelectedIndex();
                $scope.toggle(index);
            }
            $scope.toggle = function(index) {
                var existing = $scope.headers.slice();
                var header = existing[index];
                if (header) {
                    cmhHeaderListService.toggleState(header);
                }
                cmhHeaderListService.saveHeaders(existing);
                $scope.headers = existing;
            };
            
            $scope.delete = function() {
                var selIndex = $scope.fnGetSelectedIndex();
                if (selIndex >= 0) {
                    var existing = $scope.headers.slice();
                    existing.splice(selIndex, 1);
                    $scope.resetOrder(existing);
                    $scope.headers = existing;
                    $scope.clearForm();
                    cmhHeaderListService.saveHeaders(existing);
                }
            };
            
            $scope.edit = function() {
                var selIndex = $scope.fnGetSelectedIndex();
                if (selIndex >= 0) {
                    var header =  $scope.headers[selIndex];

                    $scope.cmh_index = header.index;
                    $scope.cmh_action = header.action;
                    $scope.cmh_hdr_name = header.name;
                    $scope.cmh_hdr_value = header.value;
                    $scope.cmh_hdr_description = header.description;
                }
            };
            
            $scope.moveTop = function() {
                var selIndex = $scope.fnGetSelectedIndex();
                if (selIndex >= 0) {
                    var existing = $scope.headers.slice();
                    existing.move(selIndex, 0);
                    $scope.resetOrder(existing);
                    $scope.headers = existing;
                    cmhHeaderListService.saveHeaders(existing);
                }
            };
            
            $scope.moveBottom = function() {
                var selIndex = $scope.fnGetSelectedIndex();
                if (selIndex >= 0) {

                    var existing = $scope.headers.slice();
                    existing.move(selIndex, existing.length-1);
                    $scope.resetOrder(existing);
                    $scope.headers = existing;
                    cmhHeaderListService.saveHeaders(existing);
                }
            };
            
            $scope.enableAll = function() {
                var existing = $scope.headers.slice();
                for (var i=0;i<existing.length;i++) {
                    var header = existing[i];
                    if (header) {
                        header.state = 'ENABLED';
                    }
                }
                cmhHeaderListService.saveHeaders(existing);
                $scope.headers = existing;
            };
            
            $scope.disableAll = function() {
                var existing = $scope.headers.slice();
                for (var i=0;i<existing.length;i++) {
                    var header = existing[i];
                    if (header) {
                        header.state = 'DISABLED';
                    }
                }
                cmhHeaderListService.saveHeaders(existing);
                $scope.headers = existing;
            };
            
            $scope.rowCallback = function(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
                var html = '<icon-action icon-click="toggle('+iDisplayIndex+')" row-status="'+aData.state+'"></icon-action>';
                $('td:eq(5)', nRow).html($compile(html)($scope));
                return nRow;
            };

            /* Get the rows which are currently selected */
            $scope.fnGetSelectedIndex = function () {
                var aTrs = $scope.dataTable.fnGetNodes();
                for (var i = 0; i < aTrs.length; i++) {
                    if ($(aTrs[i]).hasClass('row_selected')) {
                        return i;
                    }
                }
                return -1;
            };
            
            
//            /* Get the rows which are currently selected */
//            $scope.fnGetSelected = function () {
//                var aReturn = new Array();
//                var aTrs = $scope.dataTable.fnGetNodes();
//
//                for (var i = 0; i < aTrs.length; i++) {
//                    if ($(aTrs[i]).hasClass('row_selected')) {
//                        aReturn.push(aTrs[i]);
//                    }
//                }
//                return aReturn;
//            };
            $scope.resetOrder = function(existing) {
                for (var i=0;i<existing.length;i++) {
                    var header = existing[i];
                    if (header) {
                        header.index = i+1;
                    }
                }
            };

            $scope.columnDefs = [
                {"mDataProp": "index", "aTargets": [0]},
                {"mDataProp": "action", "aTargets": [1]},
                {"mDataProp": "name", "aTargets": [2]},
                {"mDataProp": "value", "aTargets": [3]},
                {"mDataProp": "description", "aTargets": [4]},
                {"mDataProp": "state", "aTargets": [5]}
            ];
            
            $scope.overrideOptions = {
                "bJQueryUI" : false,
                "bPaginate" : false,
                "bFilter" : false,
                "bInfo" : false,
                "bProcessing" : false,
                "bDeferRender" : true,
                "bSort": true,
                "sScrollY": "100%",
                "sScrollX": "100%",
                "bAutoWidth": false,
                "aaSorting": [[0, 'asc']]
            };  
            
            $scope.$watch('cmh_action', 
                function(newValue, oldValue) {
                    if (newValue === oldValue) {
                        return;
                    }
                    if (newValue === 'Add' || newValue === 'Modify') {
                        $('#cmh_hdr_name').attr('disabled', false);
                        $('#cmh_hdr_description').attr('disabled', false);
                        $('#cmh_hdr_value').attr('disabled', false);
                    } else if (newValue === 'Filter') {
                        $('#cmh_hdr_name').attr('disabled', false);
                        $('#cmh_hdr_description').attr('disabled', false);
                        $('#cmh_hdr_value').attr('disabled', true);
                    } else {
                        $('#cmh_hdr_name').attr('disabled', true);
                        $('#cmh_hdr_description').attr('disabled', true);
                        $('#cmh_hdr_value').attr('disabled', true);
                    }
                }
            );
            $('#controls').tooltip({
              selector: "[data-toggle=tooltip]",
              container: "body"
            });
            cmhHeaderListService.updateHeaderList($scope.headers);
            cmhControlService.setExtensionStatus();
        }
    );