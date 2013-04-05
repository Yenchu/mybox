<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<script type="text/javascript">
var fileUpload = (function(options) {
	function handleDragOver(event) {
		event.stopPropagation();
		event.preventDefault();
	}
	function handleDrop(event) {
		event.stopPropagation();
		event.preventDefault();
		
		$('#upload-modal').modal('show');
		
		var files = event.dataTransfer.files;
		uploadFiles(files);
	 }
	function handleUpload(event) {
		var files = event.target.files;
		uploadFiles(files);
	}
	function uploadFiles(files) {
		var $container = $('#upload-progresses');
		$container.data('total', 0).data('loaded', 0);
		for (var i = 0, file; file = files[i]; i++) {
			// add upload bar
			var uploadBar = renderProgress(file.name, formatFileSize(file.size));
			var $uploadBar = $(uploadBar);
			$container.append($uploadBar);
			
			var reader = new FileReader();
			reader.onerror = (function($uploadBar) {
				return function(e) {
					var error = e.target.error;
					switch(error.code) {
						case error.ABORT_ERR:
							console.log('File is aborted!');
							$uploadBar.find('.single-progress').removeClass('alert-info').addClass('alert-warning');
							msg = '<span class="label label-warning">Canceled</span>';
							break;
						case error.NOT_FOUND_ERR:
							console.log('File Not Found!');
						case error.NOT_READABLE_ERR:
							console.log('File is not readable!');
						default:
							console.log('An error occurred when reading this file!');
							$uploadBar.find('.single-progress').removeClass('alert-info').addClass('alert-error');
							msg = '<span class="label label-important">Failed</span>';
					};
					$uploadBar.find('.cancel').html(msg);
				};
			})($uploadBar);
			reader.onabort = (function(file) {
				return function(e) {
					console.log(file.name + ' upload cancelled');
				};
			})(file);
			reader.onloadstart = (function($container) {
				return function(e) {
					// calculate total file size
					var total = $container.data('total') || 0;
					total += e.total;
					$container.data('total', total);
				};
			})($container);
			reader.onprogress = (function($uploadBar, $container) {
				return function(e) {
					if (e.lengthComputable) {
						var progress = Math.round((e.loaded / e.total) * 100);
						$uploadBar.find('.single-progress').css('width', progress + '%');
						if (progress >= 100) {
							$uploadBar.find('.cancel button').attr('disabled', true);
						}
						
						// create total upload bar everytime!?
						var total = $container.data('total');
						var loaded = ($container.data('loaded') || 0) + e.loaded;
						var bitrate = 10;
						var uploadInfo = renderTotalProgress(formatBitrate(bitrate)
								, formatTime((total - loaded) * 8 / bitrate)
								, formatPercentage(loaded / total)
								, formatFileSize(loaded) + ' / ' + formatFileSize(total));
						var $totalUploadProgress = $('#total-upload-progress').html(uploadInfo);

						var totalProgress = Math.round((loaded / total) * 100);
						$totalUploadProgress.find('.total-progress').css('width', totalProgress + '%');
						if (totalProgress >= 100) {
							$('#cancel-upload-btn').attr('disabled', true);
						}
					}
				};
			})($uploadBar, $container);
			reader.onload = (function(file, $uploadBar, $container) {
				return function(e) {
					// calculate total uploaded file size
					var loaded = $container.data('loaded') || 0;
					loaded += e.total;
					$container.data('loaded', loaded);
					// start upload
					upload(file);
				};
			})(file, $uploadBar, $container);
			reader.onloadend = (function(file, $uploadBar, $container) {
				return function(e) {
					if (e.target.readyState == FileReader.DONE) {
						var msg, resp = e.target.result;
						if (!resp.error) {
							$uploadBar.find('.single-progress').removeClass('alert-info').addClass('alert-success').css('width', '100%');
							$uploadBar.find('.size').html(formatFileSize(resp.size));
							msg = '<span class="label label-success">Success</span>';
						} else {
							$uploadBar.find('.single-progress').removeClass('alert-info').addClass('alert-error');
							msg = '<span class="label label-important">Failed: ' + resp.error + '</span>';
						}
						$uploadBar.find('.cancel').html(msg);
					}
					
					var total = $container.data('total');
					var loaded = $container.data('loaded');
					var totalProgress = Math.round((loaded / total) * 100);
					if (totalProgress >= 100) {
						$('#cancel-upload-btn').attr('disabled', true).hide();
						$('#total-upload-progress').find('.total-progress').css('width', '100%');
						//$('#total-upload-progress').empty();//.hide()
					} else {
						$('#total-upload-progress').find('.total-progress').css('width', totalProgress + '%');
					}
				};
			})(file, $uploadBar, $container);
			
			// enable cancel upload
			$uploadBar.on('click', '.cancel button', {reader:reader}, function(e) {
				e.data.reader.abort();
			});
			
			// start upload
			reader.readAsBinaryString(file);
		}

		$('#total-upload-progress').empty().show();
		var $cancelBtn = $('#cancel-upload-btn');
		$cancelBtn.attr('disabled', false);
		$cancelBtn.show();
	}
	function upload(file)  {
		var formData = new FormData();
		var fileName = escape(file.name);
		formData.append('space', options.space);
		formData.append('folder', currentFolder);
		formData.append('fileSize', file.size);
		formData.append(fileName, file);
		
		$.ajax({
			url: '/ap/files',//,//options.url
			data: formData,
			type: 'POST',
			cache: false,
			contentType: false,
			processData: false,
			success: function(data){
				console.log(data);
			}
		});
	}
	
	function init() {
		$.event.props.push('dataTransfer');
		$('#file-table').on('dragover', handleDragOver).on('drop', handleDrop);
		$('#upload-files').on('change', handleUpload);
		
		setUploadFolder();
		setCancelUpload();
		setCloseUpload();
	}
	
	function setUploadFolder() {
		// enable upload folder for chrome
		if ($.browser.chrome) {
			$('#add-folder-btn').show().on('change', handleUpload);
		}
	}
	
	function setCancelUpload($uploadBar, reader) {
		$('#cancel-upload-btn').click(function() {
			$('#upload-progresses').find('.cancel button').click();
		});
	}
	
	function setCloseUpload() {
		$('#close-upload-btn').click(function() {
			$('#upload-progresses').find('.file').each(function(index) {
				// don't delete records which are still uploading
				var $target = $(this).find('.cancel button');
				if ($target.length <= 0) {
					$(this).remove();
				}
			});
		});
	}
	
	function renderProgress(fileName, fileSize) {
		return '<div class="row-fluid file" style="position:relative">' +
			'<div class="span12 single-progress alert-info" style="width:0%">' +
			'<div class="row-fluid" style="position:absolute; padding-top:5px;">' +
			'<div class="span6 name">' + fileName + '</div>' +
			'<div class="span2 size">' + fileSize + '</div>' +
			'<div class="span4 cancel"><button type="button" class="btn btn-warning btn-mini">Cancel</button></div>' +
			'</div></div></div>';
	}
	
	function renderTotalProgress(bitrate, timeLeft, loadedPercent, sizeInfo) {
		return '<div class="row-fluid" style="position:relative">' + 
			'<div class="span12 total-progress alert-info" style="width:0%">' +
			'<div class="row-fluid" style="position:absolute; padding-top:5px;">' +
			'<div class="span3">' + bitrate + '</div>' +
			'<div class="span3">' + timeLeft + '</div>' +
			'<div class="span2">' + loadedPercent + '</div>' +
			'<div class="span4">' + sizeInfo + '</div>' + 
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

function openUploadUI(e) {
	$('#upload-modal').modal('show');
}

$(function() {
	fileUpload.init();
});
</script>