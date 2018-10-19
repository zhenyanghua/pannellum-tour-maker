function getRequestParamsFromSearch(search) {
	return getRequestParams(search.substring(1));
}

function getRequestParams(queryString) {
	var obj = {};

	queryString.split("&")
		.forEach(function(kv) {
			var arr = kv.split("=");
			obj[arr[0]] = arr[1];
		});

	return obj;
}