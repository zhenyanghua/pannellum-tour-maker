var $taskList = $("#task-list-container");

getTasks();

function getTasks() {
	$.getJSON(apiUrl + "/public/guest/tasks/")
		.done(function (tasks) {
			tasks.forEach(function (task) {
				$taskList.append($("<div>").addClass("col s12 m4")
					.append($("<div>").addClass("card").attr('id', "task-list-item-" +task.name)
						.append($("<div>").addClass("card-content")
							.append($("<span>").addClass("card-title red-text text-lighten-2").text(task.name)))
						.append($("<div>").addClass("card-action")
							.append($("<a>").attr("href", "#/delete" )
								.text("Delete Task")
							))));
			});
		});
}