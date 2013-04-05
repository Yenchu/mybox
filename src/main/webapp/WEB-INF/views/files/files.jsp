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
		<li id="act-link"><a href="#"><i class="icon-share"></i> Share link</a></li>
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
</div>
</div>
<!--[if IE]>
<script>
Placeholders.init();
</script>
<![endif]-->
<script type="text/javascript">
var spaceId = '${space.id}', spaceName = '${space.name}', rootFolder = '${space.root}', currentFolder = '${currentFolder}';

var fileTableOptions = {loadFolderHandler:null};

var fileBreadcrumbOptions = {initPath:currentFolder, rootPath:rootFolder, rootName:spaceName, clickEventHandler:null};

var uploadOptions = {url:'${service}/files', space:spaceId};

var isLoadingFiles = false;

var fileGrid = (function(fileTableOptions) {
	
	var FileType = {FOLDER:'folder', FILE:'file'};

	var $grid = null, options = null, isSearching = false, isDragging = false, isLoadingSubFolder = false, isLoadingSubFolderInSearch = false;

	function createFileTable() {
		if (isSearching) {
			$grid.mytable('destroy');
			isSearching = false;
			$('#search-oper-btns').find('#disable-search-btn').remove();
			$('#file-oper-btns').show();
		}
		
		options = {
			colModels: [
				{name:'id', header:'ID', hidden:true},
				{name:'path', header:'Path', hidden:true},
				{name:'mimeType', header:'MimeType', hidden:true},
				{name:'thumbExists', header:'ThumbExists', hidden:true},
				{name:'name', header:'Name', width:'30%', sortable:true, formatter:formatFileNameColumn, editable:true},
				{name:'isDir', header:'Type', width:'20%', sortable:true, formatter:formatFileTypeColumn},
				{name:'size', header:'Size', width:'20%', sortable:true, formatter:formatSizeColumn},
				{name:'modified', header:'Modified', width:'30%', sortable:true}
			],
			inlineEditing: true,
			isMultiSelect: true,
			loadOnce: true,
			remote: {url:'${service}/metadata', editUrl:'${service}/fileops/edit', deleteUrl:'${service}/fileops/edit'
				, params:{space:spaceId, folder:currentFolder}, method:'POST'}
		};
		$grid = createTable(options, dblClickRow);
	}
	
	function createSearchTable(query) {
		if (isSearching) {
			options.remote.params = {space:spaceId, folder:currentFolder, query:query};
			$grid.mytable('setOptions', options);
			reload();
			return;
		}
		
		$grid.mytable('destroy');
		isSearching = true;
		$('#file-oper-btns').hide();
		var $disableSearchBtn = $('<span id="disable-search-btn" class="add-on" style="cursor:pointer"><i class="icon-remove"></i></span>');
		$('#search-oper-btns').append($disableSearchBtn);
		$('#disable-search-btn').off('click').on('click', function() {
			createFileTable();
		});
		
		options = {
			colModels: [
				{name:'id', header:'ID', hidden:true},
				{name:'path', header:'Path', hidden:true},
				{name:'mimeType', header:'MimeType', hidden:true},
				{name:'thumbExists', header:'ThumbExists', hidden:true},
				{name:'name', header:'Name', width:'30%', sortable:true, formatter:formatFileNameColumn, editable:true},
				{name:'isDir', header:'Type', width:'20%', sortable:true, hidden:true, formatter:formatFileTypeColumn},
				{name:'location', header:'Location', width:'20%', sortable:true},
				{name:'modified', header:'Modified', width:'30%', sortable:true}
			],
			inlineEditing: true,
			isMultiSelect: true,
			loadOnce: true,
			remote: {url:'${service}/search', editUrl:'${service}/fileops/edit', deleteUrl:'${service}/fileops/edit'
				, params:{space:spaceId, folder:currentFolder, query:query}, method:'POST'}
		};
		$grid = createTable(options, dblClickRowInSearch);
	}
	
	function createTable(options, dbClickRowHandler) {
		options['loadingBarTemplate'] = '<div class="loading-bar"><i class="icon-spinner icon-spin icon-2x"></i></div>';
		return $('#file-table').off('loaded').on('loaded', function(e) {
			postload();
			enableDragAndDrop();
			enableContextMenu();
		}).mytable(options).off('dblclickRow').on('dblclickRow', function(e) {
			var rowId = e.rowId;
			dbClickRowHandler(rowId);
		}).off('add update').on('add update', function(e) {
			var newFileName = e.form.find('[name="name"]').val();
			if (!validateFileName(newFileName)) {
				e.preventDefault();
			}
		}).off('added updated').on('added updated', function(e) {
			var rowId = e.rowId, rowData = $grid.mytable('getRowData', rowId), newFileName = rowData.name;
			var msg = (rowId === '0' ? 'Creating folder ' + newFileName + ' success.' : 'Renaming to ' + newFileName + ' success.');
			notify(msg, NotifyType.SUCCESS);
		});
	}
	
	function formatFileNameColumn(colValue, rowData) {
		var content = getFileNameWithIcon(colValue, rowData);
		if (!isFolder(rowData)) {
			content = '<div>' + content + '<a href="#' + rowData.rev + '_comments" data-toggle="collapse" class="pull-right"><i class="icon-comment-alt icon-white"></i></a></div>'
			+ '<div id="' + rowData.rev + '_comments" class="collapse">123</div>';
		}
		return content;
	}
	
	function getFileNameWithIcon(fileName, rowData) {
		//* use dynatree css ti display icons
		var rt = null;
		if (isFolder(rowData)) {
			rt = '<div class="dynatree-ico-cf"><span class="dynatree-icon"></span> ' + fileName + '</div>';
		} else {
			var thumbExists = rowData.thumbExists;
			if (thumbExists === true) {
				rt = '<img src="${service}/thumbnails/' + spaceId + rowData.path + '" width="16" height="16"/> ' + fileName;
			} else {
				rt = '<span class="dynatree-icon"></span> ' + fileName;
			}
		}
		return rt;
	}
	
	function formatFileTypeColumn(colValue, rowData) {
		var rt = null;
		if (isFolder(rowData)) {
			rt = FileType.FOLDER;
		} else {
			var mimeType = rowData.mimeType;
			if (mimeType) {
				var arr = mimeType.split('/');
				if (arr.length > 1) {
					var type = arr[0];
					if (type === 'application') {
						type = 'document';
					}
					var subType = arr[1];
					rt = type + ' ' + subType;
				} else {
					rt = mimeType;
				}
			} else {
				rt = FileType.FILE;
			}
		}
		return rt;
	}
	
	function formatSizeColumn(colValue, rowData) {
		if (isFolder(rowData)) {
			return '';
		}
		if (colValue) {
			return colValue;
		} else {
			return '';
		}
	}
	
	function getSelectedRowId() {
		return $grid.mytable('getSelectedRowId');
	}
	
	function getSelectedRowIds() {
		return $grid.mytable('getSelectedRowIds');
	}
	
	function getSelectedRowData() {
		return $grid.mytable('getSelectedRowData');
	}
	
	function getRowData(rowId) {
		return $grid.mytable('getRowData', rowId);
	}

	function dblClickRow(rowId) {
		var rowData = getRowData(rowId);
		if (isFolder(rowData)) {
			isLoadingSubFolder = true;
			var folderPath = rowData.path;
			loadFolder(folderPath);
		} else {
			downloadFile(rowData);
		}
	}
	
	function dblClickRowInSearch(rowId) {
		var rowData = getRowData(rowId);
		if (isFolder(rowData)) {
			isLoadingSubFolderInSearch = true;
			currentFolder = rowData.path;
			createFileTable();
		} else {
			downloadFile(rowData);
		}
	}
	
	function postload() {
		if (isLoadingSubFolder || isLoadingSubFolderInSearch) {
			if (fileTableOptions.loadFolderHandler) {
				var reset = (isLoadingSubFolderInSearch ? true : false);
				fileTableOptions.loadFolderHandler(currentFolder, reset);
			}
			isLoadingSubFolder = false;
			isLoadingSubFolderInSearch = false;
		}
		isLoadingFiles = false;
	}
	
	function reload() {
		$grid.mytable('loadRemoteData');
		isLoadingFiles = true;
	}
	
	function loadFiles() {
		options.remote.params = {space:spaceId, folder:currentFolder};
		$grid.mytable('setOptions', options);
		reload();
	}

	function loadFolder(folderId) {
		if (folderId) {
			currentFolder = folderId;
			if (isSearching) {
				createFileTable();
				return;
			}
		}
		loadFiles();
	}
	
	function isFolder(rowData) {
		var isDir = rowData.isDir;
		if (isDir == true) {
			return true;
		} else {
			return false;
		}
	}

	function downloadFile(rowData) {
		!rowData && (rowData = getSelectedRowData());

		if (isFolder(rowData)) {
			notify('Download folder is not allowed!', NotifyType.ERROR);
			return;
		}
		
		var url = '${service}/files';
		location.href = url + '/' + spaceId + rowData.path;
	}

	function newFolder() {
		$grid.mytable('addRow', {id:'0', name:'new folder', isDir:true});
	}

	function renameFile() {
		var rowId = getSelectedRowId();
		$grid.mytable('updateRow', rowId);
	}

	function validateFileName(fileName) {
		if (fileName === '') {
			notify('File name can not be empty!', NotifyType.ERROR);
			return false;
		}
		
		var pattern = /[\\\/:*?\"<>|]/;       // invalid chars: \/:*?"<>|
		var result = pattern.test(fileName);
		if (result) {
			notify('\/:*?"<>| is invalid!', NotifyType.ERROR);
			return false;
		}
		return true;
	}

	function moveFiles() {
		var srcFiles = getSelectedRowIds();
		mvOrCpFiles(srcFiles, false);
	}

	function copyFiles() {
		var srcFiles = getSelectedRowIds();
		mvOrCpFiles(srcFiles, true);
	}

	function mvOrCpFiles(srcFiles, isCopying) {
		var $modal = $('#file-modal');
		var title = isCopying ? 'Copy File(s) to' : 'Move File(s) to';
		$modal.find('.modal-header h3').html(title);
		
		var body = '<div id="dir-tree"></div>';
		$modal.find('.modal-body').html(body);
		$('#dir-tree').dynatree({
			debugLevel: 0,
			initAjax: {
				url: '${service}/roottree',
				data: {
					'space': spaceId,
					'folder': rootFolder
				}
			},
			keyboard: false,
			minExpandLevel: 2,
			clickFolderMode: 1, // 1:activate, 2:expand, 3:activate and expand
			onLazyRead: function(node) {
				node.appendAjax({
					url: '${service}/dirtree',
					data: {
						'space': spaceId,
						'folder': node.data.id
					}
				});
			},
			onDblClick: function(node) {
				var destFolder = node.data.id;
				isCopying ? doCopyFiles(srcFiles, destFolder) : doMoveFiles(srcFiles, destFolder);
			}
		});
		
		$modal.attr('disabled', false).off('click', '.confirm').on('click', '.confirm', function() {
			var node = $('#dir-tree').dynatree('getActiveNode');
			if (node) {
				$(this).attr('disabled', true);
				var destFolder = node.data.id;
				isCopying ? doCopyFiles(srcFiles, destFolder) : doMoveFiles(srcFiles, destFolder);
			}
		});
		$modal.modal('show');
	}

	function doMoveFiles(srcFiles, destFolder) {
		operateFile('Moving', '${service}/fileops/move', {space:spaceId, srcFiles:srcFiles, destFolder:destFolder});
	}

	function doCopyFiles(srcFiles, destFolder) {
		operateFile('Copying', '${service}/fileops/copy', {space:spaceId, srcFiles:srcFiles, destFolder:destFolder});
	}
	
	function deleteFiles() {
		var $modal = $('#file-modal');
		var title = 'Delete File(s)';
		$modal.find('.modal-header h3').html(title);
		
		var body = '<div id="delete-files"></div>';
		$modal.find('.modal-body').html(body);
		var selFiles = getSelectedRowIds();
		var toDelFiles = displayToBeDeletedFiles(selFiles);
		$('#delete-files').html(toDelFiles);

		$modal.attr('disabled', false).off('click', '.confirm').on('click', '.confirm', function() {
			$(this).attr('disabled', true);
			operateFile('Deleting', '${service}/fileops/delete', {space:spaceId, files:selFiles});
		});
		$modal.modal('show');
	}
	
	function displayToBeDeletedFiles(rowIds) {
		var content = '<table class="table table-bordered">';
		for (var i = 0, len = rowIds.length; i < len; i++) {
			var rowId = rowIds[i];
			var rowData = $grid.mytable('getRowData', rowId);
			var name = rowData.name;
			content+= '<tr><td>' + getFileNameWithIcon(name, rowData) + '</td></tr>';
		}
		content += '</table>';
		return content;
	}
	
	function getRevisions() {
		var rowData = getSelectedRowData();
		var filePath = rowData.path;
		var fileName = rowData.name;
		$.ajax({
			url: '${service}/revisions/' + spaceId + filePath
		}).done(function(resp) {
			displayRevisions(fileName, resp);
		}).fail(function() {
			notify('Getting revisions failed.', NotifyType.ERROR);
		});
	}
	
	function displayRevisions(fileName, respPage) {
		var $modal = $('#file-modal');
		var title = 'Version history of "' + fileName + '"';
		$modal.find('.modal-header h3').html(title);
		
		var body = '<form id="restore-form"></form>';
		$modal.find('.modal-body').html(body);
		$('#restore-form').html(respPage);

		$modal.attr('disabled', false).off('click', '.confirm').on('click', '.confirm', function() {
			$(this).attr('disabled', true);
			restoreFile();
		});
		$modal.modal('show');
	}

	function operateFile(opeName, url, data) {
		$.ajax({
			url: url,
			data: data,
			type: 'POST'
		}).always(function() {
			$('#file-modal').modal('hide');
			loadFiles();
		}).done(function(resps) {
			notifyFileOperationResult(opeName, resps);
		}).fail(function() {
			notify(opeName + ' operation failed.', NotifyType.ERROR);
		});
	}
	
	function restoreFile() {
		$.ajax({
			url: '${service}/restore',
			data: $('#restore-form').serialize(),
			type: 'POST'
		}).always(function() {
			$('#file-modal').modal('hide');
			loadFiles();
		}).done(function(resp) {
			notify('File ' + resp.name + ' restored.', NotifyType.SUCCESS);
		}).fail(function() {
			notify('Restoring file failed.', NotifyType.ERROR);
		});
	}
	
	function shareLink() {
		var rowData = getSelectedRowData();
		$.ajax({
			url: '${service}/link',
			data: {space:spaceId, file:rowData.path},
			type: 'POST'
		}).done(function(resp) {
			location.href = resp.url;
		}).fail(function() {
			notify('Sharing link failed.', NotifyType.ERROR);
		});
	}
	
	function search($searchTxt) {
		var query = $searchTxt.val();
		if (query === '') {
			return;
		}
		
		createSearchTable(query);
		$searchTxt.val('');
	}
	
	function notifyFileOperationResult(action, resps) {
		var successFiles = '', failFiles = '';
		for (var i = 0, len = resps.length; i < len; i++) {
			var resp = resps[i];
			if (resp.error) {
				if (failFiles != '') {
					failFiles += ', ';
				}
				failFiles += resp.name;
			} else {
				if (successFiles != '') {
					successFiles += ', ';
				}
				successFiles += resp.name;
			}
		}
		if (successFiles != '') {
			notify(action + ' ' + successFiles + ' success.', NotifyType.SUCCESS);
		}
		if (failFiles != '') {
			var positionOrder = (successFiles != '' ? 1 : 0);
			notify(action + ' ' + failFiles + ' failed.', NotifyType.ERROR, positionOrder);
		}
	}
	
	function enableDragAndDrop() {
		var selRowData = [];
		var $draggableRows = $grid.find('tbody tr td:first-child');
		$draggableRows.draggable({
			delay: 200,  // this will prevent the dragging action when user only wants to click and select items
			cursor: 'move',
			opacity: 0.75,
			scope: 'files',
			appendTo: '#table-container', // is needed for IE
			revert: 'invalid',
			helper: function() {
				selRowData = $grid.mytable('getMultiSelectedRowData') || [];
				var content = [];
				if (selRowData.length > 0) {
					for (var i = 0, len = selRowData.length; i < len; i++) {
						var rowData = selRowData[i];
						var name = rowData.name;
						content[i] = getFileNameWithIcon(name, rowData);
					}
				} else {
					var id = $(this).parent('tr').attr('id');
					var rowData = $grid.mytable('getRowData', id);
					selRowData.push(rowData);
					var name = rowData.name;
					content[0] = getFileNameWithIcon(name, rowData);
				}
				
				//* separating selected rows by <br/> may block the last row to be dropped.
				var container = $('<div class="box"></div>');
				container.html(content.join(''));
				container.append('<div id="dest-info" class="hide"><i class="icon-arrow-right"></i> move to <span id="dest-folder" class="label label-info"></span></div>');
				return container;
			},
			start: function(event, ui) {
				//* just for closing context menu if it is showing
				$('html').trigger('click');
				//* just for disabling context menu
				isDragging = true;
				// clear selecting highlight
				$grid.mytable('clearSelectedRow');
			},
			stop: function() {
				isDragging = false;
				selRowData = [];
			}
		});
		
		$grid.find('tbody tr').droppable({
			accept: $draggableRows,
			scope: 'files',
			tolerance: 'pointer',
			addClasses: false,
			over: function(event, ui) {
				var dropRowId = $(event.target).attr('id');
				var rowData = $grid.mytable('getRowData', dropRowId);
				if (!isFolder(rowData)) {
					$('#dest-info').hide();
					$('#dest-folder').empty();
					return;
				}
				
				for (var i = 0, len = selRowData.length; i < len; i++) {
					var dragRowId = selRowData[i].id;
					if (dragRowId == dropRowId) {
						$('#dest-info').hide();
						$('#dest-folder').empty();
						return;
					}
				}
				
				var folderName = rowData.name;
				$('#dest-info').show();
				$('#dest-folder').html(folderName);
			},
			out: function(event, ui) {
				if ($grid.mytable('isBlur', event)) {
					$('#dest-info').hide();
					$('#dest-folder').empty();
				}
				console.log($grid.mytable('isBlur', event.originalEvent) + ': ' + event.originalEvent.pageX + ', ' + event.originalEvent.pageY);
			},
			drop: function(event, ui) {
				var dropRowId = $(event.target).attr('id');
				var rowData = $grid.mytable('getRowData', dropRowId);
				if (!isFolder(rowData)) {
					return;
				}
				
				var dragRowIds = [];
				for (var i = 0, len = selRowData.length; i < len; i++) {
					var dragRowId = selRowData[i].id;
					dragRowIds[i] = dragRowId;
					if (dragRowId == dropRowId) {
						return;
					}
				}
				doMoveFiles(dragRowIds, dropRowId);
			}
		});
	}
	
	function enableContextMenu() {
		$grid.find('tbody tr').each(function() {
			$(this).attr('data-toggle', 'context').attr('data-target', '#file-context-menu');
			$(this).contextmenu().on('contextmenu', function(e) {
				var rowId = $(this).attr('id');
				var selIds = $grid.mytable('getSelectedRowIds');
				if (selIds.length > 1) {
					for (var i = 0, len = selIds.length; i < len; i++) {
						if (rowId === selIds[i]) {
							configContextMenu4MultiSelect(selIds);
							return;
						}
					}
				}
				$grid.mytable('clearSelectedRow').mytable('selectRow', $grid.mytable('getRow', rowId));
				configContextMenu(rowId);
			});
		});
	}

	function configContextMenu(rowId) {
		var rowData = getSelectedRowData();
		if (isFolder(rowData)) {
			$('#act-download').hide();
			$('#act-link').show();
			$('#act-rename').show();
			$('#act-revision').hide();
		} else {
			$('#act-download').show();
			$('#act-link').show();
			$('#act-rename').show();
			$('#act-revision').show();
		}
	}

	function configContextMenu4MultiSelect(rowIds) {
		$('#act-download').hide();
		$('#act-link').hide();
		$('#act-rename').hide();
		$('#act-revision').hide();
	}

	function initContextMenuBtns() {
		$('#act-download').click(function() {
			downloadFile();
		});
		$('#act-link').click(function() {
			shareLink();
		});
		$('#act-rename').click(function() {
			renameFile();
		});
		$('#act-delete').click(function() {
			deleteFiles();
		});
		$('#act-move').click(function() {
			moveFiles();
		});
		$('#act-copy').click(function() {
			copyFiles();
		});
		$('#act-revision').click(function() {
			getRevisions();
		});
	}
	
	//* in dev
	function getComments() {
		var rowData = getSelectedRowData();
		$.ajax({
			url: '${service}/link',
			data: {space:spaceId, file:rowData.path},
			type: 'POST'
		}).done(function(resp) {
			location.href = resp.url;
		}).fail(function() {
			notify('Sharing link failed.', NotifyType.ERROR);
		});
	}
	function showComments() {
		var content = '<table class="table table-bordered">';
		for (var i = 0, len = rowIds.length; i < len; i++) {
			var rowId = rowIds[i];
			var rowData = $grid.mytable('getRowData', rowId);
			var name = rowData.name;
			content+= '<tr><td>' + getFileNameWithIcon(name, rowData) + '</td></tr>';
		}
		content += '</table>';
		return content;
	}
	
	return {
		init: function() {
			createFileTable();
			initContextMenuBtns();
		},
		loadFiles: loadFiles,
		loadFolder: loadFolder,
		newFolder: newFolder,
		search: search
	};
})(fileTableOptions);

var fileBreadcrumb = (function(options) {

	var $filePaths = $('#file-paths');
	
	var rootPath = options.rootPath, rootName = options.rootName;
	
	function init() {
		// if the last char is '/', remove it
		var path = options.initPath;
		var idx = path.lastIndexOf('/');
		if ((idx === path.length - 1) && (path.length > 1)) {
			path = path.substring(0, idx);
		}
		create(path, true);
		
		$filePaths.on('click', 'li', function(event) {
			// ignore when file grid is loading 
			if (isLoadingFiles) {
				return;
			}
			
			// ignore clicking the active one
			if ($(this).hasClass('active')) {
				return;
			}

			if (options.clickEventHandler) {
				var id = $(this).data('id');
				options.clickEventHandler(id);
			}
			
			setActive($(this));
		});
	}
	
	function create(path, isLastOne) {
		if (path === rootPath) {
			if (isLastOne) {
				addActive(rootPath, rootName);
			} else {
				addInActive(rootPath, rootName);
			}
			return;
		} else {
			var name = getNameFromPath(path);
			if (isLastOne) {
				addActive(path, name);
			} else {
				addInActive(path, name);
			}
		}
		
		var idx = path.lastIndexOf('/');
		if (idx > 0) {
			var parentPath = path.substring(0, idx);
			create(parentPath, false);
		} else if (idx === 0) {
			addInActive(rootPath, rootName);
			return;
		} else {
			log('illegal path to create breadcrumb: ' + path);
		}
	}

	function append(path) {
		// make current active one inactive
		var $lastPath = $filePaths.find('li:last-child');
		setInActive($lastPath);
		
		// append new one and make it active
		var name = getNameFromPath(path);
		addActive(path, name);
	}
	
	function update(path, reset) {
		if (reset) {
			$filePaths.empty();
			create(path, true);
		} else {
			append(path);
		}
	}
	
	function setActive($elem) {
		$elem.nextAll().remove();
		$elem.addClass('active');
		var content = $elem.children('a').text();
		$elem.html(content);
	}
	
	function setInActive($elem) {
		$elem.removeClass('active');
		var name = $elem.text();
		var content = getInActiveWithDivider(name);
		$elem.html(content);
	}
	
	function addActive(id, name) {
		var $path = $('<li class="active">' + name + '</li>');
		$path.data('id', id);
		$filePaths.append($path);
	}
	
	function addInActive(id, name) {
		var $path = $('<li>' + getInActiveWithDivider(name) + '</li>');
		$path.data('id', id);
		$filePaths.prepend($path);
	}
	
	function getInActiveWithDivider(name) {
		return '<a href="#">' + name + '</a>' + '<span class="divider">/</span>';
	}
	
	function getNameFromPath(path) {
		if (path === '/') {
			return '';
		}

		var idx = path.lastIndexOf('/');
		var name;
		if (idx >= 0) {
			name = path.substring(idx + 1);
		} else {
			name = path;
		}
		return name;
	}

	return {
		init: init,
		update: update
	};
})(fileBreadcrumbOptions);

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
	uploadOptions.url = uploadOptions.url + '?detectFileSize=' + detectFileSize;

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
	
	fileTableOptions.loadFolderHandler = fileBreadcrumb.update;
	fileGrid.init();
	
	fileBreadcrumbOptions.clickEventHandler = fileGrid.loadFolder;
	fileBreadcrumb.init();
	
	// setup upload view
	$('#upload-modal').modal('hide').on('hidden', function() {
		fileGrid.loadFiles();
	});
	$('#file-modal').modal('hide').on('hidden', function() {
		//$('#dir-tree').dynatree('destroy');
		$(this).find('.modal-body').empty();
		$(this).off('click', '.confirm');
	});
	
	$('[data-toggle=tooltip]').tooltip({placement:'top'});
	
	// setup menu buttons
	$('#upload-file-btn').click(function() {
		openUploadUI();
	});
	$('#new-folder-btn').click(function() {
		fileGrid.newFolder();
	});
	$('#comment-btn').click(function() {
		comment();
	});
	$('#search-txt').keyup(function(event) {
		event.which === 13 && fileGrid.search($(this));
	});
	$('#search-btn').click(function() {
		fileGrid.search($('#search-txt'));
	});
	
	// ajax get file-upload-related js
	getUploadJs();
});
</script>