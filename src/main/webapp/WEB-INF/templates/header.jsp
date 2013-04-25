<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div id="topnav" class="navbar navbar-inverse navbar-static-top">
	<div class="navbar-inner">
		<div class="container-fluid">
			<a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
			</a>
			<a class="brand" href="#">File Cruiser</a>
			<div class="nav-collapse collapse">
				<ul class="nav">
					<li id="topnav-home"><a href="${service}/">Home</a></li>
					<li id="topnav-events"><a href="${contextPath}/events">Events</a></li>
					<li id="topnav-apps"><a href="${contextPath}/apps">Apps</a></li>
					<li id="topnav-settings"><a href="${contextPath}/settings">Settings</a></li>
					<li id="topnav-account" class="dropdown">
						<a class="dropdown-toggle" data-toggle="dropdown" href="#">Account<b class="caret"></b></a>
						<ul class="dropdown-menu">
							<li><a href="${contextPath}/account/profile">My Profile</a></li>
							<li><a href="${contextPath}/account/password">Change Password</a></li>
						</ul>
					</li>
					<li><a href="${contextPath}/logout">Logout</a></li>
				</ul>
				<div class="dropdown pull-right">
					<i class="icon-user"></i> ${user.name}
				</div>
			</div>
		</div>
	</div>
</div>
<script>
function highlightTopNav() {
	$('#topnav').find('.nav li').removeClass('active');
	var path = window.location.href;
	if (path.indexOf('${service}') >= 0) {
		$('#topnav-home').addClass('active');
	} else if (path.indexOf('/events') >= 0) {
		$('#topnav-events').addClass('active');
	} else if (path.indexOf('/apps') >= 0) {
		$('#topnav-apps').addClass('active');
	} else if (path.indexOf('/settings') >= 0) {
		$('#topnav-settings').addClass('active');
	} else if (path.indexOf('/account') >= 0) {
		$('#topnav-account').addClass('active');
	} else {
		$('#topnav-home').addClass('active');
	}
}
$(function() {
	<c:choose>
		<c:when test="${notice.error}">
			error('${notice.message}');
		</c:when>
		<c:when test="${notice.info}">
			info('${notice.message}');
		</c:when>
		<c:when test="${notice.success}">
			success('${notice.message}');
		</c:when>
		<c:when test="${notice.warning}">
			warning('${notice.message}');
		</c:when>
	</c:choose>
	highlightTopNav();
});
</script>