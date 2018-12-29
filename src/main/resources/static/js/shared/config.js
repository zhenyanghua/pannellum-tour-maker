var servletPath = "/zuul";
var rootPath = "/tour-editor";
var serverPath = rootPath + "/app";
var userManagement = "/um";
var authServer = "/auth-server";
var configServer = "/config-server";
var apiUrl = rootPath + "/api/v1";

// externalized properties from authServer
var clientId, clientSecret;

var configXhr = $.getJSON(configServer + authServer + '/prod')
	.done(function(res) {
		var propertySources = res.propertySources;

		for(var i = 0; i < propertySources.length; i++) {
			var ps = propertySources[i];
			clientId = ps.source["oauth.client-id"];
			clientSecret = ps.source["oauth.client-secret"];
			if (clientId && clientSecret) return;
		}
	});
