<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div id="leftnav">
	<ul class="nav nav-list sidenav">
		<li id="leftnav-files"><a href="${service}/metadata"><i class="icon-folder-open"></i> Files</a></li>
		<li id="leftnav-links"><a href="${service}/link"><i class="icon-tags"></i> Links</a></li>
		<li id="leftnav-trash"><a href="${service}/trash"><i class="icon-trash"></i> Trash</a></li>
	</ul>
</div>
<script>
function highlightLeftNav() {
	$('#leftnav').find('.nav li').removeClass('active');
	var path = window.location.href;
	if (path.indexOf('/metadata') >= 0) {
		$('#leftnav-files').addClass('active');
	} else if (path.indexOf('/link') >= 0) {
		$('#leftnav-links').addClass('active');
	} else if (path.indexOf('/trash') >= 0) {
		$('#leftnav-trash').addClass('active');
	} else {
		$('#leftnav-files').addClass('active');
	}
}
$(function() {
	highlightLeftNav();
});
</script>