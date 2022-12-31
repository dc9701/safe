var addHeaders = [];
var modifyHeaders = {};
var filterHeaders = {};
var enabled = false;

function setStatus(status) {
    enabled = status === 'STARTED';
}

function setHeaders(headers) {
    for (var j = 0; j < headers.length; ++j) {
        var header = headers[j];
        if (header && header.state === 'ENABLED') {
            if (header.action === 'Add')
                addHeaders.push(header);
            else if (header.action === 'Modify')
                modifyHeaders[header.name] = header;
            else if (header.action === 'Filter')
                filterHeaders[header.name] = header;
        }
    }
}

chrome.webRequest.onBeforeSendHeaders.addListener(function(details) {
    var headers = details.requestHeaders,
            blockingResponse = {};

    var targetHeaders = [];
    if (enabled) {
        for (var i = 0, l = headers.length; i < l; ++i) {
            var sourceHeader = headers[i];
            var targetHeader = modifyHeaders[sourceHeader.name];

            if (targetHeader) {
                targetHeaders.push({name: targetHeader.name, value: targetHeader.value});
            } else {
                targetHeader = filterHeaders[sourceHeader.name];
                if (targetHeader) {
                    continue;
                } else {
                    targetHeaders.push({name: sourceHeader.name, value: sourceHeader.value});
                }
            }
        }
        for (var i = 0, l = addHeaders.length; i < l; ++i) {
            var header = addHeaders[i];
            targetHeaders.push({name: header.name, value: header.value});
        }
    } else {
        targetHeaders = headers;
    }
    blockingResponse.requestHeaders = targetHeaders;
    return blockingResponse;
},
        {urls: ["<all_urls>"]}, ['requestHeaders', 'blocking']);