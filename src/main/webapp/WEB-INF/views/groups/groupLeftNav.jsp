<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div id="leftnav">
	<ul id="group-list" class="nav nav-list sidenav">
	</ul>
</div>
<script>
function listGroups() {
	$.ajax({
		url: '${service}/groups',
		type: 'POST'
	}).done(function(resps) {
		showGroups(resps);
	}).fail(function() {
		//notify('List group failed.', NotifyType.ERROR);
		error('List group failed.');
	});
}
function showGroups(groups) {
	var currSpaceId = '${space.id}';
	var content = '';
	for (var i = 0, len = groups.length; i < len; i++) {
		var group = groups[i];
		var id = group.id;
		var name = group.name;
		var href = '${service}/metadata?space=' + id;
		var isActive = id === currSpaceId ? ' class="active"' : '';
		content += '<li' + isActive + '><a href="' + href + '">' + name + '</a></li>';
	}
	if (content !== '') {
		$('#group-list').append(content);
	}
}
$(function() {
	listGroups();
});
</script>