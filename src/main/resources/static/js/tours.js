var $tourList = $("#tour-list-container");

getTours();

function getTours() {
	$.getJSON(apiUrl + "/public/guest/tours/")
		.done(function (tours) {
			tours.forEach(function (tour) {
				var preview = tour.mapPath ?
					$("<img>").attr("src", tour.mapPath) :
					$("<div>").addClass("preview");
				$tourList.append($("<div>").addClass("col s12 m4")
					.append($("<div>").addClass("card").attr('id', "tour-list-item-" +tour.name)
						.append($("<div>").addClass("card-image")
							.append(preview))
						.append($("<div>").addClass("card-content")
							.append($("<span>").addClass("card-title red-text text-lighten-2").text(tour.name)))
						.append($("<div>").addClass("card-action")
							.append($("<a>").attr("href", "/tours/" + tour.name)
								.text("Edit Tour")
							))));
			});
		});
}