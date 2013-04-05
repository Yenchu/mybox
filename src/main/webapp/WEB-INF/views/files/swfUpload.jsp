<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<style type="text/css">
/* to overlay swf object and html button  */
.swfupload {
	position: absolute;
	z-index: 1;
}
</style>
<script type="text/javascript" src="${asset}/swfupload/swfupload.js"></script>
<script type="text/javascript" src="${asset}/swfupload/plugins/swfupload.queue.js"></script>
<script type="text/javascript" src="${asset}/swfupload/plugins/swfupload.speed.js"></script>
<script type="text/javascript">
var fileUpload = (function(options) {
	
	var swfu;
	
	function init() {
		if (!swfu) {
			// make swf object has equal size with 'add-file-btn'
			var $addFileBtn = $('#add-file-btn');
			var btnWidth = $addFileBtn.width();
			var btnHeight = $addFileBtn.height();
			btnWidth += 20 * btnWidth/100;
			btnHeight += 10 * btnHeight/100;
			
			var settings = {
				flash_url: '${asset}/swfupload/swfupload.swf',
				flash9_url: '${asset}/swfupload/swfupload_fp9.swf',
				upload_url: options.url,
				file_types: '*.*',
				file_types_description: 'All Files',
		
				button_placeholder_id: 'upload-files',
				button_width: btnWidth,
				button_height: btnHeight,
				button_window_mode: SWFUpload.WINDOW_MODE.TRANSPARENT,
				button_cursor: SWFUpload.CURSOR.HAND,
				
				file_dialog_complete_handler: fileDialogComplete,
				upload_start_handler: uploadStart,
				upload_progress_handler: uploadProgress,
				upload_error_handler: uploadError,
				upload_success_handler: uploadSuccess,
				upload_complete_handler: uploadComplete,
		
				debug: false,
				custom_settings: {
					totalFileSize: 0,
					uploadedFileSizes: {}
				}
			};
		
			swfu = new SWFUpload(settings);
			setCancelUpload(swfu);
			setCloseUpload();
		}
	}
	
	function uploadStart(file) {
		var uploadProgress = '<div id="' + file.id + '" class="file row-fluid" style="position:relative">' + 
			'<div class="span12 single-progress alert-info" style="width:0%">' +
			'<div class="row-fluid" style="position:absolute; padding-top:5px;">' +
			'<div class="span6 name">' + file.name + '</div>' +
			'<div class="span2 size">' + formatBytes(file.size) + '</div>' +
			'<div class="span4 cancel"><button type="button" class="btn btn-warning btn-mini">Cancel</button></div>' +
			'</div></div></div>';
	
		var $uploadProgress = $(uploadProgress);
		$('#upload-progresses').append($uploadProgress);

		this.customSettings.totalFileSize += file.size;
		
		this.addFileParam(file.id, 'space', options.space);
		this.addFileParam(file.id, 'folder', currentFolder);
		this.addFileParam(file.id, 'fileSize', file.size);
	}
	
	function uploadProgress(file, bytesLoaded, bytesTotal) {
		var progress = Math.ceil((bytesLoaded / bytesTotal) * 100);
		var $uploadProgress = $('#' + file.id);
		if (progress === 100) {
			$uploadProgress.find('.cancel button').attr('disabled', true);
		}
		$uploadProgress.find('.single-progress').css('width', progress + '%');

		this.customSettings.uploadedFileSizes[file.id] = bytesLoaded;
		uploadProgressAll(file, this.customSettings);
	}
	
	function uploadProgressAll(file, customSettings) {
		var uploadedFileSizes = customSettings.uploadedFileSizes;
		var uploadedSum = 0;
		for (var key in uploadedFileSizes) {
			var value = uploadedFileSizes[key];
			uploadedSum += value;
		}

		var totalFileSize = customSettings.totalFileSize;
		var uploadInfo = renderExtendedProgress(file, uploadedSum, totalFileSize);
		var $totalUploadProgress = $('#total-upload-progress').html(uploadInfo);
		
		var progress = Math.ceil((uploadedSum / totalFileSize) * 100);
		if (progress === 100) {
			$('#cancel-upload-btn').attr('disabled', true);
		}
		$totalUploadProgress.find('.total-progress').css('width', progress + '%');
	}
	
	function uploadSuccess(file, serverData) {
		var resps = $.parseJSON(serverData);
		var resp = resps[0];
		
		var $uploadProgress = $('#' + file.id);
		var msg;
		if (!resp.error) {
			$uploadProgress.find('.single-progress').removeClass('alert-info').addClass('alert-success').css('width', '100%');
			$uploadProgress.find('.size').html(formatBytes(resp.size));
			msg = '<span class="label label-success">Success</span>';
		} else {
			$uploadProgress.find('.single-progress').removeClass('alert-info').addClass('alert-error');
			msg = '<span class="label label-important">Failed: ' + resp.error + '</span>';
		}
		$uploadProgress.find('.cancel').html(msg);
	}
	
	function uploadError(file, errorCode, message) {
		switch (errorCode) {
			case SWFUpload.UPLOAD_ERROR.FILE_CANCELLED:
				if (this.getStats().files_queued === 0) {
					$('#cancel-upload-btn').hide();
				}
			default:
				log("Upload error code=" + errorCode + ", filename=" + file.name + ", filesize=" + file.size + ", message=" + message);
		}
		
		var $uploadProgress = $('#' + file.id);
		var msg;
		if (errorCode === SWFUpload.UPLOAD_ERROR.FILE_CANCELLED) {
			$uploadProgress.find('.single-progress').removeClass('alert-info').addClass('alert-warning');
			msg = '<span class="label label-warning">Canceled</span>';
		} else {
			$uploadProgress.find('.single-progress').removeClass('alert-info').addClass('alert-error');
			msg = '<span class="label label-important">Failed</span>';
		}
		$uploadProgress.find('.cancel').html(msg);

		this.customSettings.uploadedFileSizes[file.id] = file.size;
		uploadProgressAll(file, this.customSettings);
	}
	
	function fileDialogComplete(numFilesSelected, numFilesQueued) {
		if (numFilesSelected > 0) {
			$('#total-upload-progress').show();
			var $cancelBtn = $('#cancel-upload-btn');
			$cancelBtn.attr('disabled', false);
			$cancelBtn.show();
		}
		this.startUpload();
	}
	
	function uploadComplete(file) {
		if (this.getStats().files_queued === 0) {
			$('#total-upload-progress').hide().empty();
			$('#cancel-upload-btn').hide();
			this.customSettings.totalFileSize = 0;
			this.customSettings.uploadedFileSizes = {};
		}
	}
	
	function setCancelUpload(swfu) {
		$('#upload-progresses').on('click', '.cancel button', function(e) {
			var $uploadProgress = $(this).parents('.file');
			var fileId = $uploadProgress.attr('id');
			swfu.cancelUpload(fileId, true);
		});
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
	
	function renderExtendedProgress(file, bytesLoaded, byteTotal) {
		var timeRemaining = file.timeRemaining * byteTotal / bytesLoaded;
		var percentUploaded = 100 * bytesLoaded / byteTotal; //file.percentUploaded
		return '<div class="row-fluid" style="position:relative">' +
			'<div class="span12 total-progress alert-info" style="width:0%">' + 
			'<div class="row-fluid" style="position:absolute; padding-top:5px;">' + 
			'<div class="span3">' + SWFUpload.speed.formatBPS(file.currentSpeed) + '</div>' +
			'<div class="span3">' + SWFUpload.speed.formatTime(timeRemaining) + '</div>' +
			'<div class="span2">' + SWFUpload.speed.formatPercent(percentUploaded) + '</div>' +
			'<div class="span4">' + SWFUpload.speed.formatBytes(bytesLoaded) + ' / ' + SWFUpload.speed.formatBytes(byteTotal) + '</div>' +
			'</div></div></div>';
	}
	
	function formatBytes(bytes) {
		return SWFUpload.speed.formatBytes(bytes);
	}
	
	return {
		init: init
	};
})(uploadOptions);

function openUploadUI() {
	$('#upload-modal').modal('show');
	fileUpload.init();
}
</script>