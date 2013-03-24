<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<script src="${asset}/jquery-file-upload/js/jquery.iframe-transport.js"></script>
<script src="${asset}/jquery-file-upload/js/jquery.fileupload.js"></script>
<!--[if gte IE 8]><script src="${asset}/jquery-file-upload/js/cors/jquery.xdr-transport.js"></script><![endif]-->
<script type="text/javascript">
var fileUpload = (function(options) {
	
	function init() {
		$('#fileupload').fileupload({
			url: options.url,
			/*maxChunkSize: 4 * 1024 * 1024,
			error: function (jqXHR, textStatus, errorThrown) {
				// Called for each failed chunk upload
				log("upload error: " + textStatus);
			},
			success: function (data, textStatus, jqXHR) {
				// Called for each successful chunk upload
				log("upload success: " + data);
			},*/
			fileInput: ('#fileupload-buttonbar input:file'),
			dropZone: $('#file-grid'),
			drop: function(e, data) {
				//* exclude the case of dragging & dropping grid rows
				if (data.files.length < 1) {
					return;
				}
				options.dropEventHandler();
			},
			add: function (e, data) {
				//* for singleFileUploads, a file per request
				var file = data.files[0];
 				var uploadFile = '<div class="row-fluid file" style="position:relative">' +
	 				'<div class="span12 single-progress alert-info" style="width:0%">' +
	 				'<div class="row-fluid" style="position:absolute; padding-top:5px;">' +
	 				'<div class="span6 name">' + file.name + '</div>' +
	 				'<div class="span2 size">' + formatFileSize(file.size) + '</div>' +
	 				'<div class="span4 cancel"><button type="button" class="btn btn-warning btn-mini">Cancel</button></div>' +
	 				'</div></div></div>';
 				  
				var $uploadFile = $(uploadFile);
				$('#fileupload-files').append($uploadFile);
				
				$uploadFile.data('data', data);
				data.context = $uploadFile;
				data.submit();
			},
			submit: function(e, data) {
				var file = data.files[0];
				data.formData = {space:options.space, folder:currentFolder, fileSize:file.size};
				return true;
			},
			progress: function (e, data) {
				var progress = parseInt(data.loaded / data.total * 100, 10);
				var $uploadFile = data.context;
				if (progress === 100) {
					$uploadFile.find('.cancel button').attr('disabled', true);
				}
				$uploadFile.find('.single-progress').css('width', progress + '%');
			},
			progressall: function (e, data) {
				var uploadInfo = renderExtendedProgress(data);
				$('#fileupload-progress').html(uploadInfo);
				
				var progress = parseInt(data.loaded / data.total * 100, 10);
				if (progress === 100) {
					$('#cancel-upload-btn').attr('disabled', true);
				}
				$('#total-progress').css('width', progress + '%');
			},
			done: function (e, data) {
				var resps;
				if (!$.browser.msie) {
					resps = $.parseJSON(data.result);
				} else {
					// IE will receive a html document: <html>...<body<<pre>json string</pre></body></html>
					resps = data.result;
					var resp = resps[0];
					var $pre = $(resp.body).find('pre');
					var jsonStr = $pre.html();
					resps = $.parseJSON(jsonStr);
				}
				var resp = resps[0];
				
				var $uploadFile = data.context;
				var msg;
				if (!resp.error) {
					$uploadFile.find('.single-progress').removeClass('alert-info').addClass('alert-success').css('width', '100%'); //* fix Firefox bug
					$uploadFile.find('.size').html(formatFileSize(resp.size));
					msg = '<span class="label label-success">Success</span>';
				} else {
					$uploadFile.find('.single-progress').removeClass('alert-info').addClass('alert-error');
					msg = '<span class="label label-important">Failed: ' + resp.error + '</span>';
				}
				$uploadFile.find('.cancel').html(msg);
			},
			fail: function (e, data) {
				var $uploadFile = data.context;
				var msg;
				if (data.errorThrown === 'abort') {
					$uploadFile.find('.single-progress').removeClass('alert-info').addClass('alert-warning');
					msg = '<span class="label label-warning">Canceled</span>';
				} else {
					$uploadFile.find('.single-progress').removeClass('alert-info').addClass('alert-error');
					msg = '<span class="label label-important">Failed</span>';
				}
				$uploadFile.find('.cancel').html(msg);
			},
			start: function (e) {
				$('#fileupload-progress').show();
				var $cancelBtn = $('#cancel-upload-btn');
				$cancelBtn.attr('disabled', false);
				$cancelBtn.show();
			},
			stop: function (e) {
				var $fileuploadProgress = $('#fileupload-progress');
				$fileuploadProgress.hide();
				$fileuploadProgress.empty();
				$('#cancel-upload-btn').hide();
			}
		});
		
		setUploadFolder();
		setCancelUpload();
		setCloseUpload();
	}
	
	function setUploadFolder() {
		// enable upload folder for chrome
		if ($.browser.chrome) {
			$('#add-folder-btn').show();
		}
	}
	
	function setCancelUpload() {
		$('#fileupload-files').on('click', '.cancel button', function(e) {
			var $uploadFile = $(this).parents('.file');
			var data = $uploadFile.data('data');
			data.jqXHR.abort();
		});
		
		$('#cancel-upload-btn').click(function() {
			$('#fileupload-files').find('.cancel button').click();
		});
	}
	
	function setCloseUpload() {
		$('#close-upload-btn').click(function() {
			$('#fileupload-files').find('.file').each(function(index) {
				// don't delete records which are still uploading
				var $target = $(this).find('.cancel button');
				if ($target.length <= 0) {
					$(this).remove();
				}
			});
		});
	}
	
	function renderExtendedProgress(data) {
		return '<div class="row-fluid" style="position:relative">' + 
			'<div id="total-progress" class="span12 alert-info" style="width:0%">' +
			'<div class="row-fluid" style="position:absolute; padding-top:5px;">' +
			'<div class="span3">' + formatBitrate(data.bitrate) + '</div>' +
			'<div class="span3">' + formatTime((data.total - data.loaded) * 8 / data.bitrate) + '</div>' +
			'<div class="span2">' + formatPercentage(data.loaded / data.total) + '</div>' +
			'<div class="span4">' + formatFileSize(data.loaded) + ' / ' + formatFileSize(data.total) + '</div>' + 
			'</div></div></div>';
	}
	
	function formatFileSize(bytes) {
		if (typeof bytes !== 'number') {
			return '';
		}
		if (bytes >= 1000000000) {
			return (bytes / 1000000000).toFixed(2) + ' GB';
		}
		if (bytes >= 1000000) {
			return (bytes / 1000000).toFixed(2) + ' MB';
		}
		return (bytes / 1000).toFixed(2) + ' KB';
	}

	function formatBitrate(bits) {
		if (typeof bits !== 'number') {
			return '';
		}
		if (bits >= 1000000000) {
			return (bits / 1000000000).toFixed(2) + ' Gbps';
		}
		if (bits >= 1000000) {
			return (bits / 1000000).toFixed(2) + ' Mbps';
		}
		if (bits >= 1000) {
			return (bits / 1000).toFixed(2) + ' Kbps';
		}
		return bits + ' bps';
	}

	function formatTime(seconds) {
		var date = new Date(seconds * 1000),
			days = parseInt(seconds / 86400, 10);
		days = days ? days + 'd ' : '';
		return days +
			('0' + date.getUTCHours()).slice(-2) + ':' +
			('0' + date.getUTCMinutes()).slice(-2) + ':' +
			('0' + date.getUTCSeconds()).slice(-2);
	}

	function formatPercentage(floatValue) {
		return (floatValue * 100).toFixed(2) + ' %';
	}
	
	return {
		init: init
	};
})(uploadOptions);

function openUploadUI() {
	$('#upload-modal').modal('show');
}

$(function() {
	uploadOptions.dropEventHandler = openUploadUI;
	fileUpload.init();
});
</script>