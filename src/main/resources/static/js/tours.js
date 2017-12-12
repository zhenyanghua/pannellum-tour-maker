var $tourList = $("#tour-list-container");
var $modalDeleteTour = $("#modal-delete-tour");
var $deleteTourYes = $("#delete-tour-yes");
var $tourNameInput = $("#tour-name");

var selectedTourName;

getTours();

$(".modal").modal({
	dismissible: false,
	complete: resetInput
});

$deleteTourYes.click(doDeleteTour);

$tourNameInput.keyup(validateName);

function resetInput() {
	$tourNameInput.val(null);
	Materialize.updateTextFields();
}

function validateName(e) {
	if (e.target.value === selectedTourName) {
		$deleteTourYes.removeClass('disabled');
	} else {
		$deleteTourYes.addClass('disabled');
	}
}

function onDeleteTour(tourName) {
	$modalDeleteTour.find('code').text(tourName);
	$modalDeleteTour.modal("open");
	selectedTourName = tourName;
}

function doDeleteTour() {
	if (!selectedTourName) return;
	console.log('delete tour ' + selectedTourName);

	$.ajax(apiUrl + "/public/guest/tours/" + selectedTourName, {
		method: "DELETE",
		contentType: "application/json"
	}).done(function () {
		selectedTourName = undefined;
		getTours();
	})
}

function getTours() {
	$tourList.empty();
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
		});
}