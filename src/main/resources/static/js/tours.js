var $tourList = $("#tour-list-container");
var $modalDeleteTour = $("#modal-delete-tour");
var $deleteTourYes = $("#delete-tour-yes");

var selectedTourName;

getTours();

function onDeleteTour(tourName) {
	$modalDeleteTour.find('code').text(tourName);
	$modalDeleteTour.modal("open");
	selectedTourName = tourName;
}

function doDeleteTour() {
	if (!selectedTourName) return;
	console.log('delete tour ' + selectedTourName);
}

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
							.append($("<a>").addClass('btn-floating blue tooltipped').attr("href", "/tours/" + tour.name)
								.attr('data-position', 'top').attr('data-delay', 50).attr('data-tooltip', 'Edit Tour')
								.append($("<i>").addClass('material-icons').text("edit")))
							.append($("<a>").addClass('btn-floating red tooltipped').attr("href", "#")
								.attr('data-position', 'top').attr('data-delay', 50).attr('data-tooltip', 'Delete Tour')
								.append($("<i>").addClass('material-icons').text("delete"))
								.click(function() {onDeleteTour(tour.name);}))
						)));
			});

			$('.tooltipped').tooltip();
			$(".modal").modal({
				dismissible: false
			});
			$deleteTourYes.click(doDeleteTour);
		});
}