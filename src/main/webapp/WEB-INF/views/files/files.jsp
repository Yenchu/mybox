<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class="row-fluid">
	<div class="span7">
		<ul id="file-paths" class="breadcrumb" style="background-color:transparent"></ul>
	</div>
	<div class="span5">
		<div class="pull-right">
			<span id="file-oper-btns">
				<button type="button" id="upload-file-btn" class="btn btn-info" data-toggle="tooltip" title="Upload File"><i class="icon-upload-alt	icon-white"></i></button>
				<button type="button" id="new-folder-btn" class="btn btn-info" data-toggle="tooltip" title="New Folder"><i class="icon-folder-close-alt icon-white"></i></button>
			</span>
			<span id="search-oper-btns" class="input-append" style="margin-bottom:0;">
				<input type="text" id="search-txt" name="query" class="input-small" placeholder="Search">
				<span id="search-btn" class="add-on" style="cursor:pointer"><i class="icon-search"></i></span>
			</span>
		</div>
	</div>
</div>
<div class="row-fluid">
<div class="span12">
<div id="table-container">
	<table id="file-table" class="table table-bordered"></table>
</div>
<div id="file-context-menu">
	<ul class="dropdown-menu" role="menu">
		<li id="act-download"><a href="#"><i class="icon-download-alt"></i> Download</a></li>
		<li id="act-share"><a href="#"><i class="icon-share"></i> Share</a></li>
		<li id="act-link"><a href="#"><i class="icon-link"></i> Link</a></li>
		<li id="act-rename"><a href="#"><i class="icon-pencil"></i> Rename</a></li>
		<li id="act-delete"><a href="#"><i class="icon-trash"></i> Delete</a></li>
		<li id="act-move"><a href="#"><i class="icon-cut"></i> Move</a></li>
		<li id="act-copy"><a href="#"><i class="icon-copy"></i> Copy</a></li>
		<li id="act-revision"><a href="#"><i class="icon-time"></i> Revisions</a></li>
	</ul>
</div>
<div id="upload-modal" class="modal hide fade">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h3>Upload File</h3>
	</div>
	<div class="modal-body">
		<div id="upload-placeholder">
			<div id="upload-progresses"></div><br>
			<div id="total-upload-progress" class="hide"></div>
		</div>
	</div>
	<div class="modal-footer">
		<div class="pull-right">
			<!-- The fileinput-button span is used to style the file input field as button -->
			<span id="add-file-btn" class="btn btn-info btn-small fileinput-button">
				<input type="file" id="upload-files" name="files[]" multiple>
				<i class="icon-plus icon-white"></i> Upload File
			</span>
			<span id="add-folder-btn" class="btn btn-info btn-small fileinput-button hide">
				<input type="file" id="upload-folders" name="folders[]" multiple directory webkitdirectory mozdirectory>
				<i class="icon-plus icon-white"></i> Upload Folder
			</span>
			<button id="cancel-upload-btn" type="reset" class="btn btn-warning btn-small hide">
				<i class="icon-ban-circle icon-white"></i> Cancel Upload
			</button>
			<button id="close-upload-btn" type="button" class="btn btn-inverse btn-small" data-dismiss="modal" aria-hidden="true">
				<i class="icon-remove icon-white"></i> Close
			</button>
		</div>
	</div>
</div>
<div id="file-modal" class="modal hide fade">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h3></h3>
	</div>
	<div class="modal-body">
	</div>
	<div class="modal-footer">
		<div class="pull-right">
		<button type="button" class="btn btn-primary confirm">Submit</button>
		<button type="button" class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button>
		</div>
	</div>
</div>
<div id="user-modal" class="modal hide fade">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h3></h3>
	</div>
	<div class="modal-body">
	</div>
	<div class="modal-footer">
		<div class="pull-right">
		<button type="button" class="btn btn-primary confirm">Submit</button>
		<button type="button" class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button>
		</div>
	</div>
</div>
</div>
</div>
<script id="share-template" type="text/template">
<div>
<table id="users-table" class="table table-bordered table-striped table-condensed"></table>
<button type="button" id="read-only-btn" class="btn btn-small"><i class="icon-pencil"></i> Read Only</button>
<button type="button" id="read-write-btn" class="btn btn-small"><i class="icon-pencil"></i> Read/Write</button>
</div>
<hr/>
<div>
<table id="shares-table" class="table table-bordered table-striped table-condensed"></table>
</div>
</script>
<script type="text/javascript">
var uploadJsOptions = null;

function getUploadJs() {
	var useFlash = false;
	var detectFileSize = false;
	if (window.File && window.FileReader && window.FileList && window.Blob) {
		detectFileSize = true;
	} else {
		if (swfobject.hasFlashPlayerVersion("9.0.0")) {
			useFlash = true;
			detectFileSize = true;
		}
	}
	
	// add extra info to upload url to tell server how ot handle upload
	uploadJsOptions.url = '${service}/files?detectFileSize=' + detectFileSize;
	
	$.ajax({
		url: '${service}/uploadjs',
		data: {useFlash:useFlash}
	}).done(function(data) {
		$('body').append(data);
	}).fail(ajaxRequestFail);
}

$(function() {
	// to prevent the selection after the Shift + click in IE
	document.onselectstart = function() {
		return false;
	};
	
	// to disable the default browser action for file drops on the document
	$(document).bind('drop dragover', function(event) {
		event.preventDefault();
	});
	
	var spaceId = '${space.id}', spaceName = '${space.name}', rootFolder = '${space.root}', currentFolder = '${currentFolder}';
	var fileTableOptions = {spaceId:spaceId, rootPath:rootFolder, currentFolder: currentFolder};
	var fileTable = FileTable(fileTableOptions);
	
	var fileBreadcrumbOptions = {fileTable:fileTable, rootPath:rootFolder, rootName:spaceName, initPath:currentFolder};
	var fileBreadcrumb = FileBreadcrumb(fileBreadcrumbOptions);
	fileTable.setLoadFolderListener(fileBreadcrumb.update);
	
	// setup upload view
	$('#upload-modal').modal('hide').on('hidden', function() {
		fileTable.loadFiles();
	});
	$('#file-modal').modal('hide').on('hidden', function() {
		$(this).find('.modal-body').empty();
		$(this).off('click', '.confirm');
	});
	
	$('[data-toggle=tooltip]').tooltip({placement:'top'});
	
	// setup menu buttons
	$('#upload-file-btn').click(function() {
		openUploadUI();
	});
	$('#new-folder-btn').click(function() {
		fileTable.newFolder();
	});
	$('#search-txt').keyup(function(event) {
		event.which === 13 && fileTable.search($(this));
	});
	$('#search-btn').click(function() {
		fileTable.search($('#search-txt'));
	});
	
	// ajax get file-upload-related js
	uploadJsOptions = {getCurrentFolder:fileTable.getCurrentFolder, spaceId:spaceId};
	getUploadJs(uploadJsOptions);
});
</script>