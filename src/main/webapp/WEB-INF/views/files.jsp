<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class="row-fluid">
	<div class="span7">
		<ul id="file-paths" class="breadcrumb" style="background-color:transparent"></ul>
	</div>
	<div class="span5">
		<div class="pull-right">
			<span id="file-oper-btns" class="btn-group">
				<button type="button" id="upload-file-btn" class="btn btn-info"><i class="icon-upload icon-white"></i> Upload File</button>
				<button type="button" id="new-folder-btn" class="btn btn-info"><i class="icon-folder-close icon-white"></i> New Folder</button>
			</span>
			<span id="search-oper-btns" class="input-append">
				<input type="text" id="search-txt" name="query" class="input-small" placeholder="Search">
				<span id="search-btn" class="add-on" style="cursor:pointer"><i class="icon-search"></i></span>
			</span>
		</div>
	</div>
</div>
<div class="row-fluid">
<div class="span12">
<div id="grid-container">
	<table id="file-grid"></table>
	<table id="search-grid"></table>
	<div id="file-context-menu" style="display: none">
		<ul>
			<li id="act-download"><a href="#"><i class="icon-download-alt"></i> Download</a></li>
			<li id="act-link"><a href="#"><i class="icon-share"></i> Share link</a></li>
			<li id="act-rename"><a href="#"><i class="icon-pencil"></i> Rename</a></li>
			<li id="act-delete"><a href="#"><i class="icon-trash"></i> Delete</a></li>
			<li id="act-move"><a href="#"><i class="icon-move"></i> Move</a></li>
			<li id="act-copy"><a href="#"><i class="icon-book"></i> Copy</a></li>
			<li id="act-revision"><a href="#"><i class="icon-list"></i> Revisions</a></li>
		</ul>
	</div>
</div>
<div id="upload-modal" class="modal hide fade" style="background-color:#f5f5f5">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h4><i class="icon-upload"></i> Upload File</h4>
	</div>
	<div class="modal-body">
		<div id="fileupload">
			<div id="fileupload-files"></div><br>
			<div id="fileupload-progress" class="hide"></div>
		</div>
	</div>
	<div class="modal-footer" style="background-color:transparent">
		<div id="fileupload-buttonbar" class="pull-right">
			<!-- The fileinput-button span is used to style the file input field as button -->
			<span id="add-file-btn" class="btn btn-info btn-small fileinput-button">
				<input id="upload-files" type="file" name="files[]" multiple>
				<i class="icon-plus icon-white"></i>Add File
			</span>
			<span id="add-folder-btn" class="btn btn-info btn-small fileinput-button hide">
				<input type="file" name="folders[]" multiple directory webkitdirectory mozdirectory>
				<i class="icon-plus icon-white"></i>Add Folder
			</span>
			<button id="cancel-upload-btn" type="reset" class="btn btn-warning btn-small hide">
				<i class="icon-ban-circle icon-white"></i>
				<span>Cancel Upload</span>
			</button>
			<button id="close-upload-btn" type="button" class="btn btn-inverse btn-small" data-dismiss="modal" aria-hidden="true">
				<i class="icon-remove icon-white"></i>
				<span>Close</span>
			</button>
		</div>
	</div>
</div>
<div id="dir-modal" class="modal hide fade" style="background-color:#f5f5f5">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h4><i class="icon-folder-open"></i> <span class="title"></span></h4>
	</div>
	<div class="modal-body">
		<div id="dir-tree"></div>
	</div>
	<div class="modal-footer" style="background-color:transparent">
		<div class="pull-right">
		<button id="select-folder-btn" type="button" class="btn btn-primary btn-small">Select</button>
		<button type="button" class="btn btn-small" data-dismiss="modal" aria-hidden="true">Close</button>
		</div>
	</div>
</div>
<div id="delete-modal" class="modal hide fade" style="background-color:#f5f5f5">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h4><i class="icon-trash"></i> Delete File(s)</h4>
	</div>
	<div class="modal-body">
		<div id="delete-files"></div>
	</div>
	<div class="modal-footer" style="background-color:transparent">
		<div class="pull-right">
		<button id="delete-file-btn" type="button" class="btn btn-primary btn-small">Delete</button>
		<button type="button" class="btn btn-small" data-dismiss="modal" aria-hidden="true">Close</button>
		</div>
	</div>
</div>
<div id="revision-modal" class="modal hide fade" style="background-color:#f5f5f5">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h4><i class="icon-list"></i> <span class="title"></span></h4>
	</div>
	<div class="modal-body">
		<form id="restore-form"></form>
	</div>
	<div class="modal-footer" style="background-color:transparent">
		<div class="pull-right">
		<button id="restore-btn" type="button" class="btn btn-primary btn-small">Restore</button>
		<button type="button" class="btn btn-small" data-dismiss="modal" aria-hidden="true">Close</button>
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

var fileOptions = {loadFolderHandler:null};

var fileBreadcrumbOptions = {initPath:currentFolder, rootPath:rootFolder, rootName:spaceName, clickEventHandler:null};

var uploadOptions = {url:'${service}/files', space:spaceId};

var isLoadingFiles = false;

var fileGrid = (function(options) {
	
	var FileType = {FOLDER:'folder', FILE:'file'};

	var $grid = null;
	
	var isSearching = false, isDragging = false, isLoadingSubFolder = false, isLoadingSubFolderInSearch = false;

	function createFileGrid() {
		var url = '${service}/metadata';
		var editurl = '${service}/fileops/edit';
		var postData = {space:spaceId, folder:currentFolder};
		var colNames = ['ID', 'Path', 'ThumbExists', 'Name', 'Type', 'Size', 'Modified'];
		var colModel = [
		    {name:'id', index:'id', hidden:true, title:false},
			{name:'path', index:'path', hidden:true, title:false},
			{name:'thumbExists', index:'thumbExists', hidden:true, title:false},
			{name:'name', index:'name', title:false, width:100, editable:true,
				editoptions: getNameEditoptions(),
				formatter: formatFileNameColumn,
	        	unformat: unformatFileNameFromColumn
			},
			{name:'isDir', index:'isDir', title:false, width:50, 
				formatter: formatFileTypeColumn,
	        	unformat: unformatFileTypeColumn
			},
			{name:'size', index:'size', title:false, width:50,
				formatter: function(value, options, rowData) {
					if (isFolder(rowData)) {
						return '';
					}
					if (value) {
						return value;
					} else {
						return '';
					}
				}
			},
			{name:'modified', index:'modified', title:false, width:100}
		];
		var sortname = 'type';
		var sortorder = 'desc';
		var options = {loadonce:true, mtype:'POST', url:url, postData:postData, editurl:editurl, colNames:colNames, colModel:colModel, 
				sortname:sortname, sortorder:sortorder, multiselect:true, beforeProcessing:beforeProcessing, gridComplete:gridComplete, 
				beforeSelectRow:beforeSelectRow, ondblClickRow:ondblClickRow};

		if (isSearching) {
			$grid.jqGrid('GridUnload');
			isSearching = false;
			$('#search-oper-btns').children(':last-child').remove();
			$('#file-oper-btns').show();
		}
		
		$grid = $('#file-grid');
		createJqGrid('file-grid', '', options);
		addContextMenuEventToGrid();
	}
	
	function createSearchGrid(query) {
		if (isSearching) {
			var params = $grid.jqGrid('getGridParam');
			params.postData = {space:spaceId, folder:currentFolder, query:query};
			reload(params);
			return;
		}
		isSearching = true;
		
		var url = '${service}/search';
		var editurl = '${service}/fileops/edit';
		var postData = {space:spaceId, folder:currentFolder, query:query};
		var colNames = ['ID', 'Path', 'ThumbExists', 'Type', 'Name', 'Location', 'Modified'];
		var colModel = [
		    {name:'id', index:'id', hidden:true, title:false},
			{name:'path', index:'path', hidden:true, title:false},
			{name:'thumbExists', index:'thumbExists', hidden:true, title:false},
			{name:'isDir', index:'isDir', hidden:true, title:false},
			{name:'name', index:'name', title:false, width:100, editable:true,
				editoptions: getNameEditoptions(),
				formatter: formatFileNameColumn,
	        	unformat: unformatFileNameFromColumn
			},
			{name:'location', index:'location', title:false, width:100},
			{name:'modified', index:'modified', title:false, width:100}
		];
		var sortname = 'name';
		var sortorder = 'desc';
		var options = {loadonce:true, mtype:'POST', url:url, postData:postData, editurl:editurl, colNames:colNames, colModel:colModel, 
				sortname:sortname, sortorder:sortorder, multiselect:true, beforeProcessing:beforeProcessing, gridComplete:gridComplete, 
				beforeSelectRow:beforeSelectRow, ondblClickRow:ondblClickRowInSearch};
		
		$grid.jqGrid('GridUnload');
		$grid = $('#search-grid');
		createJqGrid('search-grid', '', options);
		addContextMenuEventToGrid();
		
		$('#file-oper-btns').hide();
		var $disableSearchBtn = $('<span id="disable-search-btn" class="add-on" style="cursor:pointer"><i class="icon-remove"></i></span>');
		$('#search-oper-btns').append($disableSearchBtn);
		$('#disable-search-btn').on('click', function() {
			createFileGrid();
		});
	}
	
	function getNameEditoptions() {
		return {dataEvents: [
			{	type: 'blur', 
				fn: function(e) {
					restoreFiles();
				}
			},
			{	type: 'keyup', 
				fn: function(e) {
					var key = e.charCode || e.keyCode;
					if (key === 13) { // Enter key
						var newValue = $(e.target).val();
						saveFile(newValue);
					} else if (key === 27) { // Esc key
						restoreFiles();
					}
				}
			}
		]};
	}
	
	function formatFileNameColumn(cellvalue, options, rowData) {
		var rt = getFileNameWithIcon(cellvalue, rowData);
		return rt;
	}
	
	function getFileNameWithIcon(fileName, rowData) {
		//* use dynatree css ti display icons
		var rt = null;
		if (isFolder(rowData)) {
			rt = '<div class="dynatree-ico-cf"><span class="dynatree-icon"></span> ' + fileName + '</div>';
		} else {
			var thumbExists = rowData.thumbExists;
			if (thumbExists === true) {
				rt = '<div><img src="${service}/thumbnails/' + spaceId + rowData.path + '" width="16" height="16"/> ' + fileName + '</div>';
			} else {
				rt = '<div><span class="dynatree-icon"></span> ' + fileName + '</div>';
			}
		}
		return rt;
	}

	function unformatFileNameFromColumn(cellvalue) {
		//* there is an extra space before filename!
		var filename = cellvalue.substr(1);
		return filename;
	}
	
	function formatFileTypeColumn(cellvalue, options, rowData) {
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
	
	function unformatFileTypeColumn(cellvalue) {
		if (cellvalue === FileType.FOLDER) {
			return true;
		}
		return false;
	}
	
	function beforeProcessing(data) {
		// load all records instead of paging
		var params = $grid.jqGrid('getGridParam');
		params.rowNum = data.rows.length;
		$grid.jqGrid('setGridParam', params);
	}
	
	function gridComplete() {
		if (isLoadingSubFolder || isLoadingSubFolderInSearch) {
			if (options.loadFolderHandler) {
				var reset = (isLoadingSubFolderInSearch ? true : false);
				options.loadFolderHandler(currentFolder, reset);
			}
			isLoadingSubFolder = false;
			isLoadingSubFolderInSearch = false;
		}
		
		// disable multiselect checkbox column when enabling multiselect option
		$grid.jqGrid('hideCol', 'cb');
		
		//* hideCol will cause grid to shrink width, reset its width
		var refWidth = $('#file-paths').parents('.row-fluid').width();
		$grid.setGridWidth(refWidth, true);
		
		enableDragAndDrop();
		
		isLoadingFiles = false;
	}
	
	function enableDragAndDrop() {
		var dragRowIds = [];
		var dragRowNames = [];

		//* using td not tr to avoid grid rows resizing width.
		var $draggableRows = $grid.find('.ui-row-ltr td');
		$draggableRows.draggable({
			delay: 200,  // this will prevent the dragging action when user only wants to click and select items
			cursor: 'move',
			opacity: 0.75,
			scope: 'files',
			appendTo: '#grid-container', // is needed for IE
			revert: 'invalid',
			helper: function() {
				//* jqGrid creates a checkbox column when enabling multiselect option
				var selectRowElems = $('.ui-row-ltr input:checked').parents('tr');
				var content = [];
				dragRowIds = [];
				dragRowNames = [];
				
				if (selectRowElems.length > 0) {
					for (var i = 0, len = selectRowElems.length; i < len; i++) {
						var selectRowElem = selectRowElems[i];
						var rowId = $(selectRowElem).attr('id');
						var rowData = $grid.jqGrid('getRowData', rowId);
						var name = rowData.name;
						
						content[i] = getFileNameWithIcon(name, rowData);
						dragRowIds.push(rowId);
						dragRowNames.push(name);
					}
				} else {
					var selectRowElem = $(this).parent('tr');
					var rowId = selectRowElem.attr('id');
					var rowData = $grid.jqGrid('getRowData', rowId);
					var name = rowData.name;
					
					content[0] = getFileNameWithIcon(name, rowData);
					dragRowIds.push(rowId);
					dragRowNames.push(name);
				}
				
				//* separating selected rows by <br/> may block the last row to be dropped.
				var container = $('<div class="box"></div>');
				container.html(content.join(''));
				container.append('<div id="dest-info" class="hide"><i class="icon-arrow-right"></i> move to <span id="dest-folder" class="label label-info"></span></div>');
				return container;
			},
			start: function(event, ui) {
				//* just for closing context menu if it is showing
				$(document).trigger('click');

				//* just for disabling context menu
				isDragging = true;
				
				// clear selecting highlight
				$grid.jqGrid('resetSelection');
			},
			stop: function() {
				isDragging = false;
				
				// clear cached data
				dragRowNames = [];
				dragRowIds = [];
			}
		});
		
		$grid.find('.ui-row-ltr').droppable({
			accept: $draggableRows,
			scope: 'files',
			tolerance: 'pointer',
			addClasses: false,
			over: function(event, ui) {
				var dropRowId = $(event.target).attr('id');
				var rowData = $grid.jqGrid('getRowData', dropRowId);
				if (!isFolder(rowData)) {
					$('#dest-info').hide();
					$('#dest-folder').empty();
					return;
				}
				
				for (var i = 0, len = dragRowIds.length; i < len; i++) {
					var dragRowId = dragRowIds[i];
					if (dragRowId === dropRowId) {
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
				if (isEventOutOfGrid(event)) {
					$('#dest-info').hide();
					$('#dest-folder').empty();
				}
			},
			drop: function(event, ui) {
				var dropRowId = $(event.target).attr('id');
				var rowData = $grid.jqGrid('getRowData', dropRowId);
				if (!isFolder(rowData)) {
					return;
				}
				
				for (var i = 0, len = dragRowIds.length; i < len; i++) {
					var dragRowId = dragRowIds[i];
					if (dragRowId === dropRowId) {
						return;
					}
				}
				
				var dropRowName = rowData.name;
				doMoveFiles(dragRowIds, dropRowId, dragRowNames, dropRowName);
			}
		});
	}

	function beforeSelectRow(rowId, event) {
		if (event.ctrlKey) {
			return true;
		} else if (event.shiftKey) {
			var lastSelRowId = $grid.jqGrid('getGridParam', 'selrow');
			$grid.jqGrid('resetSelection');
			
			var ids = $grid.jqGrid('getDataIDs');
			var enabledSelect = false;
			for (var i = 0, len = ids.length; i < len; i++) {
				var id = ids[i];
				if (id === lastSelRowId) {
					$grid.jqGrid('setSelection', id);
					if (enabledSelect) {
						break;
					} else {
						enabledSelect = true;
						continue;
					}
				}
				if (id === rowId) {
					$grid.jqGrid('setSelection', id);
					if (enabledSelect) {
						break;
					} else {
						enabledSelect = true;
						continue;
					}
				}
				if (enabledSelect) {
					$grid.jqGrid('setSelection', id);
					continue;
				}
			}
			return false;
		} else {
			$grid.jqGrid('resetSelection');
			return true;
		}
	}

	function ondblClickRow(rowId) {
		var rowData = $grid.jqGrid('getRowData', rowId);
		if (isFolder(rowData)) {
			isLoadingSubFolder = true;
			var folderPath = rowData.path;
			loadFolder(folderPath)
		} else {
			downloadFile(rowData);
		}
	}
	
	function ondblClickRowInSearch(rowId) {
		var rowData = $grid.jqGrid('getRowData', rowId);
		if (isFolder(rowData)) {
			isLoadingSubFolderInSearch = true;
			currentFolder = rowData.path;
			createFileGrid();
		} else {
			downloadFile(rowData);
		}
	}
	
	function isEventOutOfGrid(event) {
		var offset = $grid.offset();
		var width = $grid.width();
		var height = $grid.height();
		var xmin = offset.left, ymin = offset.top, xmax = xmin + width, ymax = ymin + height;
		if ((event.pageX < xmin) || (event.pageX > xmax) || (event.pageY < ymin) || (event.pageY > ymax)) {
			return true;
		} else {
			return false;
		}
	}
	
	function getSelectRowData() {
		var rowId = $grid.jqGrid('getGridParam', 'selrow');
		var rowData = $grid.jqGrid('getRowData', rowId);
		return rowData;
	}
	
	function clearSelection() {
		$grid.jqGrid('resetSelection');
	}
	
	function reload(newOptions) {
		isLoadingFiles = true;
		
		var params = $grid.jqGrid('getGridParam');
		if (newOptions) {
			params = newOptions;
		}
		
		//* if loadonce=true, jqGrid sets datatype=local, set datatype=json to reloads grid remotely
		params.datatype = 'json';
		$grid.jqGrid('setGridParam', params).trigger('reloadGrid');
	}

	function loadFiles() {
		var params = $grid.jqGrid('getGridParam');
		params.postData.folder = currentFolder;
		reload(params);
	}

	function loadFolder(folderId) {
		if (folderId) {
			currentFolder = folderId;
		}
		loadFiles();
	}

	function restoreFiles() {
		// just reloads grid locally not remotely
		$grid.trigger('reloadGrid');
	}
	
	function isFolder(rowData) {
		var isDir = rowData.isDir;
		if (isDir === true || isDir === 'true') {
			return true;
		} else {
			return false;
		}
	}

	function downloadFile(rowData) {
		if (!rowData) {
			rowData = getSelectRowData();
			if (isFolder(rowData)) {
				notify('Download folder is not allowed!', NotifyType.ERROR);
				return;
			}
		}
		var url = '${service}/files';
		location.href = url + '/' + spaceId + rowData.path;
	}

	function newFolder() {
		$grid.jqGrid('addRow', {rowID:'0', initdata:{name:'new folder', isDir:true}});
	}

	function renameFile() {
		var rowId = $grid.jqGrid('getGridParam', 'selrow');
		$grid.jqGrid('editRow', rowId);
	}

	function saveFile(newFileName) {
		if (!validateFileName(newFileName)) {
			return false;
		}
		
		var rowId = $grid.jqGrid('getGridParam', 'selrow');
		$grid.jqGrid('saveRow', rowId, {
			url: '${service}/fileops/edit',
			extraparam: {space:spaceId, folder:currentFolder},
			successfunc: function(response) {
				var msg = (rowId === '0' ? 'Creating folder ' + newFileName + ' success.' : "Renaming to " + newFileName + ' success.');
				notify(msg, NotifyType.SUCCESS);
				loadFiles();
				return true;
			},
			errorfunc: function(id, error) {
				var msg = (rowId === '0' ? 'Creating folder ' + newFileName + ' failed.' : "Renaming to " + newFileName + ' failed.');
				notify(msg, NotifyType.ERROR);
				loadFiles();
			}
		});
	}

	function validateFileName(fileName) {
		if (fileName === '') {
			notify('File name can not be empty!', NotifyType.ERROR);
			return false;
		}
		
		var pattern = /[\\\/:*?\"<>|]/; // invalid chars: \/:*?"<>|
		var result = pattern.test(fileName);
		if (result) {
			notify('\/:*?"<>| is invalid!', NotifyType.ERROR);
			return false;
		}
		
		//* UI can't validate duolicate file name in searching grid
		/*if (!isSearching) {
			var rowDataList = $grid.jqGrid('getRowData');
			for (var i = 0, len = rowDataList.length; i < len; i++) {
				var rowData = rowDataList[i];
				var name = rowData.name;
				if (fileName === name) {
					alert('File name ' + fileName + ' is duplicate!');
					return false;
				}
			}
		}*/
		return true;
	}

	function moveFiles() {
		var srcFiles = $grid.jqGrid('getGridParam', 'selarrrow');
		mvOrCpFiles(srcFiles, false);
	}

	function copyFiles() {
		var srcFiles = $grid.jqGrid('getGridParam', 'selarrrow');
		mvOrCpFiles(srcFiles, true);
	}

	function mvOrCpFiles(srcFiles, isCopying) {
		$('#dir-tree').dynatree({
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
		
		var $dirModal = $('#dir-modal');
		var title = isCopying ? 'Copy File(s) to' : 'Move File(s) to';
		$dirModal.find('.title').html(title);
		$dirModal.modal('show');
		
		$('#select-folder-btn').attr('disabled', false).off('click').on('click', function() {
			var node = $('#dir-tree').dynatree('getActiveNode');
			if (node) {
				$(this).attr('disabled', true);
				var destFolder = node.data.id;
				isCopying ? doCopyFiles(srcFiles, destFolder) : doMoveFiles(srcFiles, destFolder);
			}
		});
	}

	function doMoveFiles(srcFiles, destFolder) {
		$.ajax({
			url: '${service}/fileops/move',
			data: {space:spaceId, srcFiles:srcFiles, destFolder:destFolder},
			type: 'POST'
		}).always(function() {
			$('#dir-modal').modal('hide');
			loadFiles();
		}).done(function(resps) {
			notifyFileOperationResult('Moving', resps);
		}).fail(function() {
			notify('Moving operation failed.', NotifyType.ERROR);
		});
	}

	function doCopyFiles(srcFiles, destFolder) {
		$.ajax({
			url: '${service}/fileops/copy',
			data: {space:spaceId, srcFiles:srcFiles, destFolder:destFolder},
			type: 'POST'
		}).always(function() {
			$('#dir-modal').modal('hide');
			loadFiles();
		}).done(function(resps) {
			notifyFileOperationResult('Copying', resps);
		}).fail(function() {
			notify('Copying operation failed.', NotifyType.ERROR);
		});
	}

	function deleteFiles() {
		var selFiles = $grid.jqGrid('getGridParam', 'selarrrow');
		var toDelFiles = displayToBeDeletedFiles(selFiles);
		$('#delete-files').html(toDelFiles);

		$('#delete-modal').modal('show');
		
		$('#delete-file-btn').attr('disabled', false).off('click').on('click', function(event) {
			$(this).attr('disabled', true);
			doDeleteFiles(selFiles);
		});
	}
	
	function displayToBeDeletedFiles(rowIds) {
		var content = '<table class="table table-bordered">'
		for (var i = 0, len = rowIds.length; i < len; i++) {
			var rowId = rowIds[i];
			var rowData = $grid.jqGrid('getRowData', rowId);
			var name = rowData.name;
			content+= '<tr><td>' + getFileNameWithIcon(name, rowData) + '</td></tr>';
		}
		content += '</table>';
		return content;
	}
	
	function doDeleteFiles(files) {
		$.ajax({
			url: '${service}/fileops/delete',
			data: {space:spaceId, files:files},
			type: 'POST'
		}).always(function() {
			$('#delete-modal').modal('hide');
			loadFiles();
		}).done(function(resps) {
			notifyFileOperationResult('Deleting', resps);
		}).fail(function() {
			notify('Deleting operation failed.', NotifyType.ERROR);
		});
	}
	
	function getRevisions() {
		var rowData = getSelectRowData();
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
		$('#restore-form').html(respPage);
		
		var $revisionModal = $('#revision-modal');
		$revisionModal.find('.title').html('Version history of "' + fileName + '"');
		$revisionModal.modal('show');

		$('#restore-btn').attr('disabled', false).off('click').on('click', function() {
			$(this).attr('disabled', true);
			restoreFile();
		});
	}
	
	function restoreFile() {
		$.ajax({
			url: '${service}/restore',
			data: $('#restore-form').serialize(),
			type: 'POST'
		}).always(function() {
			$('#revision-modal').modal('hide');
			loadFiles();
		}).done(function(resp) {
			notify('File ' + resp.name + ' restored.', NotifyType.SUCCESS);
		}).fail(function() {
			notify('Restoring file failed.', NotifyType.ERROR);
		});
	}
	
	function shareLink() {
		var rowData = getSelectRowData();
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
		
		createSearchGrid(query);
		$searchTxt.val('');
	}
	
	function notifyFileOperationResult(action, resps) {
		var successFiles = '';
		var failFiles = '';
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

	function addContextMenuEventToGrid() {
		$grid.contextMenu('file-context-menu', {
			bindings: {
				'act-download': function(trigger, currentTarget) {
					downloadFile();
				},
				'act-link': function(trigger, currentTarget) {
					shareLink();
				},
				'act-rename': function(trigger, currentTarget) {
					renameFile();
				},
				'act-delete': function(trigger, currentTarget) {
					deleteFiles();
				},
				'act-move': function(trigger, currentTarget) {
					moveFiles();
				},
				'act-copy': function(trigger, currentTarget) {
					copyFiles();
				},
				'act-revision': function(trigger, currentTarget) {
					getRevisions();
				}
			},
			onContextMenu: function(event) {
				if (isDragging) {
					return false;
				}
				
				var rowId = $(event.target).closest('tr.jqgrow').attr('id');
				if (!rowId) {
					return false;
				}
				// if right click occurs in selected rows, edit those rows
				var selRows = $grid.jqGrid('getGridParam', 'selarrrow');
				if (selRows.length > 1) {
					for (var i = 0, len = selRows.length; i < len; i++) {
						if (rowId === selRows[i]) {
							configContextMenu4MultiSelect(selRows);
							return true;
						}
					}
				}
				
				// if right click doesn't occur in selected rows, deselected those rows and only edit the right-clicked row
				$grid.jqGrid('resetSelection');
				$grid.jqGrid('setSelection', rowId);
				configContextMenu(rowId);
				return true;
			}
		});
	}

	function configContextMenu(rowId) {
		var rowData = $grid.jqGrid('getRowData', rowId);
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
	
	return {
		init: function() {
			createFileGrid();
		},
		isFocusout: isEventOutOfGrid,
		clearSelection: clearSelection,
		loadFiles: loadFiles,
		loadFolder: loadFolder,
		newFolder: newFolder,
		search: search
	};
})(fileOptions);

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
		return '<a href="#">' + name + '</a>' + '<span class="divider">/</span>'
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
	var detectFileSize = false
	if ($.browser.msie) {
		if (swfobject.hasFlashPlayerVersion("9.0.0")) {
			useFlash = true;
			detectFileSize = true;
		}
	} else {
		detectFileSize = true;
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
	}
	
	// to disable the default browser action for file drops on the document
	$(document).bind('drop dragover', function(event) {
		event.preventDefault();
	});
	
	fileGrid.init();
	$(document).click(function(event) {
		if (fileGrid.isFocusout(event)) {
			fileGrid.clearSelection();
		}
	});
	
	fileBreadcrumbOptions.clickEventHandler = fileGrid.loadFolder;
	fileBreadcrumb.init();
	fileOptions.loadFolderHandler = fileBreadcrumb.update;
	
	// setup upload view
	$('#upload-modal').modal('hide').on('hidden', function() {
		fileGrid.loadFiles();
	});
	
	$('#delete-modal').modal('hide').on('hidden', function() {
		$('#delete-files').empty();
	});
	$('#dir-modal').modal('hide').on('hidden', function() {
		$('#dir-tree').dynatree('destroy');
	});
	$('#restore-form').modal('hide').on('hidden', function() {
		$('#restore-form').empty();
	});
	
	// setup menu buttons
	$('#upload-file-btn').click(function() {
		openUploadUI();
	});
	$('#new-folder-btn').click(function() {
		fileGrid.newFolder();
	});
	$('#search-txt').keyup(function(event) {
		if (event.which === 13) {
			fileGrid.search($(this));
		}
	});
	$('#search-btn').click(function() {
		fileGrid.search($('#search-txt'));
	});
	
	// ajax get file-upload-related js
	getUploadJs();
});
</script>