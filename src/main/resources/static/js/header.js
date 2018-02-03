var $username = $("#username");

var currentUser = JSON.parse(sessionStorage.getItem("current-user"));

$username.text(currentUser.user_name);