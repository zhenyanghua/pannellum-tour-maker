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
			$uploadButton.addClass('disabled');
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
				processData: false,
				xhr: function() {
					var xhr = new XMLHttpRequest();
					xhr.upload.onloadstart = onLoadStart;
					xhr.upload.onprogress = onProgress;
					return xhr;
				},
				headers: checkAuthHeaders()
			};
			xhrUpload = upload()

			function upload() {
				return $.ajax(servletPath + apiUrl + "/tours/exist", options)
					.done(function (response, status, xhr) {
						if (xhr.status === 200) {
							Materialize.toast('<span>Photos were successfully uploaded to the server. ' +
								'You may find the status in the <a href="' + serverPath + '/tasks">Tasks</a> page</span>', 10000);
						}
					})
					.fail(function(xhr) {
						if (xhr.status === 0) {
							Materialize.toast('Upload was cancelled', 10000);
						} else {
							handleAuthError(xhr,
								function() { return upload(); },
								function() { Materialize.toast('Failed to upload.', 4000); });
						}
					})
					.fail(function (xhr) {
						var message = xhr.status === 0 ? "Upload was cancelled" : JSON.parse(xhr.responseText).message;
						Materialize.toast('Photos upload failed. ' + message, 10000);
						if (xhr.status === 401) {
							redirectToLogin();
						}
					})
					.always(function () {
						$progressBar.addClass('hide');
						$uploadButton.removeClass('disabled');
						$abortButton.addClass('hide');
					});
			}
		}
	});
});

function getTourNames() {
	$.ajax({
		url: apiUrl + "/tours/names",
		type: 'GET',
		dataType: 'json',
		headers: checkAuthHeaders()
	}).done(function (names) {
		names.forEach(function (name) {
			$selectTourName.append($("<option>").val(name).text(name));
		});
		$selectTourName.material_select();
	}).fail(function(xhr) {
		handleAuthError(xhr, function() { return getTourNames(); });
	});
}

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


