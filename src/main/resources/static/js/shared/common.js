var CREDENTIAL = "credential";
var INVALID_TOKEN = "invalid_token";
var INVALID_ACCESS_TOKEN_DESCRIPTION = "Access token expired";
var INVALID_REFRESH_TOKEN_DESCRIPTION = "Invalid refresh token";
var UNAUTHORIZED = "unauthorized";
var ACCESS_DENIED = "access_denied";

function checkAuthHeaders() {
	var headers = {};
	var credential = JSON.parse(sessionStorage.getItem('credential'));
	if (credential && credential["access_token"]) {
		headers["Authorization"] = 'Bearer ' + credential["access_token"];
	}
	return headers;
}

function redirectToLogin() {
	window.location.assign(userManagement + "/login?from=" + window.location.pathname);
}

function handleAuthError(xhr, retryCallback, failCallback) {
	var err = xhr.responseJSON;

	if (err.error === INVALID_TOKEN &&
		new RegExp(INVALID_ACCESS_TOKEN_DESCRIPTION).test(err.error_description)) {
		console.log("Refreshing token now");
		this.refreshToken()
			.done(function(credential) {
				console.log(credential);
				sessionStorage.setItem(CREDENTIAL, JSON.stringify(credential));
				retryCallback();
			}).fail(function(refreshXhr) {
				var err = refreshXhr.responseJSON;
				if ((err.error === INVALID_TOKEN &&
					new RegExp(INVALID_REFRESH_TOKEN_DESCRIPTION).test(err.error_description)) ||
					err.error === UNAUTHORIZED || err.error === ACCESS_DENIED) {
					redirectToLogin();
				} else {
					failCallback();
				}
			});
	} else if (err.error === UNAUTHORIZED || err.error === ACCESS_DENIED) {
		redirectToLogin();
	} else {
		failCallback();
	}
}

function refreshToken() {
	return $.ajax({
		url: authServer + "/oauth/token",
		method: 'POST',
		data: {
			grant_type: "refresh_token",
			refresh_token: JSON.parse(sessionStorage.getItem(CREDENTIAL)).refresh_token
		},
		headers: {
			"Authorization": "Basic " + btoa(clientId + ":" + clientSecret)
		}
	})
}
