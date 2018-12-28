var $progressBar = $('.progress');
var $determinateBar = $progressBar.find('.determinate');
var $uploadButton = $('#photo-upload');
var $abortButton = $('#abort');
var xhrUpload;

configXhr.then(function (value) {
	$abortButton.click(function () {
		if (xhrUpload) {
			xhrUpload.abort();
			xhrUpload = undefined;
		}
	});

	$("#form-upload").validate({
		errorElement: 'div',
		rules: {
			name: {
				required: true
			},
			file: {
				required: true,
				accept: "application/zip,application/x-zip,application/octet-stream,application/x-zip-compressed"
			},
			map: {
				accept: "image/jpeg, image/png"
			}
		},

		messages: {
			name: {
				required: "Tour name is required."
			},
			file: {
				required: "Photo zip file is required.",
				accept: "Only support *.zip file."
			},
			map: {
				accept: "Only support jpg or png file."
			}
		},

		submitHandler: function () {
			$uploadButton.addClass('disabled');
			var name = $("#input-tourname").val();
			var file = $("#input-file")[0].files[0];
			var mapInput = $("#input-map")[0];
			var map = mapInput.files.length > 0 ? mapInput.files[0] : undefined;
			var northOffset = $("#input-north-offset").val();

			var form = new FormData();
			form.append("file", file);
			form.append("name", name);
			if (map) form.append("map", map);
			if (northOffset) form.append("northOffset", northOffset);

			var options = {
				method: "POST",
				mimeType: "multipart/form-data",
				data: form,
				cache: false,
				contentType: false,
				processData: false,
				xhr: function() {
					var xhr = new XMLHttpRequest();
					xhr.upload.onloadstart = onLoadStart;
					xhr.upload.onprogress = onProgress;
					return xhr;
				},
				headers: checkAuthHeaders()
			};

			validateTour();

			function validateTour() {
				$.ajax({
					url: apiUrl + "/tours/" + name + "/exists",
					type: 'GET',
					dataType: 'json',
					headers: checkAuthHeaders()
				}).done(function(response) {
					if (response.exists) {
						return uploadExistsHandler();
					}
					xhrUpload = upload();

					function upload() {
						return $.ajax(apiUrl + "/tours", options)
							.done(function (response, status, xhr) {
								if (xhr.status === 200) {
									Materialize.toast('<span>Photos were successfully uploaded to the server. ' +
										'You may find the status in the <a href="' + serverPath + '/tasks">Tasks</a> page</span>', 10000);
								}
							}).fail(function(xhr) {
								handleAuthError(xhr,
									function() { return upload(); },
									function () {
										uploadFailHanlder(xhr);
										Materialize.toast('Failed to upload.', 4000);
									});
							}).always(uploadAlwaysHandler);
					}
				}).fail(function(xhr) {
					handleAuthError(xhr,
						function() { return validateTour(); },
						function () {
							uploadFailHanlder(xhr);
							uploadAlwaysHandler();
							Materialize.toast('Failed to validate tour.', 4000);
						});
				});
			}
		}
	});
});

function onLoadStart(e) {
	$progressBar.removeClass('hide');
	$determinateBar.css('width', '0%');
	$abortButton.removeClass('hide');
}

function onProgress(e) {
	if (e.lengthComputable) {
		var percentComplete = e.loaded / e.total;
		percentComplete = parseInt(percentComplete * 100);
		$determinateBar.css('width', percentComplete + "%");
	}
}

function uploadFailHanlder(xhr) {
	var message = xhr.status === 0 ? "Upload was cancelled" : JSON.parse(xhr.responseText).message;
	Materialize.toast('Photos upload failed. ' + message, 10000)
}

function uploadAlwaysHandler() {
	$progressBar.addClass('hide');
	$uploadButton.removeClass('disabled');
	$abortButton.addClass('hide');
}

function uploadExistsHandler() {
	Materialize.toast('Photos upload failed. Tour Name is used.', 10000);
	uploadAlwaysHandler();
}
