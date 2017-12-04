var $tourList = $("#tour-list-container");

getTours();

function getTours() {
	$.getJSON(apiUrl + "/public/guest/tours/")
		.done(function (tours) {
			tours.forEach(function (tour) {
				$tourList.append($("<li>").addClass("collection-item").attr('id', "tour-list-item-" +tour.name)
					.append($("<div>").text(tour.name)
						.append($("<a>").attr("href", "/tours/" + tour.name).addClass("secondary-content")
							.append($("<i>").addClass("material-icons").text("arrow_forward")))
					));
			});
		});
}