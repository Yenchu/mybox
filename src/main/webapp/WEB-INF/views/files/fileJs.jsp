<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<script src="//ajax.googleapis.com/ajax/libs/swfobject/2.2/swfobject.js"></script>
<script src="${asset}/dynatree/jquery.dynatree.min.js"></script>
<script src="${asset}/js/bootstrap-contextmenu.js"></script>
<script src="${asset}/js/mustache.js"></script>
<script src="${asset}/bootstrap-pagingtable/bootstrap-pagingtable.js"></script>
<!--[if IE]>
<script>
Placeholders.init();
</script>
<![endif]-->
<script type="text/javascript">
function createTable(tableId, options) {
	options['loadingBarTemplate'] = '<div class="loading-bar"><i class="icon-spinner icon-spin icon-2x"></i></div>';
	var $table = $('#' + tableId).pagingtable(options);
	return $table;
}

var FileTable = (function() {
	
	var FileType = {FOLDER:'folder', FILE:'file'}, Notation = {WRITE:2, READ:4};
	var loadFolderListener = null, rootFolder = null, currentFolder = null;
	var $table = null, options = null, isLoadingSubFolder = false, isLoadingSubFolderInSearch = false, isLoadingFiles = false, isSearching = false, isDragging = false;
	
	function init(options) {
		rootFolder = options.rootFolder;
		currentFolder = options.currentFolder;
	}

	function createFileTable() {
		if (isSearching) {
			$table.pagingtable('destroy');
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
				{name:'name', header:'Name', sortable:true, formatter:formatFileNameColumn, editable:true},
				{name:'isDir', header:'Type', sortable:true, formatter:formatFileTypeColumn},
				{name:'size', header:'Size', sortable:true, formatter:formatSizeColumn},
				{name:'modified', header:'Modified', sortable:true}
			],
			inlineEditing: true,
			isMultiSelect: true,
			loadOnce: true,
			remote: {url:'${service}/metadata', editUrl:'${service}/fileops/rename', deleteUrl:'${service}/fileops/delete'
				, params:{folder:currentFolder}, method:'POST'}
		};
		$table = createTable(options, dblClickRow);
	}
	
	function createSearchTable(query) {
		if (isSearching) {
			var options = $table.pagingtable('getOptions');
			options.remote.params = {folder:currentFolder, query:query};
			reload();
			return;
		}
		
		$table.pagingtable('destroy');
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
				{name:'name', header:'Name', sortable:true, formatter:formatFileNameColumn, editable:true},
				{name:'isDir', header:'Type', sortable:true, hidden:true, formatter:formatFileTypeColumn},
				{name:'location', header:'Location', sortable:true},
				{name:'modified', header:'Modified', sortable:true}
			],
			inlineEditing: true,
			isMultiSelect: true,
			loadOnce: true,
			remote: {url:'${service}/search', editUrl:'${service}/fileops/rename', deleteUrl:'${service}/fileops/delete'
				, params:{folder:currentFolder, query:query}, method:'POST'}
		};
		$table = createTable(options, dblClickRowInSearch);
	}
	
	function createTable(options, dbClickRowHandler) {
		options['loadingBarTemplate'] = '<div class="loading-bar"><i class="icon-spinner icon-spin icon-2x"></i></div>';
		return $('#file-table').off('loaded').on('loaded', function(e) {
			postload();
			enableDragAndDrop();
			enableContextMenu();
		}).pagingtable(options).off('dblclickRow').on('dblclickRow', function(e) {
			var rowId = e.rowId;
			dbClickRowHandler(rowId);
		}).off('remoteLoadError').on('remoteLoadError', function(e) {
			var resp = $.parseJSON(e.jqXHR.responseText);
			error(resp.message || 'Loading data failed.');
		}).off('add update').on('add update', function(e) {
			var $form = e.form;
			var newFileName = $form.find('[name="name"]').val();
			if (!validateFileName(newFileName)) {
				e.preventDefault();
			}
			var id = $form.find('[name="id"]').val();
			if (!id || id == '0') {
				$form.attr('action', '${service}/fileops/create_folder');
			}
		}).off('added updated').on('added updated', function(e) {
			var rowId = e.rowId, rowData = $table.pagingtable('getRowData', rowId), newFileName = rowData.name;
			var msg = (!rowId || rowId == '0' ? 'Creating folder ' + newFileName + ' success.' : 'Renaming to ' + newFileName + ' success.');
			success(msg);
		}).off('addError updateError').on('addError updateError', function(e) {
			var rowId = e.rowId, rowData = $table.pagingtable('getRowData', rowId), newFileName = rowData.name;
			var msg = (!rowId || rowId == '0' ? 'Creating folder ' + newFileName + ' failed.' : 'Renaming to ' + newFileName + ' failed.');
			error(msg, NotifyType.ERROR);
		});
	}
	
	function formatFileNameColumn(colValue, rowData) {
		return getFileNameWithIcon(colValue, rowData);
	}
	
	function getFileNameWithIcon(fileName, rowData) {
		//* use dynatree css ti display icons
		var rt = null;
		if (isFolder(rowData)) {
			rt = '<div class="dynatree-ico-cf"><span class="dynatree-icon"></span> ' + fileName + '</div>';
		} else {
			var thumbExists = rowData.thumbExists;
			if (thumbExists === true) {
				rt = '<div><img src="${service}/thumbnails' + rowData.path + '" width="16" height="16"/> ' + fileName + '</div>';
			} else {
				rt = '<div><span class="dynatree-icon"></span> ' + fileName + '</div>';
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
		return $table.pagingtable('getSelectedRowId');
	}
	
	function getSelectedRowIds() {
		return $table.pagingtable('getSelectedRowIds');
	}
	
	function getSelectedRowData() {
		return $table.pagingtable('getSelectedRowData');
	}
	
	function getRowData(rowId) {
		return $table.pagingtable('getRowData', rowId);
	}

	function dblClickRow(rowId) {
		var rowData = getRowData(rowId);
		if (isFolder(rowData)) {
			isLoadingSubFolder = true;
			var folder = rowData.path;
			loadFolder(folder);
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
			if (loadFolderListener) {
				var reset = (isLoadingSubFolderInSearch ? true : false);
				loadFolderListener(currentFolder, reset);
			}
			isLoadingSubFolder = false;
			isLoadingSubFolderInSearch = false;
		}
		isLoadingFiles = false;
	}
	
	function reload() {
		$table.pagingtable('loadRemoteData');
		isLoadingFiles = true;
	}
	
	function loadFiles() {
		var options = $table.pagingtable('getOptions');
		options.remote.params = {folder:currentFolder};
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
			error('Download folder is not allowed!');
			return;
		}
		
		var url = '${service}/files';
		location.href = url + rowData.path;
	}

	function newFolder() {
		$table.pagingtable('addRow', {id:'0', name:'new folder', isDir:true});
	}

	function renameFile() {
		var rowId = getSelectedRowId();
		$table.pagingtable('updateRow', rowId);
	}

	function validateFileName(fileName) {
		if (fileName === '') {
			error('File name can not be empty!');
			return false;
		}
		
		var pattern = /[\\\/:*?\"<>|]/;       // invalid chars: \/:*?"<>|
		var result = pattern.test(fileName);
		if (result) {
			error('\/:*?"<>| is invalid!');
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
				data: {'folder': rootFolder}
			},
			keyboard: false,
			minExpandLevel: 2,
			clickFolderMode: 1, // 1:activate, 2:expand, 3:activate and expand
			onLazyRead: function(node) {
				node.appendAjax({
					url: '${service}/dirtree',
					data: {'folder': node.data.id}
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
		$modal.off('hidden').on('hidden', function() {
			$('#dir-tree').dynatree('destroy');
			$(this).find('.modal-body').empty();
			$(this).off('click', '.confirm');
		});
		$modal.modal('show');
	}

	function doMoveFiles(srcFiles, destFolder) {
		operateFile('Moving', '${service}/fileops/move', {srcFiles:srcFiles, destFolder:destFolder});
	}

	function doCopyFiles(srcFiles, destFolder) {
		operateFile('Copying', '${service}/fileops/copy', {srcFiles:srcFiles, destFolder:destFolder});
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
			error(opeName + ' operation failed.');
		});
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
			success(action + ' ' + successFiles + ' success.');
		}
		if (failFiles != '') {
			var positionOrder = (successFiles != '' ? 1 : 0);
			error(action + ' ' + failFiles + ' failed.', positionOrder);
		}
	}
	
	function deleteFiles() {
		var $modal = $('#file-modal');
		var title = 'Delete File(s)';
		$modal.find('.modal-header h3').html(title);
		
		var body = '<div id="delete-files"></div>';
		$modal.find('.modal-body').html(body);
		var selFiles = getSelectedRowIds();
		var toDelFiles = getToBeDeletedFiles(selFiles);
		$('#delete-files').html(toDelFiles);

		$modal.attr('disabled', false).off('click', '.confirm').on('click', '.confirm', function() {
			$(this).attr('disabled', true);
			operateFile('Deleting', '${service}/fileops/delete', {files:selFiles});
		});
		$modal.modal('show');
	}
	
	function getToBeDeletedFiles(rowIds) {
		var content = '<table class="table table-bordered">';
		for (var i = 0, len = rowIds.length; i < len; i++) {
			var rowId = rowIds[i];
			var rowData = $table.pagingtable('getRowData', rowId);
			var name = rowData.name;
			content+= '<tr><td>' + getFileNameWithIcon(name, rowData) + '</td></tr>';
		}
		content += '</table>';
		return content;
	}
	
	function getRevisions() {
		var rowData = getSelectedRowData();
		$.ajax({
			url: '${service}/revisions' + rowData.path
		}).done(function(resp) {
			displayRevisions(rowData, resp);
		}).fail(function() {
			error('Getting revisions failed.');
		});
	}
	
	function displayRevisions(rowData, respPage) {
		var fileName = rowData.name;
		var $modal = $('#file-modal');
		var title = 'Version history of "' + fileName + '"';
		$modal.find('.modal-header h3').html(title);
		
		var body = '<form id="restore-form"></form>';
		$modal.find('.modal-body').html(body);
		$('#restore-form').html(respPage);

		$modal.attr('disabled', false).off('click', '.confirm').on('click', '.confirm', function() {
			restoreFile(rowData);
		});
		$modal.modal('show');
	}
	
	function restoreFile(rowData) {
		$.ajax({
			url: '${service}/restore' + rowData.path,
			data: $('#restore-form').serialize(),
			type: 'POST'
		}).always(function() {
			$('#file-modal').modal('hide');
			loadFiles();
		}).done(function(resp) {
			success('File ' + rowData.name + ' restored.');
		}).fail(function() {
			error('Restoring file failed.');
		});
	}
	
	function linkFile() {
		var rowData = getSelectedRowData();
		$.ajax({
			url: '${service}/shares' + rowData.path,
			type: 'POST'
		}).done(function(resp) {
			location.href = resp.url;
		}).fail(function() {
			error('Linking file failed.');
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
	
	function enableDragAndDrop() {
		var selRowData = [];
		var $draggableRows = $table.find('tbody tr td:first-child');
		$draggableRows.draggable({
			delay: 200,  // this will prevent the dragging action when user only wants to click and select items
			cursor: 'move',
			opacity: 0.75,
			scope: 'files',
			appendTo: '#table-container', // is needed for IE
			revert: 'invalid',
			helper: function() {
				selRowData = $table.pagingtable('getMultiSelectedRowData') || [];
				var content = [];
				if (selRowData.length > 0) {
					for (var i = 0, len = selRowData.length; i < len; i++) {
						var rowData = selRowData[i];
						var name = rowData.name;
						content[i] = getFileNameWithIcon(name, rowData);
					}
				} else {
					var id = $(this).parent('tr').attr('id');
					var rowData = $table.pagingtable('getRowData', id);
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
				$table.pagingtable('clearSelectedRow');
			},
			stop: function() {
				isDragging = false;
				selRowData = [];
			}
		});
		
		$table.find('tbody tr').droppable({
			accept: $draggableRows,
			scope: 'files',
			tolerance: 'pointer',
			addClasses: false,
			over: function(event, ui) {
				var dropRowId = $(event.target).attr('id');
				var rowData = $table.pagingtable('getRowData', dropRowId);
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
				if ($table.pagingtable('isBlur', event)) {
					$('#dest-info').hide();
					$('#dest-folder').empty();
				}
				console.log($table.pagingtable('isBlur', event.originalEvent) + ': ' + event.originalEvent.pageX + ', ' + event.originalEvent.pageY);
			},
			drop: function(event, ui) {
				var dropRowId = $(event.target).attr('id');
				var rowData = $table.pagingtable('getRowData', dropRowId);
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
		$table.find('tbody tr').each(function() {
			$(this).attr('data-toggle', 'context').attr('data-target', '#file-context-menu');
			$(this).contextmenu().on('contextmenu', function(e) {
				if (isDragging) {
					return false;
				}
				
				var rowId = $(this).attr('id');
				var selIds = $table.pagingtable('getSelectedRowIds');
				if (selIds.length > 1) {
					for (var i = 0, len = selIds.length; i < len; i++) {
						if (rowId === selIds[i]) {
							configContextMenu4MultiSelect(selIds);
							return;
						}
					}
				}
				$table.pagingtable('clearSelectedRow').pagingtable('selectRow', $table.pagingtable('getRow', rowId));
				configContextMenu(rowId);
			});
		});
	}

	function configContextMenu(rowId) {
		var rowData = getSelectedRowData();
		if (isFolder(rowData)) {
			$('#act-download').hide();
			$('#act-share').show();
			$('#act-link').show();
			$('#act-rename').show();
			$('#act-revision').hide();
		} else {
			$('#act-download').show();
			$('#act-share').hide();
			$('#act-link').show();
			$('#act-rename').show();
			$('#act-revision').show();
		}
	}

	function configContextMenu4MultiSelect(rowIds) {
		$('#act-download').hide();
		$('#act-share').hide();
		$('#act-link').hide();
		$('#act-rename').hide();
		$('#act-revision').hide();
	}

	function initContextMenuBtns() {
		$('#act-download').click(function() {
			downloadFile();
		});
		$('#act-share').click(function() {
			getShares();
		});
		$('#act-link').click(function() {
			linkFile();
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
	
	return function(options) {
		init(options);
		createFileTable();
		initContextMenuBtns();
		
		return {
			isLoading: function() {
				return isLoadingFiles;
			},
			getCurrentFolder: function() {
				return currentFolder;
			},
			setLoadFolderListener: function(listener) {
				loadFolderListener = listener;
			},
			loadFiles: loadFiles,
			loadFolder: loadFolder,
			newFolder: newFolder,
			search: search
		};
	};
})();

var FileBreadcrumb = (function() {

	var $breadcrumb = null, fileTable = null, rootPath = null, rootName = null, initPath = null;
	
	function init(options) {
		$breadcrumb = $('#file-paths'), fileTable = options.fileTable, rootPath = options.rootPath, rootName = options.rootName, initPath = options.initPath;
		
		// if the last char is '/', remove it
		var path = initPath;
		var idx = path.lastIndexOf('/');
		if ((idx === path.length - 1) && (path.length > 1)) {
			path = path.substring(0, idx);
		}
		
		create(path, true);
		
		$breadcrumb.on('click', 'li', function(event) {
			// ignore when file grid is loading 
			if (fileTable.isLoading()) {
				return;
			}
			
			// ignore clicking the active one
			if ($(this).hasClass('active')) {
				return;
			}

			var id = $(this).data('id');
			fileTable.loadFolder(id);
			
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
		var $lastPath = $breadcrumb.find('li:last-child');
		setInActive($lastPath);
		
		// append new one and make it active
		var name = getNameFromPath(path);
		addActive(path, name);
	}
	
	function update(path, reset) {
		if (reset) {
			$breadcrumb.empty();
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
		$breadcrumb.append($path);
	}
	
	function addInActive(id, name) {
		var $path = $('<li>' + getInActiveWithDivider(name) + '</li>');
		$path.data('id', id);
		$breadcrumb.prepend($path);
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

	return function(options) {
		init(options);
		return {
			update: update
		};
	};
})();
</script>