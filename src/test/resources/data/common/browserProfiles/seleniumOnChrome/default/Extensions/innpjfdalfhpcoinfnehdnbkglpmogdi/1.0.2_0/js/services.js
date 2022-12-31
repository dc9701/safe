'use strict';

angular.module('ChromeModifyHeaders.services', []).value('version', '0.1')
        .service('cmhControlService', function() {
            this.getStatus = function() {
                var value = $.jStorage.get('CMH.CTRL');
                if (!value) value = 'STOPPED';
                return value;
            },
            this.setStatus = function(status) {
                if (status && 
                        ((status === 'STARTED') || (status === 'STOPPED')))  {
                    $.jStorage.set('CMH.CTRL', status);
                }
                this.setExtensionStatus();
            },
            this.setExtensionStatus = function() {
                var bkg = chrome.extension.getBackgroundPage();
                bkg.setStatus(this.getStatus());                
            };
        }).service('cmhHeaderListService', function() {
            this.toggleState = function(header) {
                if (header.state === 'DISABLED') header.state = 'ENABLED';
                else if (header.state === 'ENABLED') header.state = 'DISABLED';
            },
            this.setState = function(state) {
                if (state && 
                        ((state === 'ENABLED') || (state === 'DISABLED')))  {
                    header.state = state;
                }
            },
            this.saveHeaders = function(headers) {
                $.jStorage.set('CMH.HEADERS', headers);
                this.updateHeaderList(headers);
            },
            this.loadHeaders = function() {
                var headers = $.jStorage.get('CMH.HEADERS');
                if (!headers) headers = [];
                return headers;
            },
            this.updateHeaderList = function(headers) {
                var bkg = chrome.extension.getBackgroundPage();
                bkg.setHeaders(headers.slice());
            };
        });
