<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class="row-fluid">
	<div class="span12">
	<h4>Groups</h4>
	</div>
</div>
<div class="row-fluid">
	<div class="span12">
	<table id="group-grid"></table>
	</div>
</div>
<script>
var groupGrid = (function() {
	
	var $grid = null;
	
	function createGrid() {
		var url = '${service}/groups';
		var colNames = ['ID', 'Name', 'Manager', 'Quota', 'Number of User', 'Expiration', 'Activation', 'Notification', 'Portal Url', 'Description'];
		var colModel = [
		    {name:'id', index:'id', hidden:true, title:false},
			{name:'name', index:'name', title:false},
			{name:'manager', index:'manager', title:false},
			{name:'quota', index:'quota', title:false},
			{name:'numberOfUser', index:'numberOfUser', title:false},
			{name:'expiryDate', index:'expiryDate', title:false},
			{name:'activation', index:'activation', title:false},
			{name:'notification', index:'notification', title:false},
			{name:'portalUrl', index:'portalUrl', title:false},
			{name:'description', index:'description', title:false}
		];
		var sortname = 'name';
		var sortorder = 'desc';
		var options = {loadonce:true, mtype:'POST', url:url, colNames:colNames, colModel:colModel, 
				sortname:sortname, sortorder:sortorder, ondblClickRow:ondblClickRow};
		
		createJqGrid('group-grid', '', options);
		$grid = $('#group-grid');
	}
	
	function ondblClickRow(rowId) {
		var rowData = $grid.jqGrid('getRowData', rowId);
		var spaceId = rowData.id;
		location.href = '${service}/metadata?space=' + spaceId;
	}
	
	return {
		init: function() {
			createGrid();
		}
	};
})();

$(function() {
	groupGrid.init();
});
</script>