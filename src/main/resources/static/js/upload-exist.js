$.validator.setDefaults({ ignore: [] });

$('select').change(function(){ $(this).valid(); })
	.material_select();
var $selectTourName = $('#select-tour-name');

getTourNames();

$("#form-upload").validate({
	errorElement: 'div',
	rules: {
		name: {
			required: true
		},
		file: {
			required: true,
			accept: "application/zip,application/x-zip,application/octet-stream,application/x-zip-compressed"
		}
	},

	messages: {
		name: {
			required: "Tour name is required."
		},
		file: {
			required: "Photo zip file is required.",
			accept: "Only support *.zip file."
		}
	},

	submitHandler: function (form) {
		var name = $("#select-tour-name").val();
		var file = $("#input-file")[0].files[0];
		var northOffset = $("#input-north-offset").val();

		var form = new FormData();
		form.append("file", file);
		form.append("name", name);
		if (northOffset) form.append("northOffset", northOffset);

		var options = {
			method: "POST",
			mimeType: "multipart/form-data",
			data: form,
			cache: false,
			contentType: false,
			processData: false
		};

		// $.ajax(apiUrl + "/public/guest/tours", options)
		// 	.done(function (response, status, xhr) {
		// 		if (xhr.status === 200) {
		// 			Materialize.toast('Photos were successfully uploaded to the server. ' +
		// 				'It might take a moment for the server to process these photos. ' +
		// 				'You will find the successfully processed tour in the tour list.', 10000)
		// 		}
		// 	})
		// 	.fail(function(xhr) {
		// 		var message = JSON.parse(xhr.responseText).message;
		// 		Materialize.toast('Photos upload failed. ' + message, 10000)
		// 	});
	}
});

function getTourNames() {
	$.getJSON(apiUrl + "/public/guest/tours/names")
		.done(function (names) {
			names.forEach(function (name) {
				$selectTourName.append($("<option>").val(name).text(name));
			});
			$selectTourName.material_select();
		});
}

