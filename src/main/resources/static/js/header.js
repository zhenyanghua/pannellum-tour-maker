var $username = $("#username");
var $logout = $("#logout");

$logout.click(function () {
	$.post(authServer + "/sessions/destroy");
	document.cookie = "_s=; expires=Thu, 01 Jan 1970 00:00:00 GMT;path=/";
	sessionStorage.clear();
});

var currentUser = JSON.parse(sessionStorage.getItem("current-user"));

$username.text(currentUser.user_name);