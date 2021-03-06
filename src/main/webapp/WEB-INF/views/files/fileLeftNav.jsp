<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div id="leftnav">
	<ul class="nav nav-tabs nav-stacked">
		<li id="leftnav-files"><a href="${service}/metadata"><i class="icon-folder-open"></i> Files<i class="icon-chevron-right"></i></a></li>
		<li id="leftnav-sharing"><a href="${contextPath}/underConstruction"><i class="icon-share"></i> Shares<i class="icon-chevron-right"></i></a></li>
		<li id="leftnav-links"><a href="${contextPath}/underConstruction"><i class="icon-link"></i> Links<i class="icon-chevron-right"></i></a></li>
		<li id="leftnav-trash"><a href="${contextPath}/underConstruction"><i class="icon-trash"></i> Trash<i class="icon-chevron-right"></i></a></li>
	</ul>
</div>
<script>
function highlightLeftNav() {
	$('#leftnav').find('.nav li').removeClass('active');
	var path = window.location.href;
	if (path.indexOf('/metadata') >= 0) {
		$('#leftnav-files').addClass('active');
	} else if (path.indexOf('/shares') >= 0) {
		$('#leftnav-shares').addClass('active');
	} else if (path.indexOf('/links') >= 0) {
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